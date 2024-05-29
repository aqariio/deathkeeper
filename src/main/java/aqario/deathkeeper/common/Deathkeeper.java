package aqario.deathkeeper.common;

import aqario.deathkeeper.common.config.DeathkeeperConfig;
import aqario.deathkeeper.common.entity.DeathkeeperEntityType;
import aqario.deathkeeper.common.network.DeathkeeperMessages;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deathkeeper implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Deathkeeper");
    public static final String ID = "deathkeeper";

    @Override
    public void onInitialize(ModContainer mod) {
        LOGGER.info("Loading {}", mod.metadata().name());
        DeathkeeperConfig.init(ID, DeathkeeperConfig.class);
        DeathkeeperEntityType.init();
        DeathkeeperMessages.init();
    }
}
