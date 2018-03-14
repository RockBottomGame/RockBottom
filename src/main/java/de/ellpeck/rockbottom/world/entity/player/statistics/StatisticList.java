package de.ellpeck.rockbottom.world.entity.player.statistics;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.statistics.NumberStatistic;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.HashMap;
import java.util.Map;

public final class StatisticList{

    public static final Map<Tile, IResourceName> INDIVIDUAL_TILES_PLACED = new HashMap<>();
    public static final Map<Tile, IResourceName> INDIVIDUAL_TILES_BROKEN = new HashMap<>();
    public static final Map<Item, IResourceName> ITEMS_USED_AS_TOOL = new HashMap<>();

    public static final IResourceName TILES_BROKEN = RockBottomAPI.createInternalRes("tiles_broken");
    public static final IResourceName TILES_PLACED = RockBottomAPI.createInternalRes("tiles_placed");
    public static final IResourceName CONTAINERS_OPENED = RockBottomAPI.createInternalRes("containers_opened");
    public static final IResourceName NOTHING_USED_AS_TOOL = RockBottomAPI.createInternalRes("nothing_as_tool");

    public static void init(){
        new NumberStatistic(TILES_BROKEN).register();
        new NumberStatistic(TILES_PLACED).register();
        new NumberStatistic(CONTAINERS_OPENED).register();
        new NumberStatistic(NOTHING_USED_AS_TOOL).register();

        for(Map.Entry<IResourceName, Tile> entry : RockBottomAPI.TILE_REGISTRY.getUnmodifiable().entrySet()){
            IResourceName name = entry.getKey();
            Tile tile = entry.getValue();

            IResourceName broken = name.addSuffix("_broken");
            new NumberStatistic(broken).register();
            INDIVIDUAL_TILES_BROKEN.put(tile, broken);

            IResourceName placed = name.addSuffix("_placed");
            new NumberStatistic(placed).register();
            INDIVIDUAL_TILES_PLACED.put(tile, placed);
        }

        for(Map.Entry<IResourceName, Item> entry : RockBottomAPI.ITEM_REGISTRY.getUnmodifiable().entrySet()){
            IResourceName name = entry.getKey();
            Item item = entry.getValue();

            IResourceName tool = name.addSuffix("_as_tool");
            new NumberStatistic(tool).register();
            ITEMS_USED_AS_TOOL.put(item, tool);
        }
    }
}
