package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentProgressBar;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySimpleFurnace;

public class GuiSimpleFurnace extends GuiContainer{

    private final TileEntitySimpleFurnace tile;

    public GuiSimpleFurnace(AbstractEntityPlayer player, TileEntitySimpleFurnace tile){
        super(player, 135, 130);
        this.tile = tile;
    }

    @Override
    public void init(IGameInstance game){
        super.init(game);

        this.components.add(new ComponentProgressBar(this, 52, 4, 31, 8, Colors.GREEN, false, this.tile :: getSmeltPercentage));
        this.components.add(new ComponentProgressBar(this, 62, 16, 10, 15, Colors.RED, true, this.tile :: getFuelPercentage));
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("simple_furnace");
    }
}
