package aqario.deathkeeper.client;

import aqario.deathkeeper.client.model.GraveEntityModel;
import aqario.deathkeeper.client.render.GraveEntityRenderer;
import aqario.deathkeeper.common.entity.DeathkeeperEntityType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class DeathkeeperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(DeathkeeperEntityType.GRAVE, GraveEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GraveEntityModel.LAYER, GraveEntityModel::getTexturedModelData);
    }
}
