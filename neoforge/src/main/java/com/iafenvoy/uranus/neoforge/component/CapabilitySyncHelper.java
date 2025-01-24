package com.iafenvoy.uranus.neoforge.component;

import com.iafenvoy.uranus.Uranus;
import com.iafenvoy.uranus.network.PacketBufferUtils;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilitySyncHelper {
    public static final Identifier CAPABILITY_SYNC = new Identifier(Uranus.MOD_ID, "capability_sync");
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
                NetworkManager.sendToPlayer(serverPlayer, CAPABILITY_SYNC, PacketBufferUtils.create().writeIdentifier(holder.id).writeNbt(serverPlayer.getData(holder.attachmentType).serializeNBT()));
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        for (AttachmentType<? extends IAttachment> type : LIVINGS)
            if (living.getData(type) instanceof ITickableAttachment tickable)
                tickable.tick();
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        for (PlayerCapabilityHolder<? extends IAttachment> holder : PLAYERS) {
            if (player.getData(holder.attachmentType) instanceof ITickableAttachment attachment) {
                attachment.tick();
                if (attachment.isDirty() && player instanceof ServerPlayerEntity serverPlayer)
                    NetworkManager.sendToPlayer(serverPlayer, CAPABILITY_SYNC, PacketBufferUtils.create().writeIdentifier(holder.id).writeNbt(attachment.serializeNBT()));
            }
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void init(FMLClientSetupEvent event) {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, CAPABILITY_SYNC, (buf, context) -> {
                Identifier id = buf.readIdentifier();
                NbtCompound compound = buf.readNbt();
                PLAYERS.stream().filter(x -> x.id.equals(id)).findFirst().ifPresent(holder -> context.queue(() -> {
                    ClientPlayerEntity player = MinecraftClient.getInstance().player;
                    if (player != null)
                        player.getData(holder.attachmentType).deserializeNBT(compound);
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
