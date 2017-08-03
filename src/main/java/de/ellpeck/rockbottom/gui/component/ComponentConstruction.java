package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollBar;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.*;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.net.packet.toserver.PacketManualConstruction;
import org.newdawn.slick.Graphics;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class ComponentConstruction extends GuiComponent{

    private static final IResourceName LOC_NEED = AbstractGame.internalRes("info.need_items");

    private final ComponentRecipeButton[] constructionButtons;
    private final ComponentScrollBar scrollBar;
    private final ComponentInputField searchBar;
    private final MutableBool shouldShowAll;
    private final MutableString searchText;
    private final MutableInt scrollAmount;

    private final GuiContainer gui;
    private final int startId;
    private final List<BasicRecipe> recipes;
    private final int buttonAmountX;
    private final BiConsumer<IRecipe, Integer> onClickedConsumer;

    public ComponentConstruction(GuiContainer gui, int startId, int x, int y, int sizeX, int sizeY, int buttonAmountX, int buttonAmonutY, MutableBool shouldShowAll, MutableString searchText, MutableInt scrollAmount, List<BasicRecipe> recipes, BiConsumer<IRecipe, Integer> onClickedConsumer){
        super(gui, x, y, sizeX, sizeY);
        this.gui = gui;
        this.startId = startId;
        this.shouldShowAll = shouldShowAll;
        this.searchText = searchText;
        this.scrollAmount = scrollAmount;
        this.recipes = recipes;
        this.onClickedConsumer = onClickedConsumer;

        int addX = 0;
        int addY = 0;

        this.buttonAmountX = buttonAmountX;
        this.constructionButtons = new ComponentRecipeButton[buttonAmountX*buttonAmonutY];

        for(int i = 0; i < this.constructionButtons.length; i++){
            this.constructionButtons[i] = new ComponentRecipeButton(gui, startId+2+i, x+addX+8, y+addY, 16, 16);
            gui.getComponents().add(this.constructionButtons[i]);

            addX += 18;
            if((i+1)%buttonAmountX == 0){
                addY += 18;
                addX = 0;
            }
        }

        this.scrollBar = new ComponentScrollBar(gui, startId+1, x, y, 6, sizeY, this.scrollAmount.get(), 0, 0, new BoundBox(0, 0, sizeX, sizeY).add(x, y), (min, max, number) -> {
            this.scrollAmount.set(number);
            this.populateConstructionButtons();
        });
        this.gui.getComponents().add(this.scrollBar);

        this.searchBar = new ComponentInputField(gui, x, y-14, sizeX-14, 12, true, true, false, 40, true);
        this.searchBar.setText(this.searchText.get());
        this.gui.getComponents().add(this.searchBar);

        this.gui.getComponents().add(new ComponentFancyToggleButton(gui, startId, x+sizeX-12, y-14, 12, 12, !this.shouldShowAll.get(), AbstractGame.internalRes("gui.all_construction"), RockBottomAPI.getGame().getAssetManager().localize(AbstractGame.internalRes("button.all_construction"))));

        this.populateConstructionButtons();
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);

        String text = this.searchBar.getText();
        if(!text.equals(this.searchText.get())){
            this.searchText.set(text);
            this.populateConstructionButtons();
        }
    }

    public void populateConstructionButtons(){
        int offset = this.scrollAmount.get()*this.buttonAmountX;

        int recipeCounter = 0;
        int buttonIndex = 0;

        for(int counter = 0; counter < (this.shouldShowAll.get() ? 2 : 1); counter++){
            for(int i = 0; i < this.recipes.size(); i++){
                BasicRecipe recipe = this.recipes.get(i);
                if(this.searchText.get().isEmpty() || this.matchesSearch(recipe.getOutputs())){
                    boolean matches = IRecipe.matchesInv(recipe, this.gui.player.getInv());

                    if(matches ? counter == 0 : counter == 1){
                        recipeCounter++;

                        if(i >= offset && buttonIndex < this.constructionButtons.length){
                            this.constructionButtons[buttonIndex].setRecipe(recipe, this.recipes.indexOf(recipe), matches);
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

    private boolean matchesSearch(List<ItemInstance> outputs){
        String lowerSearch = this.searchText.get().toLowerCase(Locale.ROOT);
        for(ItemInstance instance : outputs){
            if(instance.getDisplayName().toLowerCase(Locale.ROOT).contains(lowerSearch)){
                return true;
            }
        }
        return false;
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("construction");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g){
        super.render(game, manager, g);
        this.renderNoRecipesInfo(manager);
    }

    private void renderNoRecipesInfo(IAssetManager manager){
        for(ComponentRecipeButton button : this.constructionButtons){
            if(button.recipe != null){
                return;
            }
        }

        manager.getFont().drawSplitString(this.x+8, this.y, FormattingCode.GRAY+manager.localize(LOC_NEED), 0.25F, 88);
    }

    public boolean onPress(IGameInstance game, int button){
        if(button == this.startId){
            this.shouldShowAll.invert();
            this.populateConstructionButtons();
            return true;
        }
        else{
            for(ComponentRecipeButton but : this.constructionButtons){
                if(but.recipe != null && but.id == button){
                    if(but.canConstruct){
                        this.onClickedConsumer.accept(but.recipe, but.recipeId);
                        return true;
                    }
                    else{
                        break;
                    }
                }
            }
            return false;
        }
    }
}
