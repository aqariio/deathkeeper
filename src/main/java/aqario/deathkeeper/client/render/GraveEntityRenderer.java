package aqario.deathkeeper.client.render;

import aqario.deathkeeper.client.model.GraveEntityModel;
import aqario.deathkeeper.common.entity.GraveEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class GraveEntityRenderer extends EntityRenderer<GraveEntity> {
    private static final Identifier DEFAULT_TEXTURE = new Identifier("textures/entity/skeleton/skeleton.png");
    private final GraveEntityModel model;

    public GraveEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.model = new GraveEntityModel(ctx.getPart(GraveEntityModel.LAYER));
        this.shadowRadius = 0.15F;
        this.shadowOpacity = 0.75F;
    }

    @Override
    public void render(GraveEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.push();
        matrices.translate(0.0, 1.0, 0.0);
        matrices.scale(0.75F, 0.75F, 0.75F);
        float l = MathHelper.sin((entity.age + tickDelta) / 10.0F + entity.uniqueOffset) * 0.1F + 0.1F;
        matrices.translate(0.0, l + 0.25F, 0.0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(entity.getRotation(tickDelta)));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.getLayer(this.getTexture(entity)));
        this.model.setAngles(entity, tickDelta, 0.0F, -0.1F, 0.0F, 0.0F);
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
    }

    @Override
    protected boolean hasLabel(GraveEntity entity) {
        return entity.hasCustomName();
    }

    @Override
    public Identifier getTexture(GraveEntity entity) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if(world != null) {
            ClientPlayerEntity player = (ClientPlayerEntity) world.getPlayerByUuid(entity.getOwnerUuid());
            if(player != null) {
                return player.getSkinTexture();
            }
        }
        return DEFAULT_TEXTURE;
    }
}
