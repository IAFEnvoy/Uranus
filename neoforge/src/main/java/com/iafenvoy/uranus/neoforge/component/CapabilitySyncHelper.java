package com.iafenvoy.uranus.neoforge.component;

import com.iafenvoy.uranus.Uranus;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class CapabilitySyncHelper {
    public static final Identifier CAPABILITY_SYNC = Identifier.of(Uranus.MOD_ID, "capability_sync");
    private static final List<LivingCapabilityHolder<? extends IAttachment>> LIVINGS = new ArrayList<>();

    public static <T extends IAttachment> void registerForLiving(Identifier id, AttachmentType<T> capability) {
        LIVINGS.add(new LivingCapabilityHolder<>(id, capability));
    }

    @Deprecated(forRemoval = true)
    public static <T extends IAttachment> void registerForLiving(AttachmentType<T> capability) {
    }

    @Deprecated(forRemoval = true)
    public static <T extends IAttachment> void registerForPlayer(Identifier id, AttachmentType<T> capability) {
    }

    @SubscribeEvent
    public static void onLivingTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity living)) return;
        World world = living.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            for (LivingCapabilityHolder<? extends IAttachment> holder : LIVINGS) {
                IAttachment attachment = living.getData(holder.attachmentType);
                if (attachment instanceof ITickableAttachment tickable) {
                    tickable.tick();
                    if (tickable.isDirty())
                        syncToNearbyPlayers(serverWorld, living, holder.id, attachment.serializeNBT());
                }
            }
        }
    }

    private static void syncToNearbyPlayers(ServerWorld world, LivingEntity entity, Identifier id, NbtCompound compound) {
        for (ServerPlayerEntity player : world.getPlayers(x -> x.isAlive() && x.getPos().distanceTo(entity.getPos()) <= 64))
            NetworkManager.sendToPlayer(player, new CapabilitySyncPayload(id, entity.getId(), compound));
    }

    static {
        if (Platform.getEnv() == Dist.DEDICATED_SERVER)
            NetworkManager.registerS2CPayloadType(CapabilitySyncPayload.ID, CapabilitySyncPayload.CODEC);
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void init(FMLClientSetupEvent event) {
            NetworkManager.registerReceiver(NetworkManager.Side.S2C, CapabilitySyncPayload.ID, CapabilitySyncPayload.CODEC, (payload, ctx) -> ctx.queue(() -> {
                        ClientWorld world = MinecraftClient.getInstance().world;
                        if (world == null) return;
                        Entity entity = world.getEntityById(payload.entityId());
                        if (entity == null) return;
                        for (LivingCapabilityHolder<? extends IAttachment> holder : LIVINGS) {
                            if (Objects.equals(holder.id, payload.id())) {
                                entity.getData(holder.attachmentType).deserializeNBT(payload.compound());
                                break;
                            }
                        }
                    })
            );
        }
    }

    public static class LivingCapabilityHolder<T> {
        final Identifier id;
        final AttachmentType<T> attachmentType;

        public LivingCapabilityHolder(Identifier id, AttachmentType<T> attachmentType) {
            this.id = id;
            this.attachmentType = attachmentType;
        }
    }
}