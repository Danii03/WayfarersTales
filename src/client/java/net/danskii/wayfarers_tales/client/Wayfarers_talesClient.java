package net.danskii.wayfarers_tales.client;

import net.danskii.wayfarers_tales.client.render.entity.wisp.WispEntityModel;
import net.danskii.wayfarers_tales.client.render.entity.wisp.WispEntityRenderer;
import net.danskii.wayfarers_tales.registry.ModBlockRegistry;
import net.danskii.wayfarers_tales.registry.ModEntityRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.BlockRenderLayer;

public class Wayfarers_talesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.putBlock(ModBlockRegistry.SHORTER_GRASS, BlockRenderLayer.CUTOUT);
        ColorProviderRegistry.BLOCK.register((state, world, pos, tintIndex) ->
                        world != null && pos != null ? BiomeColors.getGrassColor(world, pos) : 0x91BD59,
                ModBlockRegistry.SHORTER_GRASS
        );
        EntityModelLayerRegistry.registerModelLayer(WispEntityModel.LAYER, WispEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntityRegistry.WISP, WispEntityRenderer::new);
    }
}
