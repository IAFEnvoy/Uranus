package com.iafenvoy.uranus.fabric.mixin;

import com.iafenvoy.uranus.client.render.armor.IArmorTextureProvider;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(ArmorFeatureRenderer.class)
public abstract class ArmorFeatureRendererMixin<T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    @Shadow
    protected abstract boolean usesInnerModel(EquipmentSlot slot);

    public ArmorFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @ModifyExpressionValue(method = "renderArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ArmorMaterial$Layer;getTexture(Z)Lnet/minecraft/util/Identifier;"))
    private Identifier pushTexture(Identifier original,
                                   @Local(ordinal = 0) ItemStack itemStack,
                                   @Local(ordinal = 0, argsOnly = true) T entity,
                                   @Local(ordinal = 0, argsOnly = true) EquipmentSlot armorSlot,
                                   @Local(ordinal = 0) ArmorMaterial.Layer layer
    ) {
        if (itemStack.getItem() instanceof IArmorTextureProvider provider)
            return provider.getArmorTexture(itemStack, entity, armorSlot, layer, this.usesInnerModel(armorSlot));
        return original;
    }
}
