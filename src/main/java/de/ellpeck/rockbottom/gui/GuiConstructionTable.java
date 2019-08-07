package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.component.ComponentMenu;
import de.ellpeck.rockbottom.api.gui.component.construction.ComponentPolaroid;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityConstructionTable;

import java.util.ArrayList;
import java.util.List;

public class GuiConstructionTable extends GuiContainer {

    private static final ResourceName background = ResourceName.intern("gui.construction_table.background");
    private static final int PAGE_HEIGHT = 94;
    private static final int MENU_WIDTH = 22 + 4;

    private final TileEntityConstructionTable tile;
    private ComponentMenu menu;
    private final List<ComponentPolaroid> polaroids = new ArrayList<>();

    public GuiConstructionTable(AbstractEntityPlayer player, TileEntityConstructionTable tile) {
        super(player, 135, 169);
        this.tile = tile;

        int playerSlots = player.getInv().getSlotAmount();

        ShiftClickBehavior input = new ShiftClickBehavior(0, playerSlots - 1, playerSlots, playerSlots - 1 + tile.getTileInventory().getSlotAmount());
        this.shiftClickBehaviors.add(input);
        this.shiftClickBehaviors.add(input.reversed());
    }

    @Override
    public final void render(IGameInstance game, IAssetManager assetManager, IRenderer renderer) {
        assetManager.getTexture(background).draw((float) this.x + 7, (float) this.y, 120.0F, 94.0F);

        super.render(game, assetManager, renderer);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);

        this.menu = new ComponentMenu(this, 7, 1, 6, PAGE_HEIGHT - 2, 1, 4, 4, 0, new BoundBox(7, 0, MENU_WIDTH, PAGE_HEIGHT).add(this.x, this.y), ResourceName.intern("gui.construction_table.scroll_bar"));
        this.components.add(this.menu);
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("construction_table");
    }
}
