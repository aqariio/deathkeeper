package aqario.deathkeeper.common.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class DeathkeeperConfig extends MidnightConfig {
    @Entry
    public static boolean enableGraves = true;
    @Entry
    public static boolean highlightGraves = false;
    @Entry
    public static boolean openOtherGraves = true;
}
