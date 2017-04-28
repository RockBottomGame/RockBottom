package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.assets.font.FormattingCode;
import de.ellpeck.rockbottom.construction.BasicRecipe;
import de.ellpeck.rockbottom.construction.ConstructionList;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.gui.component.ComponentButton;
import de.ellpeck.rockbottom.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.gui.component.ComponentRecipeButton;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.inventory.IInventory;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.net.packet.toserver.PacketManualConstruction;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class GuiInventory extends GuiContainer implements IInvChangeCallback{

    private final List<ComponentRecipeButton> constructionButtons = new ArrayList<>();

    private static boolean isConstructionOpen;
    private static boolean shouldShowAll;

    public GuiInventory(EntityPlayer player){
        super(player, 158, 83);
    }

    @Override
    public void initGui(RockBottom game){
        super.initGui(game);

        this.components.add(new ComponentFancyToggleButton(this, 0, this.guiLeft-14, this.guiTop, 12, 12, !isConstructionOpen, "gui.construction", game.assetManager.localize("button.construction")));
        this.components.add(new ComponentButton(this, -1, this.guiLeft+this.sizeX/2-15, this.guiTop+this.sizeY+10, 30, 10, game.assetManager.localize("button.close")));

        if(isConstructionOpen){
            this.components.add(new ComponentFancyToggleButton(this, 1, this.guiLeft-14, this.guiTop+14, 12, 12, !shouldShowAll, "gui.all_construction", game.assetManager.localize("button.all_construction")));
            this.initConstructionButtons();
        }
    }

    protected void initConstructionButtons(){
        if(!this.constructionButtons.isEmpty()){
            this.components.removeAll(this.constructionButtons);
            this.constructionButtons.clear();
        }

        List<ItemInstance> playerInv = this.player.inv.getItems();

        List<BasicRecipe> recipes;
        if(shouldShowAll){
            recipes = ConstructionRegistry.MANUAL_RECIPES.getUnmodifiable();
        }
        else{
            recipes = ConstructionRegistry.MANUAL_RECIPES.fromInputs(playerInv);
        }

        int x = 0;
        int y = 0;
        for(int i = 0; i < recipes.size(); i++){
            BasicRecipe recipe = recipes.get(i);
            this.constructionButtons.add(new ComponentRecipeButton(this, 2+i, this.guiLeft-104+x, this.guiTop+y, 16, 16, recipe, ConstructionRegistry.MANUAL_RECIPES.getId(recipe), ConstructionList.matchesInputs(recipe, playerInv)));

            x += 18;
            if((i+1)%5 == 0){
                y += 18;
                x = 0;
            }
        }

        this.components.addAll(this.constructionButtons);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g){
        if(isConstructionOpen){
            if(this.constructionButtons.isEmpty()){
                manager.getFont().drawSplitString(this.guiLeft-104, this.guiTop, FormattingCode.GRAY+manager.localize("info.need_items"), 0.25F, 88);
            }
        }

        super.render(game, manager, g);
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
        if(button == 1){
            shouldShowAll = !shouldShowAll;
            this.initConstructionButtons();
            return true;
        }
        else if(button == 0){
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
                    if(but.canConstruct){
                        if(NetHandler.isClient()){
                            NetHandler.sendToServer(new PacketManualConstruction(game.player.getUniqueId(), but.recipeId));
                        }
                        else{
                            ContainerInventory.doManualCraft(game.player, but.recipe);
                        }
                        return true;
                    }
                    else{
                        break;
                    }
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
