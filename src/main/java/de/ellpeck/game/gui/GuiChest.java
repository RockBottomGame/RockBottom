package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.entity.TileEntityChest;

public class GuiChest extends GuiContainer{

    private final TileEntityChest tile;

    public GuiChest(EntityPlayer player, TileEntityChest tile){
        super(player, 158, 150);
        this.tile = tile;
    }

    @Override
    public void initGui(Game game){
        super.initGui(game);

        this.addSlotGrid(this.tile.inventory, 0, this.tile.inventory.getSlotAmount(), this.guiLeft-20, this.guiTop, 10);
        this.addPlayerInventory(this.guiLeft, this.guiTop+60);
    }

    @Override
    public void onClosed(Game game){
        super.onClosed(game);
        this.tile.openCount--;
    }
}
