package aqario.deathwriter.client.model;

import aqario.deathwriter.common.Deathwriter;
import aqario.deathwriter.common.entity.GraveEntity;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class GraveEntityModel extends EntityModel<GraveEntity> {
	public static final EntityModelLayer LAYER = new EntityModelLayer(new Identifier(Deathwriter.ID, "grave"), "main");
	private final ModelPart head;

	public GraveEntityModel(ModelPart root) {
		this.head = root.getChild("head");
	}

	public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

		modelPartData.addChild("head", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 24.0F, 0.0F));

		return TexturedModelData.of(modelData, 64, 32);
	}

    @Override
    public void setAngles(GraveEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        head.render(matrices, vertices, light, overlay, red, green, blue, alpha);
    }
}