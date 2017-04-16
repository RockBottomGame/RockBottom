package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.gui.container.ContainerChest;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.entity.TileEntityChest;

public class GuiChest extends GuiContainer{

    private final TileEntityChest tile;

    public GuiChest(EntityPlayer player, TileEntityChest tile){
        super(player, 198, 150);
        this.tile = tile;
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

    }

    @Override
    public void onClosed(Game game){
        super.onClosed(game);
        this.tile.openCount--;
    }
}
