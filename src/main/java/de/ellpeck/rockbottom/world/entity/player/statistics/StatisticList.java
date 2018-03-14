package de.ellpeck.rockbottom.world.entity.player.statistics;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.statistics.NumberStatistic;
import de.ellpeck.rockbottom.api.entity.player.statistics.TileStatistic;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public final class StatisticList{

    public static final IResourceName TILES_BROKEN_TOTAL = RockBottomAPI.createInternalRes("tiles_broken_total");
    public static final IResourceName TILES_PLACED_TOTAL = RockBottomAPI.createInternalRes("tiles_placed_total");
    public static final IResourceName CONTAINERS_OPENED = RockBottomAPI.createInternalRes("containers_opened");
    public static final IResourceName TILES_BROKEN_PER_TILE = RockBottomAPI.createInternalRes("tiles_broken_per_tile");
    public static final IResourceName TILES_PLACED_PER_TILE = RockBottomAPI.createInternalRes("tiles_placed_per_tile");

    public static void init(){
        new NumberStatistic(TILES_BROKEN_TOTAL).register();
        new NumberStatistic(TILES_PLACED_TOTAL).register();
        new NumberStatistic(CONTAINERS_OPENED).register();
        new TileStatistic(TILES_BROKEN_PER_TILE).register();
        new TileStatistic(TILES_PLACED_PER_TILE).register();
    }
}
