package de.ellpeck.rockbottom.gui.menu.background;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.gui.IMainMenuTheme;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;

public class StoneTheme implements IMainMenuTheme {

    private final int ladderX = Util.RANDOM.nextInt(TILE_AMOUNT);

    @Override
    public TileState getState(int x, int y, TileState[][] grid) {
        if (x == this.ladderX) {
            return GameContent.Tiles.LADDER.getDefState();
        } else {
            switch (Util.RANDOM.nextInt(10)) {
                case 0:
                    return GameContent.Tiles.COAL.getDefState();
                default:
                    return GameContent.Tiles.STONE.getDefState();
            }
        }
    }

    @Override
    public int getBackgroundColor() {
        return Colors.DARK_GRAY;
    }
}
