package com.iafenvoy.uranus.client.render;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkullRenderRegistry {
    private static final List<SkullInfoWithModel> SKULL_INFO_WITH_MODEL = new ArrayList<>();
    private static final List<SkullInfoWithLayer> SKULL_INFO_WITH_LAYER = new ArrayList<>();

    public static void register(SkullBlock.SkullType type, Identifier texture, Block... blocks) {
        register(type, texture, EntityModelLayers.ZOMBIE_HEAD, blocks);
    }

    public static void register(SkullBlock.SkullType type, Identifier texture, EntityModelLayer layer, Block... blocks) {
        SKULL_INFO_WITH_LAYER.add(new SkullInfoWithLayer(type, texture, layer, List.of(blocks)));
    }

    public static void register(SkullBlock.SkullType type, Identifier texture, SkullBlockEntityModel model, Block... blocks) {
        SKULL_INFO_WITH_MODEL.add(new SkullInfoWithModel(type, texture, model, List.of(blocks)));
    }

    @ApiStatus.Internal
    public static ImmutableMap<SkullBlock.SkullType, SkullBlockEntityModel> getSkulls(Map<SkullBlock.SkullType, Identifier> textures, EntityModelLoader modelLoader) {
        ImmutableMap.Builder<SkullBlock.SkullType, SkullBlockEntityModel> builder = ImmutableMap.builder();
        for (SkullInfoWithModel info : SKULL_INFO_WITH_MODEL) {
            textures.put(info.type, info.texture);
            builder.put(info.type, info.model);
        }
        for (SkullInfoWithLayer info : SKULL_INFO_WITH_LAYER) {
            textures.put(info.type, info.texture);
            builder.put(info.type, new SkullEntityModel(modelLoader.getModelPart(info.layer)));
        }
        return builder.build();
    }

    @ApiStatus.Internal
    public static boolean supported(Block block) {
        for (SkullInfoWithModel info : SKULL_INFO_WITH_MODEL)
            if (info.blocks.contains(block)) return true;
        for (SkullInfoWithLayer info : SKULL_INFO_WITH_LAYER)
            if (info.blocks.contains(block)) return true;
        return false;
    }

    private record SkullInfoWithModel(SkullBlock.SkullType type, Identifier texture, SkullBlockEntityModel model,
                                      List<Block> blocks) {
    }

    private record SkullInfoWithLayer(SkullBlock.SkullType type, Identifier texture, EntityModelLayer layer,
                                      List<Block> blocks) {
    }
}
