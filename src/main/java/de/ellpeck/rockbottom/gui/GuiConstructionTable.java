package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.MutableBool;
import de.ellpeck.rockbottom.api.util.MutableInt;
import de.ellpeck.rockbottom.api.util.MutableString;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentConstruction;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.net.packet.toserver.PacketManualConstruction;
import de.ellpeck.rockbottom.net.packet.toserver.PacketTableConstruction;

import java.util.ArrayList;
import java.util.List;

public class GuiConstructionTable extends GuiContainer implements IInvChangeCallback{

    private ComponentConstruction construction;

    public GuiConstructionTable(AbstractEntityPlayer player){
        super(player, 158, 135);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        List<BasicRecipe> allRecipes = new ArrayList<>();
        allRecipes.addAll(RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES);
        allRecipes.addAll(RockBottomAPI.CONSTRUCTION_TABLE_RECIPES);

        this.construction = new ComponentConstruction(this, 0, this.guiLeft, this.guiTop, this.sizeX, 52, 8, 3, new MutableBool(true), new MutableString(), new MutableInt(0), allRecipes, (recipe, recipeId) -> {
            if(RockBottomAPI.getNet().isClient()){
                RockBottomAPI.getNet().sendToServer(new PacketTableConstruction(game.getPlayer().getUniqueId(), recipeId, 1));
            }
            else{
                ContainerInventory.doInvBasedConstruction(game.getPlayer(), recipe, 1);
            }
        });
        this.components.add(this.construction);
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
    public boolean onButtonActivated(IGameInstance game, int button){
        return this.construction.onPress(game, button);
    }

    @Override
    public void onChange(IInventory inv, int slot, ItemInstance newInstance){
        this.construction.populateConstructionButtons();
    }
}
