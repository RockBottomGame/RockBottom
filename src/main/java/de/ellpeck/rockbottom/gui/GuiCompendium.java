package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.construction.compendium.construction.ConstructionRecipe;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentFancyButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentInputField;
import de.ellpeck.rockbottom.api.gui.component.ComponentMenu;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.gui.component.construction.ComponentConstruct;
import de.ellpeck.rockbottom.api.gui.component.construction.ComponentIngredient;
import de.ellpeck.rockbottom.api.gui.component.construction.ComponentPolaroid;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class GuiCompendium extends GuiContainer {

    public static final int PAGE_WIDTH = 72;
    public static final int PAGE_HEIGHT = 94;
    private static final ResourceName LEFT_PAGE = ResourceName.intern("gui.construction.page_items");
    private static final ResourceName RIGHT_PAGE = ResourceName.intern("gui.construction.page_recipes");
    private static final ResourceName SEARCH_ICON = ResourceName.intern("gui.construction.search_bar");
    private static final ResourceName SEARCH_BAR = ResourceName.intern("gui.construction.search_bar_extended");
    private final List<ComponentPolaroid> polaroids = new ArrayList<>();
    private final List<ComponentIngredient> ingredients = new ArrayList<>();
    public ICompendiumRecipe selectedRecipe;
    private ComponentMenu menu;
    private ComponentConstruct construct;

    private ComponentInputField searchBar;
    private BoundBox searchButtonBox;
    private String searchText = "";
    private final BiConsumer<IInventory, Integer> invCallback = (inv, slot) -> this.organize();
    private boolean keepContainerOpen;

    public GuiCompendium(AbstractEntityPlayer player) {
        super(player, PAGE_WIDTH * 2 + 1, PAGE_HEIGHT + 75);

        ShiftClickBehavior behavior = new ShiftClickBehavior(0, 7, 8, player.getInv().getSlotAmount() - 1);
        this.shiftClickBehaviors.add(behavior);
        this.shiftClickBehaviors.add(behavior.reversed());
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.menu = new ComponentMenu(this, -12, 2, 12, PAGE_HEIGHT - 4, 3, 4, 11, 0, new BoundBox(0, 0, PAGE_WIDTH, PAGE_HEIGHT).add(this.x, this.y), ResourceName.intern("gui.construction.scroll_bar"));
        this.components.add(this.menu);

        this.components.add(new ComponentFancyButton(this, 5 - 16, GuiCompendium.PAGE_HEIGHT + 5, 14, 14, () -> {
            this.keepContainerOpen = true;
            game.getGuiManager().openGui(new GuiInventory(this.player));
            return true;
        }, ResourceName.intern("gui.construction.book_open"), game.getAssetManager().localize(ResourceName.intern("button.close_compendium"))));

        this.searchBar = new ComponentInputField(this, 145, 79, 70, 12, false, false, false, 64, false, strg -> {
            if (!strg.equals(this.searchText)) {
                this.searchText = strg;
                this.organize();
            }
        });
        this.searchBar.setActive(false);
        this.components.add(this.searchBar);

        this.searchButtonBox = new BoundBox(0, 0, 13, 14).add(this.x + 145, this.y + 78);

        this.organize();
    }

    private void organize() {
        this.menu.clear();
        this.polaroids.clear();

        boolean containsSelected = false;
        for (ConstructionRecipe recipe : Registries.MANUAL_CONSTRUCTION_RECIPES.values()) {
            if (recipe.isKnown(this.player)) {
                if (this.searchText.isEmpty() || this.matchesSearch(recipe.getOutputs())) {
                    IInventory inv = this.player.getInv();
                    ComponentPolaroid polaroid = recipe.getPolaroidButton(this, this.player, recipe.canConstruct(inv, inv));

                    polaroid.isSelected = this.selectedRecipe == recipe;
                    if (polaroid.isSelected) {
                        containsSelected = true;
                    }

                    this.polaroids.add(polaroid);
                }
            } else if (this.searchText.isEmpty()) {
                this.polaroids.add(new ComponentPolaroid(this, null, false));
            }
        }
        if (!containsSelected) {
            this.selectedRecipe = null;
        }

        this.polaroids.sort((p1, p2) -> Integer.compare(Boolean.compare(p1.recipe == null, p2.recipe == null) * 2, Boolean.compare(p1.canConstruct, p2.canConstruct)));

        for (ComponentPolaroid comp : this.polaroids) {
            this.menu.add(new MenuComponent(18, 20).add(0, 2, comp));
        }

        this.menu.organize();

        if (this.selectedRecipe != null) {
            this.stockIngredients(this.selectedRecipe.getIngredientButtons(this, this.player));
        } else {
            this.stockIngredients(Collections.emptyList());
        }
        this.initConstructButton(this.selectedRecipe);
    }

    @Override
    public boolean shouldCloseContainer() {
        return !this.keepContainerOpen;
    }

    private boolean matchesSearch(List<ItemInstance> outputs) {
        String lowerSearch = this.searchText.toLowerCase(Locale.ROOT);
        for (ItemInstance instance : outputs) {
            if (instance.getDisplayName().toLowerCase(Locale.ROOT).contains(lowerSearch)) {
                return true;
            }
        }
        return false;
    }

    private void stockIngredients(List<ComponentIngredient> actualIngredients) {
        if (!this.ingredients.isEmpty()) {
            this.components.removeAll(this.ingredients);
            this.ingredients.clear();
        }

        this.ingredients.addAll(actualIngredients);
        while (this.ingredients.size() < 8) {
            this.ingredients.add(new ComponentIngredient(this, false, Collections.emptyList()));
        }

        this.components.addAll(this.ingredients);

        int ingrX = 0;
        int ingrY = 0;
        int counter = 0;

        for (ComponentIngredient comp : this.ingredients) {
            comp.setPos(78 + ingrX, 51 + ingrY);

            ingrX += 16;
            counter++;

            if (counter >= 4) {
                counter = 0;
                ingrX = 0;

                ingrY += 19;
            }
        }
    }

    private void initConstructButton(ICompendiumRecipe recipe) {
        if (this.construct != null) {
            this.components.remove(this.construct);
            this.construct = null;
        }

        if (recipe != null) {
            IInventory inv = this.player.getInv();
            this.construct = recipe.getConstructButton(this, this.player, this.selectedRecipe.canConstruct(inv, inv));
            this.components.add(this.construct);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        manager.getTexture(LEFT_PAGE).draw(this.x, this.y, PAGE_WIDTH, PAGE_HEIGHT);
        manager.getTexture(RIGHT_PAGE).draw(this.x + PAGE_WIDTH + 1, this.y, PAGE_WIDTH, PAGE_HEIGHT);

        if (this.selectedRecipe != null) {
            String strg = this.selectedRecipe.getOutputs().get(0).getDisplayName();
            manager.getFont().drawAutoScaledString(this.x + 109, this.y + 6, strg, 0.25F, PAGE_WIDTH - 2, Colors.BLACK, Colors.NO_COLOR, true, false);
        }

        if (this.searchBar.isActive()) {
            manager.getTexture(SEARCH_BAR).draw(this.x + 145, this.y + 78, 84, 14);
        } else {
            manager.getTexture(SEARCH_ICON).draw(this.x + 145, this.y + 78, 13, 14);
        }

        super.render(game, manager, g);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("compendium");
    }

    @Override
    public void onOpened(IGameInstance game) {
        super.onOpened(game);
        this.player.getInv().addChangeCallback(this.invCallback);
    }

    @Override
    public void onClosed(IGameInstance game) {
        super.onClosed(game);
        this.player.getInv().removeChangeCallback(this.invCallback);
    }

    @Override
    public boolean onMouseAction(IGameInstance game, int button, float x, float y) {
        if (!super.onMouseAction(game, button, x, y)) {
            if (Settings.KEY_GUI_ACTION_1.isKey(button)) {
                if (this.searchButtonBox.contains(x, y)) {
                    boolean activeNow = !this.searchBar.isActive();
                    this.searchBar.setActive(activeNow);
                    this.searchBar.setSelected(true);

                    this.searchButtonBox.add((activeNow ? 1 : -1) * 71, 0);

                    if (!this.searchText.isEmpty()) {
                        this.searchBar.setText("");
                        this.searchText = "";
                        this.organize();
                    }

                    return true;
                }

                boolean did = false;
                for (ComponentPolaroid polaroid : this.polaroids) {
                    if (polaroid.recipe != null && polaroid.isMouseOverPrioritized(game)) {
                        if (this.selectedRecipe != polaroid.recipe) {
                            this.selectedRecipe = polaroid.recipe;
                            polaroid.isSelected = true;

                            this.initConstructButton(polaroid.recipe);
                            this.stockIngredients(polaroid.recipe.getIngredientButtons(this, this.player));
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
    public int getSlotOffsetX() {
        return 5;
    }

    @Override
    public int getSlotOffsetY() {
        return GuiCompendium.PAGE_HEIGHT + 5;
    }

    @Override
    public boolean shouldDoFingerCursor(IGameInstance game) {
        IRenderer g = game.getRenderer();
        return this.searchButtonBox.contains(g.getMouseInGuiX(), g.getMouseInGuiY()) || super.shouldDoFingerCursor(game);
    }
}