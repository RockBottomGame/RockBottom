package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.MutableBool;
import de.ellpeck.rockbottom.api.util.MutableInt;
import de.ellpeck.rockbottom.api.util.MutableString;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentConstruction;
import de.ellpeck.rockbottom.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class GuiInventory extends GuiContainer implements IInvChangeCallback{

    private static boolean isConstructionOpen;
    private static final MutableBool SHOW_ALL = new MutableBool(true);
    private static final MutableString SEARCH_TEXT = new MutableString();
    private static final MutableInt SCROLL_AMOUNT = new MutableInt(0);

    private ComponentConstruction construction;

    public GuiInventory(EntityPlayer player){
        super(player, 158, 87);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentFancyToggleButton(this, 0, this.guiLeft-14, this.guiTop, 12, 12, !isConstructionOpen, AbstractGame.internalRes("gui.construction"), game.getAssetManager().localize(AbstractGame.internalRes("button.construction"))));

        if(isConstructionOpen){
            this.construction = new ComponentConstruction(this, 1, this.guiLeft-112, this.guiTop, 110, 88, 5, 5, SHOW_ALL, SEARCH_TEXT, SCROLL_AMOUNT, RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES);
            this.components.add(this.construction);
        }
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);
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
    protected void initGuiVars(IGameInstance game){
        super.initGuiVars(game);

        if(isConstructionOpen){
            this.guiLeft += 52;
        }
    }

    @Override
    public boolean onButtonActivated(IGameInstance game, int button){
        if(this.construction != null && this.construction.onPress(game, button)){
            return true;
        }
        else if(button == 0){
            isConstructionOpen = !isConstructionOpen;
            this.initGui(game);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("inventory");
    }

    @Override
    public void onChange(IInventory inv, int slot, ItemInstance newInstance){
        if(this.construction != null){
            this.construction.populateConstructionButtons();
        }
    }
}
