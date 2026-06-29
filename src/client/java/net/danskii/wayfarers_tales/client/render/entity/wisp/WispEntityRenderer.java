package net.danskii.wayfarers_tales.client.render.entity.wisp;

import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.danskii.wayfarers_tales.entity.wisp.WispEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class WispEntityRenderer extends MobEntityRenderer<WispEntity, WispEntityRenderState, WispEntityModel> {
    private static final Identifier[] TEXTURES = {
            Wayfarers_tales.id("textures/entity/wisp/wisp_0.png"),
            Wayfarers_tales.id("textures/entity/wisp/wisp_1.png"),
            Wayfarers_tales.id("textures/entity/wisp/wisp_2.png"),
            Wayfarers_tales.id("textures/entity/wisp/wisp_3.png"),
            Wayfarers_tales.id("textures/entity/wisp/wisp_4.png"),
            Wayfarers_tales.id("textures/entity/wisp/wisp_5.png")
    };
    private static final int MINIMUM_BLOCK_LIGHT = 11;

    public WispEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new WispEntityModel(context.getPart(WispEntityModel.LAYER)), 0.25F);
    }

    @Override
    public WispEntityRenderState createRenderState() {
        return new WispEntityRenderState();
    }

    @Override
    public void updateRenderState(WispEntity entity, WispEntityRenderState state, float tickProgress) {
        super.updateRenderState(entity, state, tickProgress);
        state.variant = entity.getVariant();
        state.wispScale = entity.getWispScale();
    }

    @Override
    public Identifier getTexture(WispEntityRenderState state) {
        return TEXTURES[MathHelper.clamp(state.variant, 0, TEXTURES.length - 1)];
    }

    @Override
    protected void scale(WispEntityRenderState state, MatrixStack matrices) {
        matrices.scale(state.wispScale, state.wispScale, state.wispScale);
    }

    @Override
    protected int getBlockLight(WispEntity entity, BlockPos pos) {
        return Math.max(MINIMUM_BLOCK_LIGHT, super.getBlockLight(entity, pos));
    }
}
