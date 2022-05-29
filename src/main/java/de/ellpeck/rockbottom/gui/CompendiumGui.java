package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.CompendiumCategory;
import de.ellpeck.rockbottom.api.construction.compendium.ICompendiumRecipe;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.ContainerGui;
import de.ellpeck.rockbottom.api.gui.component.FancyButtonComponent;
import de.ellpeck.rockbottom.api.gui.component.InputFieldComponent;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.gui.component.MenuItemComponent;
import de.ellpeck.rockbottom.api.gui.component.construction.ConstructComponent;
import de.ellpeck.rockbottom.api.gui.component.construction.IngredientComponent;
import de.ellpeck.rockbottom.api.gui.component.construction.PolaroidComponent;
import de.ellpeck.rockbottom.api.helper.InventoryHelper;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.construction.category.ManualConstructionCategory;
import de.ellpeck.rockbottom.gui.component.CompendiumCategoryComponent;
import de.ellpeck.rockbottom.gui.container.ItemListContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

public class CompendiumGui extends ContainerGui {

    public static CompendiumCategory currentCategory = ManualConstructionCategory.INSTANCE;
    private static int categoryOffset;

    public static final int PAGE_WIDTH = 72;
    public static final int PAGE_HEIGHT = 94;
    private static final ResourceName LEFT_PAGE = ResourceName.intern("gui.compendium.page_items");
    private static final ResourceName SEARCH_ICON = ResourceName.intern("gui.compendium.search_bar");
    private static final ResourceName SEARCH_BAR = ResourceName.intern("gui.compendium.search_bar_extended");
    private final List<PolaroidComponent> polaroids = new ArrayList<>();
    private final List<IngredientComponent> ingredients = new ArrayList<>();
    private final List<CompendiumCategoryComponent> categories = new ArrayList<>();
    private FancyButtonComponent categoryDown;
    private FancyButtonComponent categoryUp;
    public ICompendiumRecipe selectedRecipe;
    public boolean keepContainerOpen;
    private MenuComponent menu;
    private ConstructComponent construct;
    private InputFieldComponent searchBar;
    private BoundingBox searchButtonBox;
    private String searchText = "";
    private final BiConsumer<IInventory, Integer> invCallback = (inv, slot) -> this.organize();

    public CompendiumGui(AbstractPlayerEntity player) {
        super(player, PAGE_WIDTH * 2 + 1, PAGE_HEIGHT + 75);

        ShiftClickBehavior behavior = new ShiftClickBehavior(0, 7, 8, player.getInv().getSlotAmount() - 1);
        this.shiftClickBehaviors.add(behavior);
        this.shiftClickBehaviors.add(behavior.reversed());
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.menu = new MenuComponent(this, -12, 2, 12, PAGE_HEIGHT - 4, 3, 4, 11, 0, new BoundingBox(0, 0, PAGE_WIDTH, PAGE_HEIGHT).add(this.x, this.y), ResourceName.intern("gui.compendium.scroll_bar"));
        this.components.add(this.menu);

        this.components.add(new FancyButtonComponent(this, 5 - 16, CompendiumGui.PAGE_HEIGHT + 5, 14, 14, () -> {
            this.keepContainerOpen = true;
            game.getGuiManager().openGui(new InventoryGui(this.player));
            return true;
        }, ResourceName.intern("gui.compendium.book_open"), game.getAssetManager().localize(ResourceName.intern("button.close_compendium"))));

        this.searchBar = new InputFieldComponent(this, 145, 80, 70, 12, false, false, false, 64, false, strg -> {
            if (!strg.equals(this.searchText)) {
                this.searchText = strg;
                this.organize();
            }
        });
        this.searchBar.setActive(false);
        this.components.add(this.searchBar);

        this.searchButtonBox = new BoundingBox(0, 0, 13, 14).add(this.x + 145, this.y + 79);

        for (CompendiumCategory category : Registries.COMPENDIUM_CATEGORY_REGISTRY.values()) {
            if (category.shouldDisplay(this.player)) {
                this.categories.add(new CompendiumCategoryComponent(this, category, category == currentCategory));
            }
        }
        this.components.addAll(this.categories);

        this.categoryUp = new FancyButtonComponent(this, this.width + 17, 1, 6, 6, () -> {
            categoryOffset--;
            this.sortCategories();
            return true;
        }, ResourceName.intern("gui.compendium.arrow_up"));
        this.components.add(this.categoryUp.setHasBackground(false));

        this.categoryDown = new FancyButtonComponent(this, this.width + 17, 1 + 14 * 5 - 6, 6, 6, () -> {
            categoryOffset++;
            this.sortCategories();
            return true;
        }, ResourceName.intern("gui.compendium.arrow_down"));
        this.components.add(this.categoryDown.setHasBackground(false));

        if (this.player.getGameMode().isCreative()) {
            this.components.add(new FancyButtonComponent(this, 145, 153, 16, 16, () -> {
                this.player.openGuiContainer(new ItemListGui(player, true), new ItemListContainer(player));
                return true;
            }, ResourceName.intern("gui.icons.creative_icon")));
        }

        this.sortCategories();
        this.organize();
    }

