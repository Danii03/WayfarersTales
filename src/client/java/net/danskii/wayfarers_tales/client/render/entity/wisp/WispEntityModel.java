package net.danskii.wayfarers_tales.client.render.entity.wisp;

import net.danskii.wayfarers_tales.Wayfarers_tales;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.math.MathHelper;

public class WispEntityModel extends EntityModel<WispEntityRenderState> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(Wayfarers_tales.id("wisp"), "main");

    private final ModelPart body;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public WispEntityModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.leftWing = root.getChild("left_wing");
        this.rightWing = root.getChild("right_wing");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        root.addChild(
                "body",
                ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new Dilation(0.0F))
                        .uv(17, 0).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.origin(0.0F, 16.0F, 0.0F)
        );
        root.addChild(
                "left_wing",
                ModelPartBuilder.create()
                        .uv(0, 9).cuboid(0.0F, -5.0F, 0.0F, 4.0F, 9.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.origin(2.0F, 16.0F, 0.0F)
        );
        root.addChild(
                "right_wing",
                ModelPartBuilder.create()
                        .uv(9, 9).cuboid(-4.0F, -5.0F, 0.0F, 4.0F, 9.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.origin(-2.0F, 16.0F, 0.0F)
        );

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public void setAngles(WispEntityRenderState state) {
        super.setAngles(state);

        float cycle = state.age * 0.38F;
        float wingBeat = MathHelper.sin(cycle) * 0.75F;
        float bodyPulse = 1.1F + MathHelper.sin(cycle * 0.5F) * 0.1F;

        this.body.xScale = bodyPulse;
        this.body.yScale = bodyPulse;
        this.body.zScale = bodyPulse;

        this.leftWing.yaw = -0.35F - wingBeat;
        this.leftWing.roll = 0.12F + wingBeat * 0.25F;
        this.rightWing.yaw = 0.35F + wingBeat;
        this.rightWing.roll = -0.12F - wingBeat * 0.25F;
    }
}
