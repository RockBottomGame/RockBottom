package de.ellpeck.rockbottom.gui.component;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.construction.BasicRecipe;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyToggleButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentScrollMenu;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.util.reg.NameRegistry;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class ComponentConstruction extends GuiComponent{

    private static final IResourceName LOC_NEED = RockBottomAPI.createInternalRes("info.need_items");
    public final GuiContainer gui;
    public final BiConsumer<IRecipe, IResourceName> consumer;
    private final ComponentScrollMenu menu;
    private final NameRegistry<BasicRecipe> recipes;
    private String searchText = "";
    private boolean shouldShowAll;

    public ComponentConstruction(GuiContainer gui, int x, int y, int sizeX, int sizeY, int buttonAmountX, int buttonAmonutY, boolean shouldShowAll, NameRegistry<BasicRecipe> recipes, BiConsumer<IRecipe, IResourceName> consumer){
        super(gui, x, y, sizeX, sizeY);
        this.gui = gui;
        this.recipes = recipes;
        this.consumer = consumer;
        this.shouldShowAll = shouldShowAll;

        this.menu = new ComponentScrollMenu(gui, x, y, sizeY, buttonAmountX, buttonAmonutY, new BoundBox(0, 0, sizeX, sizeY).add(gui.getX()+x, gui.getY()+y));
        this.gui.getComponents().add(this.menu);

        this.gui.getComponents().add(new ComponentInputField(gui, x, y-14, sizeX-14, 12, true, true, false, 40, true, (text) -> {
            if(!text.equals(this.searchText)){
                this.searchText = text;
                this.organize();
            }
        }));

        this.gui.getComponents().add(new ComponentFancyToggleButton(gui, x+sizeX-12, y-14, 12, 12, !this.shouldShowAll, () -> {
            this.shouldShowAll = !this.shouldShowAll;
            this.organize();
            return true;
        }, RockBottomAPI.createInternalRes("gui.all_construction"), RockBottomAPI.getGame().getAssetManager().localize(RockBottomAPI.createInternalRes("button.all_construction"))));

        this.organize();
    }

    public void organize(){
        this.menu.clear();

        for(int counter = 0; counter < (this.shouldShowAll ? 2 : 1); counter++){
            for(BasicRecipe recipe : this.recipes.getUnmodifiable().values()){
                if(recipe.isKnown(this.gui.player)){
                    if(this.searchText.isEmpty() || this.matchesSearch(recipe.getOutputs())){
                        boolean matches = IRecipe.matchesInv(recipe, this.gui.player.getInv());

                        if(matches ? counter == 0 : counter == 1){
                            this.menu.add(new ComponentRecipeButton(this, 0, 0, 16, 16, recipe, recipe.getName(), matches));
                        }
                    }
                }
            }
        }

        this.menu.organize();
    }

    private boolean matchesSearch(List<ItemInstance> outputs){
        String lowerSearch = this.searchText.toLowerCase(Locale.ROOT);
        for(ItemInstance instance : outputs){
            if(instance.getDisplayName().toLowerCase(Locale.ROOT).contains(lowerSearch)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, int x, int y){
        super.render(game, manager, g, x, y);

        if(this.menu.isEmpty()){
            manager.getFont().drawSplitString(x+8, y, FormattingCode.GRAY+manager.localize(LOC_NEED), 0.25F, 88);
        }
    }

    @Override
    public IResourceName getName(){
        return RockBottomAPI.createInternalRes("construction");
    }

    @Override
    public boolean isMouseOver(IGameInstance game){
        return false;
    }
}