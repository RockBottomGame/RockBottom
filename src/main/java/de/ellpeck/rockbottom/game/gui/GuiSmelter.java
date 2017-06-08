package de.ellpeck.rockbottom.game.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentProgressBar;
import de.ellpeck.rockbottom.game.world.tile.entity.TileEntitySmelter;

public class GuiSmelter extends GuiContainer{

    private final TileEntitySmelter tile;

    public GuiSmelter(AbstractEntityPlayer player, TileEntitySmelter tile){
        super(player, 198, 150);
        this.tile = tile;
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentProgressBar(this, this.guiLeft+80, this.guiTop+15, 40, 8, PROGRESS_COLOR, false, this.tile::getSmeltPercentage));
        this.components.add(new ComponentProgressBar(this, this.guiLeft+74, this.guiTop+30, 8, 18, FIRE_COLOR, true, this.tile:: getFuelPercentage));
    }
}
