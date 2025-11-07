package aqario.deathkeeper.common;

import aqario.deathkeeper.common.config.DeathkeeperConfig;
import aqario.deathkeeper.common.entity.DeathkeeperEntityType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Deathkeeper implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Deathkeeper");
    public static final String ID = "deathkeeper";

    public static boolean isTrinketsLoaded() {
        return FabricLoader.getInstance().isModLoaded("trinkets");
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Deathkeeper");
        DeathkeeperConfig.init(ID, DeathkeeperConfig.class);
        DeathkeeperEntityType.init();
    }
}
