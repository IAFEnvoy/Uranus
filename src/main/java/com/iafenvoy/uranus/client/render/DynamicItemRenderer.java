package com.iafenvoy.uranus.client.render;

import com.iafenvoy.uranus.Uranus;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import org.joml.Vector3fc;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Dynamic item renderer registry for item models with type {@code uranus:dynamic}.
 * Item display context is now resolved by the item-model pipeline before submission.
 */
@FunctionalInterface
public interface DynamicItemRenderer {
    Identifier TYPE = Identifier.fromNamespaceAndPath(Uranus.MOD_ID, "dynamic");
    Map<Item, DynamicItemRenderer> RENDERERS = new IdentityHashMap<>();

    void submit(ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean foil, int color);

    final class Renderer implements SpecialModelRenderer<ItemStack> {
        @Override
        public void submit(ItemStack stack, PoseStack poseStack, SubmitNodeCollector collector, int light, int overlay, boolean foil, int color) {
            DynamicItemRenderer renderer = RENDERERS.get(stack.getItem());
            if (renderer != null) renderer.submit(stack, poseStack, collector, light, overlay, foil, color);
        }

        @Override
        public void getExtents(Consumer<Vector3fc> output) {
        }

        @Override
        public ItemStack extractArgument(ItemStack stack) {
            return stack;
        }
    }

    record Unbaked() implements SpecialModelRenderer.Unbaked<ItemStack> {
        public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

        @Override
        public SpecialModelRenderer<ItemStack> bake(SpecialModelRenderer.BakingContext context) {
            return new Renderer();
        }

        @Override
        public MapCodec<Unbaked> type() {
            return MAP_CODEC;
        }
    }

    @EventBusSubscriber(modid = Uranus.MOD_ID, value = Dist.CLIENT)
    final class ClientRegistration {
        private ClientRegistration() {
        }

        @SubscribeEvent
        public static void registerRenderer(RegisterSpecialModelRendererEvent event) {
            event.register(TYPE, Unbaked.MAP_CODEC);
        }
    }
}
