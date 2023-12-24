package aqario.deathwriter.common;

import aqario.deathwriter.common.entity.DeathwriterEntityType;
import aqario.deathwriter.common.network.DeathwriterMessages;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deathwriter implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Deathwriter");
    public static final String ID = "deathwriter";

    @Override
    public void onInitialize(ModContainer mod) {
        LOGGER.info("Loading {}", mod.metadata().name());
        DeathwriterEntityType.init();
        DeathwriterMessages.init();
    }
}
