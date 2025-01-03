package com.iafenvoy.uranus.forge.component;

import com.iafenvoy.uranus.Uranus;
import com.iafenvoy.uranus.network.PacketBufferUtils;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilitySyncHelper {
    public static final Identifier CAPABILITY_SYNC = new Identifier(Uranus.MOD_ID, "capability_sync");
    private static final List<LivingCapabilityHolder<? extends ICapabilityProvider, ? extends ITickableCapability>> LIVINGS = new ArrayList<>();
    private static final List<PlayerCapabilityHolder<? extends ICapabilityProvider, ? extends ITickableCapability>> PLAYERS = new ArrayList<>();

    public static <P extends ICapabilityProvider, S extends ITickableCapability> void registerForLiving(Identifier id, Capability<S> capability, Function<LivingEntity, P> constructor) {
        LIVINGS.add(new LivingCapabilityHolder<>(id, capability, constructor));
    }

    public static <P extends ICapabilityProvider, S extends ITickableCapability> void registerForPlayer(Identifier id, Capability<S> capability, Function<PlayerEntity, P> constructor) {
        registerForPlayer(id, capability, constructor, CopyPolicy.KEEP_INVENTORY);
    }

    public static <P extends ICapabilityProvider, S extends ITickableCapability> void registerForPlayer(Identifier id, Capability<S> capability, Function<PlayerEntity, P> constructor, CopyPolicy copyPolicy) {
        PLAYERS.add(new PlayerCapabilityHolder<>(id, capability, constructor, copyPolicy));
    }

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof LivingEntity living)
            for (LivingCapabilityHolder<?, ?> holder : LIVINGS)
                if (!living.getCapability(holder.capability).isPresent())
                    event.addCapability(holder.id, holder.constructor.apply(living));
        if (event.getObject() instanceof PlayerEntity player)
            for (PlayerCapabilityHolder<?, ?> holder : PLAYERS)
                if (!player.getCapability(holder.capability).isPresent())
                    event.addCapability(holder.id, holder.constructor.apply(player));
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity serverPlayer)
            for (PlayerCapabilityHolder<?, ?> holder : PLAYERS)
                serverPlayer.getCapability(holder.capability).resolve().ifPresent(storage -> NetworkManager.sendToPlayer(serverPlayer, CAPABILITY_SYNC, PacketBufferUtils.create().writeIdentifier(holder.id).writeNbt(storage.serializeNBT())));
    }

    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity living = event.getEntity();
        for (LivingCapabilityHolder<?, ? extends ITickableCapability> holder : LIVINGS) {
            Optional<? extends ITickableCapability> optional = living.getCapability(holder.capability).resolve();
            if (optional.isEmpty()) continue;
            ITickableCapability capability = optional.get();
            capability.tick();
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        for (PlayerCapabilityHolder<?, ? extends ITickableCapability> holder : PLAYERS) {
            Optional<? extends ITickableCapability> optional = player.getCapability(holder.capability).resolve();
            if (optional.isEmpty()) continue;
            ITickableCapability capability = optional.get();
            capability.tick();
            if (capability.isDirty() && player instanceof ServerPlayerEntity serverPlayer)
                NetworkManager.sendToPlayer(serverPlayer, CAPABILITY_SYNC, PacketBufferUtils.create().writeIdentifier(holder.id).writeNbt(capability.serializeNBT()));
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerEntity origin = event.getOriginal(), player = event.getEntity();
        for (PlayerCapabilityHolder<?, ? extends ITickableCapability> holder : PLAYERS) {
            if (event.isWasDeath() && !holder.copyPolicy.shouldCopyOnDeath(player.getEntityWorld())) continue;
            Optional<? extends ITickableCapability> originalCapability = origin.getCapability(holder.capability).resolve();
            Optional<? extends ITickableCapability> newCapability = player.getCapability(holder.capability).resolve();
            if (originalCapability.isEmpty() || newCapability.isEmpty()) continue;
            newCapability.get().deserializeNBT(originalCapability.get().serializeNBT());
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
                        player.getCapability(holder.capability).resolve().ifPresent(x -> x.deserializeNBT(compound));
                }));
            });
        }
    }

    public static class LivingCapabilityHolder<P extends ICapabilityProvider, S extends ITickableCapability> {
        private final Identifier id;
        private final Capability<S> capability;
        private final Function<LivingEntity, P> constructor;

        public LivingCapabilityHolder(Identifier id, Capability<S> capability, Function<LivingEntity, P> constructor) {
            this.id = id;
            this.capability = capability;
            this.constructor = constructor;
        }
    }

    public static class PlayerCapabilityHolder<P extends ICapabilityProvider, S extends ITickableCapability> {
        private final Identifier id;
        private final Capability<S> capability;
        private final Function<PlayerEntity, P> constructor;
        private final CopyPolicy copyPolicy;

        public PlayerCapabilityHolder(Identifier id, Capability<S> capability, Function<PlayerEntity, P> constructor, CopyPolicy copyPolicy) {
            this.id = id;
            this.capability = capability;
            this.constructor = constructor;
            this.copyPolicy = copyPolicy;
        }
    }

    public enum CopyPolicy {
        ALWAYS, KEEP_INVENTORY, NEVER;

        public boolean shouldCopyOnDeath(World world) {
            if (world.getGameRules().getBoolean(GameRules.KEEP_INVENTORY))
                return this != NEVER;
            return this == ALWAYS;
        }
    }
}
