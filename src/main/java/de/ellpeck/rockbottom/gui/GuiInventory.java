package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollBar;
import de.ellpeck.rockbottom.api.inventory.IInvChangeCallback;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.gui.component.ComponentRecipeButton;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.net.packet.toserver.PacketManualConstruction;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Graphics;

import java.util.List;
import java.util.Locale;

public class GuiInventory extends GuiContainer implements IInvChangeCallback{

    private static final IResourceName LOC_NEED = AbstractGame.internalRes("info.need_items");
    private static boolean isConstructionOpen;
    private static boolean shouldShowAll;
    private static int scrollAmount;
    private static String searchText;

    private final ComponentRecipeButton[] constructionButtons = new ComponentRecipeButton[25];
    private ComponentScrollBar scrollBar;
    private ComponentInputField searchBar;

    public GuiInventory(EntityPlayer player){
        super(player, 158, 83);
    }

    @Override
    public void initGui(IGameInstance game){
        super.initGui(game);

        this.components.add(new ComponentFancyToggleButton(this, 0, this.guiLeft-14, this.guiTop, 12, 12, !isConstructionOpen, AbstractGame.internalRes("gui.construction"), game.getAssetManager().localize(AbstractGame.internalRes("button.construction"))));

        if(isConstructionOpen){
            int x = 0;
            int y = 0;
            for(int i = 0; i < this.constructionButtons.length; i++){
                this.constructionButtons[i] = new ComponentRecipeButton(this, 3+i, this.guiLeft-104+x, this.guiTop+y, 16, 16);
                this.components.add(this.constructionButtons[i]);

                x += 18;
                if((i+1)%5 == 0){
                    y += 18;
                    x = 0;
                }
            }

            this.components.add(new ComponentFancyToggleButton(this, 1, this.guiLeft-14, this.guiTop+14, 12, 12, !shouldShowAll, AbstractGame.internalRes("gui.all_construction"), game.getAssetManager().localize(AbstractGame.internalRes("button.all_construction"))));

            this.scrollBar = new ComponentScrollBar(this, 2, this.guiLeft-112, this.guiTop, 6, 88, scrollAmount, 0, 10, new BoundBox(-112, 0, 0, 88).add(this.guiLeft, this.guiTop), (min, max, number) -> {
                scrollAmount = number;
                this.populateConstructionButtons();
            });
            this.components.add(this.scrollBar);

            this.searchBar = new ComponentInputField(this, this.guiLeft-112, this.guiTop-14, 110, 12, true, true, false, 40, true);
            this.searchBar.setText(searchText);
            this.components.add(this.searchBar);

            this.populateConstructionButtons();
        }
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        if(this.searchBar != null){
            String text = this.searchBar.getText();
            if(!text.equals(searchText)){
                searchText = text;
                this.populateConstructionButtons();
            }
        }
    }

    protected void populateConstructionButtons(){
        int offset = scrollAmount*5;

        int recipeCounter = 0;
        int buttonIndex = 0;

        for(int counter = 0; counter < (shouldShowAll ? 2 : 1); counter++){
            for(int i = 0; i < RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.size(); i++){
                BasicRecipe recipe = RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.get(i);
                if(searchText == null || searchText.isEmpty() || matchesSearch(recipe.getOutputs())){
                    boolean matches = IRecipe.matchesInv(recipe, this.player.getInv());

                    if(matches ? counter == 0 : counter == 1){
                        recipeCounter++;

                        if(i >= offset && buttonIndex < this.constructionButtons.length){
                            this.constructionButtons[buttonIndex].setRecipe(recipe, RockBottomAPI.MANUAL_CONSTRUCTION_RECIPES.indexOf(recipe), matches);
                            buttonIndex++;
                        }
                    }
                }
            }
        }

        for(int i = buttonIndex; i < this.constructionButtons.length; i++){
            this.constructionButtons[i].setRecipe(null, 0, false);
        }

        boolean locked = recipeCounter <= this.constructionButtons.length;
        this.scrollBar.setLocked(locked);
        if(!locked){
            this.scrollBar.setMax(Util.ceil((double)recipeCounter/5)-5);
        }
    }

    private static boolean matchesSearch(List<ItemInstance> outputs){
        String lowerSearch = searchText.toLowerCase(Locale.ROOT);
        for(ItemInstance instance : outputs){
            if(instance.getDisplayName().toLowerCase(Locale.ROOT).contains(lowerSearch)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        if(isConstructionOpen){
            this.renderNoRecipesInfo(manager);
        }

        super.render(game, manager, g);
    }

    private void renderNoRecipesInfo(IAssetManager manager){
        for(ComponentRecipeButton button : this.constructionButtons){
            if(button.recipe != null){
                return;
            }
        }

        manager.getFont().drawSplitString(this.guiLeft-104, this.guiTop, FormattingCode.GRAY+manager.localize(LOC_NEED), 0.25F, 88);
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
            this.populateConstructionButtons();
            return true;
        }
        else if(button == 0){
            isConstructionOpen = !isConstructionOpen;
            this.initGui(game);
            return true;
        }
        else{
            for(ComponentRecipeButton but : this.constructionButtons){
                if(but.recipe != null && but.id == button){
                    if(but.canConstruct){
                        if(RockBottomAPI.getNet().isClient()){
                            RockBottomAPI.getNet().sendToServer(new PacketManualConstruction(game.getPlayer().getUniqueId(), but.recipeId, 1));
                        }
                        else{
                            ContainerInventory.doManualCraft(game.getPlayer(), but.recipe, 1);
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
            this.populateConstructionButtons();
        }
    }
}
