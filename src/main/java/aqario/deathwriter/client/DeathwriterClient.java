package aqario.deathwriter.client;

import aqario.deathwriter.client.model.GraveEntityModel;
import aqario.deathwriter.client.render.GraveEntityRenderer;
import aqario.deathwriter.common.entity.DeathwriterEntityType;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;

public class DeathwriterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient(ModContainer mod) {
        EntityRendererRegistry.register(DeathwriterEntityType.GRAVE, GraveEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(GraveEntityModel.LAYER, GraveEntityModel::getTexturedModelData);
    }
}
