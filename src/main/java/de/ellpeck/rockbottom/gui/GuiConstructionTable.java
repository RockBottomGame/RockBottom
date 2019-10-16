package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.ConstructionRecipe;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentMenu;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.gui.component.construction.ComponentConstruct;
import de.ellpeck.rockbottom.api.gui.component.construction.ComponentIngredient;
import de.ellpeck.rockbottom.api.gui.component.construction.ComponentPolaroid;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityConstructionTable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;

public class GuiConstructionTable extends GuiContainer {

    private static final ResourceName background = ResourceName.intern("gui.construction_table.background");
    private static final int PAGE_HEIGHT = 94;
    private static final int MENU_WIDTH = 43 + 4;

    private static final int GUI_WIDTH = 135;
    private static final int GUI_HEIGHT = 169;

	public static final ResourceName POLAROID_TEX = ResourceName.intern("gui.construction_table.item_background");
	public static final ResourceName INGREDIENT_TEX = ResourceName.intern("gui.construction_table.ingredient_background");

	private final TileEntityConstructionTable tile;
    private final List<ComponentPolaroid> polaroids = new ArrayList<>();
    private final List<ComponentIngredient> ingredients = new ArrayList<>();
    private final BiConsumer<IInventory, Integer> invCallback = (inv, slot) -> this.organise();

    private ComponentMenu menu;
    private ComponentConstruct construct;
    private ICompendiumRecipe selectedRecipe;

    public GuiConstructionTable(AbstractEntityPlayer player, TileEntityConstructionTable tile) {
        super(player, GUI_WIDTH, GUI_HEIGHT);
        this.tile = tile;

        int playerSlots = player.getInv().getSlotAmount();

        ShiftClickBehavior input = new ShiftClickBehavior(0, playerSlots - 1, playerSlots, playerSlots - 1 + tile.getTileInventory().getSlotAmount());
        this.shiftClickBehaviors.add(input);
        this.shiftClickBehaviors.add(input.reversed());
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.menu = new ComponentMenu(this, -4, 1, 6, PAGE_HEIGHT - 2, 2, 4, -1, 1, new BoundBox(0, 0, MENU_WIDTH, PAGE_HEIGHT).add(this.x, this.y), ResourceName.intern("gui.construction_table.scroll_bar"), 1);
        this.components.add(this.menu);

        organise();
    }

    private void initConstructButton(ICompendiumRecipe recipe) {
        if (this.construct != null) {
            this.components.remove(this.construct);
            this.construct = null;
        }

        if (recipe != null) {
            IInventory inv = this.player.getInv();
            this.construct = recipe.getConstructButton(this, this.player, tile, this.selectedRecipe.canConstruct(inv, inv));
            this.construct.setPos(66, 17);
            this.components.add(this.construct);
        }
    }

    @Override
    public final void render(IGameInstance game, IAssetManager assetManager, IRenderer renderer) {
        assetManager.getTexture(background).draw((float) this.x, (float) this.y, GUI_WIDTH, PAGE_HEIGHT);
        if (this.selectedRecipe != null) {
            String strg = this.selectedRecipe.getOutputs().get(0).getDisplayName();
            assetManager.getFont().drawAutoScaledString(this.x + 72, this.y + 6, strg, 0.25F, 72 - 2, Colors.BLACK, Colors.NO_COLOR, true, false);
        }
        super.render(game, assetManager, renderer);
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
        if (!super.onMouseAction(game, button, x, y)) {
            if (Settings.KEY_GUI_ACTION_1.isKey(button)) {
                boolean did = false;
                for (ComponentPolaroid polaroid : this.polaroids) {
                    if (polaroid.recipe != null && polaroid.isMouseOverPrioritized(game)) {
                        if (this.selectedRecipe != polaroid.recipe) {
                            this.selectedRecipe = polaroid.recipe;
                            polaroid.isSelected = true;

                            this.initConstructButton(polaroid.recipe);
                            this.stockIngredients(polaroid.recipe.getIngredientButtons(this, this.player, INGREDIENT_TEX));
                        }
                        did = true;
                    } else {
                        polaroid.isSelected = false;
                    }
                }

                if (!did) {
                    if (this.selectedRecipe != null) {
                        this.selectedRecipe = null;
                        this.initConstructButton(null);
                        this.stockIngredients(Collections.emptyList());
                    }
                }
                return did;
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onOpened(IGameInstance game) {
        super.onOpened(game);
        this.player.getInv().addChangeCallback(this.invCallback);
        this.tile.getTileInventory().addChangeCallback(this.invCallback);
    }

    @Override
    public void onClosed(IGameInstance game) {
        super.onClosed(game);
        this.player.getInv().removeChangeCallback(this.invCallback);
        this.tile.getTileInventory().removeChangeCallback(this.invCallback);
    }

    private void stockIngredients(List<ComponentIngredient> actualIngredients) {
        if (!this.ingredients.isEmpty()) {
            this.components.removeAll(this.ingredients);
            this.ingredients.clear();
        }

        this.ingredients.addAll(actualIngredients);
        while (this.ingredients.size() < 8) {
            this.ingredients.add(new ComponentIngredient(this, false, Collections.emptyList(), INGREDIENT_TEX));
        }

        this.components.addAll(this.ingredients);

        int counter = 0;
        for (ComponentIngredient comp : this.ingredients) {
            Pos2 pos = new Pos2(50 + (counter % 4) * 16, 51 + (counter / 4) * 19);
            comp.setPos(pos.getX(), pos.getY());
            counter++;
        }
    }

    private void organise() {
        this.menu.clear();
        this.polaroids.clear();

        boolean containsSelected = false;
        for (ConstructionRecipe recipe : Registries.CONSTRUCTION_RECIPES.values()) {
            if (recipe.showInConstructionTable()) {
                if (recipe.isKnown(this.player)) {
                    IInventory inv = this.player.getInv();
                    ComponentPolaroid polaroid = recipe.getPolaroidButton(this, player, recipe.canUseTools(tile) && recipe.canConstruct(inv, inv), POLAROID_TEX);

                    polaroid.isSelected = this.selectedRecipe == recipe;
                    if (polaroid.isSelected) {
                        containsSelected = true;
                    }

                    this.polaroids.add(polaroid);

                } else {
                    this.polaroids.add(ComponentPolaroid.getUnknown(this, POLAROID_TEX));
                }
            }
        }
        if (!containsSelected) {
            this.selectedRecipe = null;
            initConstructButton(null);
        } else {
            this.initConstructButton(selectedRecipe);
        }

        this.polaroids.sort((p1, p2) -> Integer.compare(Boolean.compare(p1.recipe == null, p2.recipe == null) * 2, Boolean.compare(p1.canConstruct, p2.canConstruct)));

        for (ComponentPolaroid comp : this.polaroids) {
            this.menu.add(new MenuComponent(18, 20).add(0, 2, comp));
        }

        this.menu.organize();

        if (this.selectedRecipe != null) {
            this.stockIngredients(this.selectedRecipe.getIngredientButtons(this, this.player, INGREDIENT_TEX));
        } else {
            this.stockIngredients(Collections.emptyList());
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("construction_table");
    }
}
