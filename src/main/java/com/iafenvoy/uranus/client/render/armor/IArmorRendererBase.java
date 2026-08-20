package com.iafenvoy.uranus.client.render.armor;

import com.iafenvoy.uranus.Uranus;
import net.minecraft.client.model.Model;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Client armor-model hook backed by NeoForge's per-item client extensions.
 *
 * <p>The old entity argument is intentionally absent: 26.1 extracts an immutable
 * render state before layer submission. Per-entity animation belongs in the model's
 * {@link Model#setupAnim(Object)} implementation.</p>
 */
public interface IArmorRendererBase {
    Map<Item, IArmorRendererBase> RENDERERS = new IdentityHashMap<>();

    Model getHumanoidArmorModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, Model defaultModel);

    default Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer, Identifier defaultTexture) {
        return defaultTexture;
    }

    static void register(IArmorRendererBase renderer, ItemLike... items) {
        Arrays.stream(items).map(ItemLike::asItem).forEach(item -> RENDERERS.put(item, renderer));
    }

    @EventBusSubscriber(modid = Uranus.MOD_ID, value = Dist.CLIENT)
    final class ClientRegistration {
        private ClientRegistration() {
        }

        @SubscribeEvent
        public static void registerExtensions(RegisterClientExtensionsEvent event) {
            Map<IArmorRendererBase, java.util.List<Item>> grouped = new IdentityHashMap<>();
            RENDERERS.forEach((item, renderer) -> grouped.computeIfAbsent(renderer, ignored -> new java.util.ArrayList<>()).add(item));
            grouped.forEach((renderer, items) -> event.registerItem(renderer.asClientExtension(), items.toArray(Item[]::new)));
        }
    }

    private IClientItemExtensions asClientExtension() {
        return new IClientItemExtensions() {
            @Override
            public Model getHumanoidArmorModel(ItemStack stack, EquipmentClientInfo.LayerType layerType, Model defaultModel) {
                return IArmorRendererBase.this.getHumanoidArmorModel(stack, layerType, defaultModel);
            }

            @Override
            public Identifier getArmorTexture(ItemStack stack, EquipmentClientInfo.LayerType layerType, EquipmentClientInfo.Layer layer, Identifier defaultTexture) {
                return IArmorRendererBase.this.getArmorTexture(stack, layerType, layer, defaultTexture);
            }
        };
    }
}
