package de.ellpeck.rockbottom.world.gen.feature.trees;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.StaticTileProps.LogType;
import de.ellpeck.rockbottom.api.tile.state.TileState;

import java.util.HashMap;
import java.util.Map;

public final class TreeDesigns{

    public static final String[][] DESIGNS = new String[][]{
            {
                    " LLL ",
                    "LLLLL",
                    " LLL ",
                    " q^p ",
                    "  T  ",
                    " <v> "
            },
            {
                    "  L  ",
                    " LLL ",
                    "LLLLL",
                    " LLL ",
                    "  ^  ",
                    "  T  ",
                    "  T  ",
                    " <v> "
            },
            {
                    "  LLL  ",
                    " LLLLL ",
                    "LLLLLLL",
                    " LLLLL ",
                    "  q^p  ",
                    "   T   ",
                    "  <v>  "
            }
    };
    public static final Map<Character, TileState> STATE_MAP = new HashMap<>();

    static{
        STATE_MAP.put('L', GameContent.TILE_LEAVES.getDefState());

        STATE_MAP.put('q', GameContent.TILE_LOG.getDefState().prop(StaticTileProps.LOG_VARIANT, LogType.BRANCH_LEFT));
        STATE_MAP.put('p', GameContent.TILE_LOG.getDefState().prop(StaticTileProps.LOG_VARIANT, LogType.BRANCH_RIGHT));
        STATE_MAP.put('^', GameContent.TILE_LOG.getDefState().prop(StaticTileProps.LOG_VARIANT, LogType.TRUNK_TOP));
        STATE_MAP.put('T', GameContent.TILE_LOG.getDefState().prop(StaticTileProps.LOG_VARIANT, LogType.TRUNK_MIDDLE));
        STATE_MAP.put('v', GameContent.TILE_LOG.getDefState().prop(StaticTileProps.LOG_VARIANT, LogType.TRUNK_BOTTOM));
        STATE_MAP.put('<', GameContent.TILE_LOG.getDefState().prop(StaticTileProps.LOG_VARIANT, LogType.ROOT_LEFT));
        STATE_MAP.put('>', GameContent.TILE_LOG.getDefState().prop(StaticTileProps.LOG_VARIANT, LogType.ROOT_RIGHT));
    }
}
