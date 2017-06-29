package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlider;
import de.ellpeck.rockbottom.api.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.gui.component.ComponentRecipeButton;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.net.packet.toserver.PacketManualConstruction;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public class GuiInventory extends GuiContainer implements IInvChangeCallback{

    private static final IResourceName LOC_NEED = AbstractGame.internalRes("info.need_items");
    private static boolean isConstructionOpen;
    private static boolean shouldShowAll;
    private static int craftAmount = 1;

    private final List<ComponentRecipeButton> constructionButtons = new ArrayList<>();

    public GuiInventory(EntityPlayer player){
        super(player, 158, 83);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentFancyToggleButton(this, 0, this.guiLeft-14, this.guiTop, 12, 12, !isConstructionOpen, AbstractGame.internalRes("gui.construction"), game.getAssetManager().localize(AbstractGame.internalRes("button.construction"))));

        if(isConstructionOpen){
            this.components.add(new ComponentSlider(this, 2, this.guiLeft-104, this.guiTop+71, 88, 12, craftAmount, 1, 128, new ComponentSlider.ICallback(){
                @Override
                public void onNumberChange(float mouseX, float mouseY, int min, int max, int number){
                    craftAmount = number;
                }
            }, game.getAssetManager().localize(AbstractGame.internalRes("button.construction_amount"))));
            this.components.add(new ComponentFancyToggleButton(this, 1, this.guiLeft-14, this.guiTop+14, 12, 12, !shouldShowAll, AbstractGame.internalRes("gui.all_construction"), game.getAssetManager().localize(AbstractGame.internalRes("button.all_construction"))));
            this.initConstructionButtons();
        }
    }

    protected void initConstructionButtons(){
        if(!this.constructionButtons.isEmpty()){
            this.components.removeAll(this.constructionButtons);
            this.constructionButtons.clear();
        }

        int x = 0;
        int y = 0;

        for(int i = 0; i < RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.size(); i++){
            BasicRecipe recipe = RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.get(i);
            boolean matches = IRecipe.matchesInv(recipe, this.player.getInv());

            if(matches || shouldShowAll){
                this.constructionButtons.add(new ComponentRecipeButton(this, 3+i, this.guiLeft-104+x, this.guiTop+y, 16, 16, recipe, RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.indexOf(recipe), matches));

                x += 18;
                if((i+1)%5 == 0){
                    y += 18;
                    x = 0;
                }
            }
        }

        this.components.addAll(this.constructionButtons);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        if(isConstructionOpen){
            if(this.constructionButtons.isEmpty()){
                manager.getFont().drawSplitString(this.guiLeft-104, this.guiTop, FormattingCode.GRAY+manager.localize(LOC_NEED), 0.25F, 88);
            }
        }

        super.render(game, manager, g);
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
        else{
            for(ComponentRecipeButton but : this.constructionButtons){
                if(but.id == button){
                    if(but.canConstruct){
                        if(RockBottomAPI.getNet().isClient()){
                            RockBottomAPI.getNet().sendToServer(new PacketManualConstruction(game.getPlayer().getUniqueId(), but.recipeId, craftAmount));
                        }
                        else{
                            ContainerInventory.doManualCraft(game.getPlayer(), but.recipe, craftAmount);
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
