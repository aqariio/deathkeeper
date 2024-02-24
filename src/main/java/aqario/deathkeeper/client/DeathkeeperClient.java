package aqario.deathkeeper.client;

import aqario.deathkeeper.client.model.GraveEntityModel;
import aqario.deathkeeper.client.render.GraveEntityRenderer;
import aqario.deathkeeper.common.entity.DeathkeeperEntityType;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class DeathkeeperClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(DeathkeeperEntityType.GRAVE, GraveEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GraveEntityModel.LAYER, GraveEntityModel::getTexturedModelData);
    }
}
