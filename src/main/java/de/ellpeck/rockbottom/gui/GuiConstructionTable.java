package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.RockBottom;

public class GuiConstructionTable extends GuiContainer implements IInvChangeCallback{

    public GuiConstructionTable(AbstractEntityPlayer player){
        super(player, 158, 135);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);
    }

    @Override
    public void onOpened(IGameInstance game){
        super.onOpened(game);
        this.player.getInv().addChangeCallback(this);
    }

    @Override
    public void onClosed(IGameInstance game){
        super.onClosed(game);
        this.player.getInv().removeChangeCallback(this);
    }

    @Override
    public IResourceName getName(){
        return RockBottom.internalRes("construction_table");
    }

    @Override
    public void onChange(IInventory inv, int slot, ItemInstance newInstance){

    }
}
