package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.construction.BasicRecipe;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.gui.component.ComponentButton;
import de.ellpeck.rockbottom.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.gui.component.ComponentRecipeButton;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.inventory.IInventory;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toserver.PacketManualConstruction;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;

public class GuiInventory extends GuiContainer implements IInvChangeCallback{

    private final List<ComponentRecipeButton> constructionButtons = new ArrayList<>();
    public static boolean isConstructionOpen;

    public GuiInventory(EntityPlayer player){
        super(player, 158, 83);
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        this.components.add(new ComponentFancyButton(this, 0, this.guiLeft-14, this.guiTop, 12, 12, "gui.construction", game.assetManager.localize("button.construction")));
        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-15, this.guiTop+this.sizeY+10, 30, 10, game.assetManager.localize("button.close")));

        if(isConstructionOpen){
            this.initConstructionButtons();
        }
    }

    protected void initConstructionButtons(){
        if(!this.constructionButtons.isEmpty()){
            this.components.removeAll(this.constructionButtons);
            this.constructionButtons.clear();
        }

        List<BasicRecipe> recipes = ConstructionRegistry.MANUAL_RECIPES.fromInputs(this.player.inv.getItems());

        int x = 0;
        int y = 0;
        for(int i = 0; i < recipes.size(); i++){
            BasicRecipe recipe = recipes.get(i);
            this.constructionButtons.add(new ComponentRecipeButton(this, 1+i, this.guiLeft-104+x, this.guiTop+y, 16, 16, recipe, ConstructionRegistry.MANUAL_RECIPES.getId(recipe)));

            x += 18;
            if((i+1)%5 == 0){
                y += 18;
                x = 0;
            }
        }

        this.components.addAll(this.constructionButtons);
    }

    @Override
    public void onOpened(RockBottom game){
        super.onOpened(game);
        this.player.inv.addChangeCallback(this);
    }

    @Override
    public void onClosed(RockBottom game){
        super.onClosed(game);
        this.player.inv.removeChangeCallback(this);
    }

    @Override
    protected void initGuiVars(RockBottom game){
        super.initGuiVars(game);

        if(isConstructionOpen){
            this.guiLeft += 52;
        }
    }

    @Override
    public boolean onButtonActivated(RockBottom game, int button){
        if(button == 0){
            isConstructionOpen = !isConstructionOpen;
            this.initGui(game);
            return true;
        }
        else if(button == -1){
            game.guiManager.closeGui();
            return true;
        }
        else{
            for(ComponentRecipeButton but : this.constructionButtons){
                if(but.id == button){
                    if(NetHandler.isClient()){
                        NetHandler.sendToServer(new PacketManualConstruction(game.player.getUniqueId(), but.recipeId));
                    }
                    else{
                        ContainerInventory.doManualCraft(game.player, but.recipe);
                    }
                    return true;
                }
            }
            return super.onButtonActivated(game, button);
        }
    }

    @Override
    public void onChange(IInventory inv, int slot, ItemInstance newInstance){
        if(isConstructionOpen){
            this.initConstructionButtons();
        }
    }
}
