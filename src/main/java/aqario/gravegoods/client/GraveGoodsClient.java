package aqario.gravegoods.client;

import aqario.gravegoods.client.model.GraveEntityModel;
import aqario.gravegoods.client.render.GraveEntityRenderer;
import aqario.gravegoods.common.entity.GraveGoodsEntityType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class GraveGoodsClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(GraveGoodsEntityType.GRAVE, GraveEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GraveEntityModel.DEFAULT_LAYER, GraveEntityModel::createDefaultLayer);
        EntityModelLayerRegistry.registerModelLayer(GraveEntityModel.PLAYER_LAYER, GraveEntityModel::createPlayerLayer);
    }
}
