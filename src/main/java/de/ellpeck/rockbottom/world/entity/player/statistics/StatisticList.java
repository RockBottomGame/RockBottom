package de.ellpeck.rockbottom.world.entity.player.statistics;

import de.ellpeck.rockbottom.api.entity.player.statistics.NumberStatistic;
import de.ellpeck.rockbottom.api.entity.player.statistics.TileStatistic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

public final class StatisticList{

    public static final ResourceName TILES_BROKEN_TOTAL = ResourceName.intern("tiles_broken_total");
    public static final ResourceName TILES_PLACED_TOTAL = ResourceName.intern("tiles_placed_total");
    public static final ResourceName CONTAINERS_OPENED = ResourceName.intern("containers_opened");
    public static final ResourceName TILES_BROKEN_PER_TILE = ResourceName.intern("tiles_broken_per_tile");
    public static final ResourceName TILES_PLACED_PER_TILE = ResourceName.intern("tiles_placed_per_tile");

    public static void init(){
        new NumberStatistic(TILES_BROKEN_TOTAL).register();
        new NumberStatistic(TILES_PLACED_TOTAL).register();
        new NumberStatistic(CONTAINERS_OPENED).register();
        new TileStatistic(TILES_BROKEN_PER_TILE).register();
        new TileStatistic(TILES_PLACED_PER_TILE).register();
    }
}
