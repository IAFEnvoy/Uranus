package com.iafenvoy.uranus.neoforge.component;

import com.iafenvoy.uranus.Uranus;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class CapabilitySyncHelper {
    public static final Identifier CAPABILITY_SYNC = Identifier.of(Uranus.MOD_ID, "capability_sync");
    private static final List<AttachmentType<? extends IAttachment>> LIVINGS = new ArrayList<>();
    private static final List<PlayerCapabilityHolder<? extends IAttachment>> PLAYERS = new ArrayList<>();

    public static <T extends IAttachment> void registerForLiving(AttachmentType<T> capability) {
        LIVINGS.add(capability);
    }

    public static <T extends IAttachment> void registerForPlayer(Identifier id, AttachmentType<T> capability) {
        PLAYERS.add(new PlayerCapabilityHolder<>(id, capability));
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity serverPlayer)
            for (PlayerCapabilityHolder<? extends IAttachment> holder : PLAYERS)
                NetworkManager.sendToPlayer(serverPlayer, new CapabilitySyncPayload(holder.id, serverPlayer.getData(holder.attachmentType).serializeNBT()));
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity living)
            for (AttachmentType<? extends IAttachment> type : LIVINGS)
                if (living.getData(type) instanceof ITickableAttachment tickable)
                    tickable.tick();
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        PlayerEntity player = event.getEntity();
        for (PlayerCapabilityHolder<? extends IAttachment> holder : PLAYERS) {
            if (player.getData(holder.attachmentType) instanceof ITickableAttachment attachment) {
                attachment.tick();
                if (attachment.isDirty() && player instanceof ServerPlayerEntity serverPlayer)
                    NetworkManager.sendToPlayer(serverPlayer, new CapabilitySyncPayload(holder.id, attachment.serializeNBT()));
            }
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void init(FMLClientSetupEvent event) {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, CapabilitySyncPayload.ID, CapabilitySyncPayload.CODEC, (payload, ctx) -> {
                PLAYERS.stream().filter(x -> x.id.equals(payload.id())).findFirst().ifPresent(holder -> ctx.queue(() -> {
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null)
                        player.getData(holder.attachmentType).deserializeNBT(payload.compound());
                }));
            });
        }
    }

    public static class PlayerCapabilityHolder<T> {
        private final Identifier id;
        private final AttachmentType<T> attachmentType;

        public PlayerCapabilityHolder(Identifier id, AttachmentType<T> attachmentType) {
            this.id = id;
            this.attachmentType = attachmentType;
        }
    }
}
