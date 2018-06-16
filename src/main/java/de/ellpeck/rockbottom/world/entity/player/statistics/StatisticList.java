package de.ellpeck.rockbottom.world.entity.player.statistics;

import de.ellpeck.rockbottom.api.entity.player.statistics.ItemStatistic;
import de.ellpeck.rockbottom.api.entity.player.statistics.NumberStatistic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public final class StatisticList{

    public static final ResourceName CONTAINERS_OPENED = ResourceName.intern("containers_opened");
    public static final ResourceName TILES_BROKEN = ResourceName.intern("tiles_broken");
    public static final ResourceName TILES_PLACED = ResourceName.intern("tiles_placed");
    public static final ResourceName SECONDS_PLAYED = ResourceName.intern("seconds_played");
    public static final ResourceName DEATHS = ResourceName.intern("deaths");
    public static final ResourceName TILES_WALKED = ResourceName.intern("tiles_walked");
    public static final ResourceName TOOLS_BROKEN = ResourceName.intern("tools_broken");

    public static void init(){
        new NumberStatistic(CONTAINERS_OPENED, CONTAINERS_OPENED.addPrefix("stats.")).register();
        new ItemStatistic(TILES_BROKEN, TILES_BROKEN.addPrefix("stats.")).register();
        new ItemStatistic(TILES_PLACED, TILES_PLACED.addPrefix("stats.")).register();
        new TimeStatistic(SECONDS_PLAYED, SECONDS_PLAYED.addPrefix("stats.")).register();
        new NumberStatistic(DEATHS, DEATHS.addPrefix("stats.")).register();
        new NumberStatistic(TILES_WALKED, TILES_WALKED.addPrefix("stats.")).register();
        new NumberStatistic(TOOLS_BROKEN, TOOLS_BROKEN.addPrefix("stats.")).register();
    }
}