    private void sortCategories() {
        int y = 1;
        for (int i = 0; i < this.categories.size(); i++) {
            CompendiumCategoryComponent comp = this.categories.get(i);
            if (i >= categoryOffset && i < categoryOffset + 5) {
                comp.setActive(true);

                comp.setPos(this.width, y);
                y += 14;
            } else {
                comp.setActive(false);
            }
        }

        this.categoryUp.setActive(categoryOffset > 0);
        this.categoryDown.setActive(this.categories.size() > categoryOffset + 5);
    }

    private void organize() {
        this.menu.clear();
        this.polaroids.clear();

        boolean containsSelected = false;
        for (ICompendiumRecipe recipe : currentCategory.getRecipes()) {
            if (recipe.isKnown(this.player)) {
                if (this.searchText.isEmpty() || this.matchesSearch(recipe.getOutputs())) {
                    IInventory inv = this.player.getInv();
                    PolaroidComponent polaroid = recipe.getPolaroidButton(this, this.player, recipe.canConstruct(this.player, inv, inv, null, InventoryHelper.collectItems(inv)), PolaroidComponent.DEFAULT_TEX);

                    polaroid.isSelected = this.selectedRecipe == recipe;
                    if (polaroid.isSelected) {
                        containsSelected = true;
                    }

                    this.polaroids.add(polaroid);
                }
            } else if (this.searchText.isEmpty()) {
                this.polaroids.add(new PolaroidComponent(this, null, false));
            }
        }
        if (!containsSelected) {
            this.selectedRecipe = null;
        }

        this.polaroids.sort((p1, p2) -> Integer.compare(Boolean.compare(p1.recipe == null, p2.recipe == null) * 2, Boolean.compare(p1.canConstruct, p2.canConstruct)));

        for (PolaroidComponent comp : this.polaroids) {
            this.menu.add(new MenuItemComponent(18, 20).add(0, 2, comp));
        }

        this.menu.organize();

        if (this.selectedRecipe != null) {
            this.stockIngredients(this.selectedRecipe.getIngredientButtons(this, this.player, IngredientComponent.DEFAULT_TEX));
        } else {
            this.stockIngredients(Collections.emptyList());
        }
        this.initConstructButton(this.selectedRecipe);

        currentCategory.onGuiOrganized(this, this.menu, this.polaroids, this.ingredients, this.construct);
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

    private void stockIngredients(List<IngredientComponent> actualIngredients) {
        if (!this.ingredients.isEmpty()) {
            this.components.removeAll(this.ingredients);
            this.ingredients.clear();
        }

        this.ingredients.addAll(actualIngredients);
        while (this.ingredients.size() < currentCategory.getMaxIngredientAmount(this, actualIngredients)) {
            this.ingredients.add(new IngredientComponent(this, false, Collections.emptyList()));
        }

        this.components.addAll(this.ingredients);

        int counter = 0;
        for (IngredientComponent comp : this.ingredients) {
            Pos2 pos = currentCategory.getIngredientPosition(this, comp, counter);
            comp.setPos(pos.getX(), pos.getY());
            counter++;
        }
    }

    private void initConstructButton(ICompendiumRecipe recipe) {
        if (this.construct != null) {
            this.components.remove(this.construct);
            this.construct = null;
        }

        if (recipe != null) {
            IInventory inv = this.player.getInv();
            this.construct = recipe.getConstructButton(this, this.player, null, recipe.canConstruct(this.player, inv, inv, null, InventoryHelper.collectItems(inv)));
            this.components.add(this.construct);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        manager.getTexture(LEFT_PAGE).draw(this.x, this.y, PAGE_WIDTH, PAGE_HEIGHT);
        manager.getTexture(currentCategory.getBackgroundPicture(this, manager)).draw(this.x + PAGE_WIDTH + 1, this.y, PAGE_WIDTH, PAGE_HEIGHT);

        if (this.selectedRecipe != null) {
            String outputName = this.selectedRecipe.getOutputs().get(0).getDisplayName();
            manager.getFont().drawAutoScaledString(this.x + 109, this.y + 6, outputName, 0.25F, PAGE_WIDTH - 2, Colors.BLACK, Colors.NO_COLOR, true, false);
        }

        if (this.searchBar.isActive()) {
            manager.getTexture(SEARCH_BAR).draw(this.x + 145, this.y + 79, 84, 14);
        } else {
            manager.getTexture(SEARCH_ICON).draw(this.x + 145, this.y + 79, 13, 14);
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
                for (PolaroidComponent polaroid : this.polaroids) {
                    if (polaroid.recipe != null && polaroid.isMouseOverPrioritized(game)) {
                        if (this.selectedRecipe != polaroid.recipe) {
                            this.selectedRecipe = polaroid.recipe;
                            polaroid.isSelected = true;

                            this.initConstructButton(polaroid.recipe);
                            this.stockIngredients(polaroid.recipe.getIngredientButtons(this, this.player, IngredientComponent.DEFAULT_TEX));
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
        return CompendiumGui.PAGE_HEIGHT + 5;
    }

    @Override
    public boolean shouldDoFingerCursor(IGameInstance game) {
        IRenderer g = game.getRenderer();
        return this.searchButtonBox.contains(g.getMouseInGuiX(), g.getMouseInGuiY()) || super.shouldDoFingerCursor(game);
    }
}