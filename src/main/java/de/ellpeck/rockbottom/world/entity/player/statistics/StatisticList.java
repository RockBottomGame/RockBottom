package de.ellpeck.rockbottom.world.entity.player.statistics;

import de.ellpeck.rockbottom.api.entity.player.statistics.ItemStatistic;
import de.ellpeck.rockbottom.api.entity.player.statistics.NumberStatistic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public final class StatisticList{

    public static final ResourceName CONTAINERS_OPENED = ResourceName.intern("containers_opened");
    public static final ResourceName TILES_BROKEN = ResourceName.intern("tiles_broken");
    public static final ResourceName TILES_PLACED = ResourceName.intern("tiles_placed");

    public static void init(){
        new NumberStatistic(CONTAINERS_OPENED, ResourceName.intern("stats.containers_opened")).register();
        new ItemStatistic(TILES_BROKEN, ResourceName.intern("stats.tiles_broken")).register();
        new ItemStatistic(TILES_PLACED, ResourceName.intern("stats.tiles_placed")).register();
    }
}
