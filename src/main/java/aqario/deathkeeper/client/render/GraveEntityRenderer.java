package aqario.deathkeeper.client.render;

import aqario.deathkeeper.client.model.GraveEntityModel;
import aqario.deathkeeper.common.entity.GraveEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class GraveEntityRenderer extends EntityRenderer<GraveEntity> {
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    private final Map<Boolean, GraveEntityModel> models;
    private GraveEntityModel model;

    public GraveEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.models = bakeModels(context);
        this.model = this.models.get(false);
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    private static Map<Boolean, GraveEntityModel> bakeModels(EntityRendererProvider.Context context) {
        return Map.of(
            false,
            new GraveEntityModel(context.bakeLayer(GraveEntityModel.DEFAULT_LAYER)),
            true,
            new GraveEntityModel(context.bakeLayer(GraveEntityModel.PLAYER_LAYER))
        );
    }

    @Override
    public void render(GraveEntity entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        this.model = this.models.get(this.tryGetOwner(entity) != null);
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        matrices.pushPose();
        matrices.translate(0.0, 1.0, 0.0);
        matrices.scale(0.75F, 0.75F, 0.75F);
        float l = Mth.sin((entity.tickCount + tickDelta) / 10.0F + entity.uniqueOffset) * 0.1F + 0.1F;
        matrices.translate(0.0, l + 0.25F, 0.0);
        matrices.mulPose(Axis.YP.rotation(entity.getRotation(tickDelta)));
        matrices.mulPose(Axis.XP.rotationDegrees(180.0F));
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.model.renderType(this.getTextureLocation(entity)));
        this.model.setupAnim(entity, tickDelta, 0.0F, -0.1F, 0.0F, 0.0F);
        this.model.renderToBuffer(matrices, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.popPose();
    }

    @Override
    protected boolean shouldShowName(GraveEntity entity) {
        return entity.hasCustomName();
    }

    @Nullable
    private LocalPlayer tryGetOwner(GraveEntity entity) {
        ClientLevel world = Minecraft.getInstance().level;
        if(world != null) {
            return (LocalPlayer) world.getPlayerByUUID(entity.getOwnerUuid());
        }
        return null;
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(GraveEntity entity) {
        LocalPlayer player = this.tryGetOwner(entity);
        if(player != null) {
            return player.getSkinTextureLocation();
        }
        return DEFAULT_TEXTURE;
    }
}
