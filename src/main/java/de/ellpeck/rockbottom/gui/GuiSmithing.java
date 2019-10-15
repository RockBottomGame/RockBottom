package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.construction.compendium.SmithingRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.gui.container.ContainerSmithingTable;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmithingTable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GuiSmithing extends Gui {

    private static final ResourceName TEX = ResourceName.intern("gui.smithing_table.construct");
    private ResourceName backgroundTex;

    private static int TABLE_WIDTH = 160;
    private static int TABLE_HEIGHT = 100;

    private List<Pos2> ingredientPositions;
    private List<Integer> ingredientOrder;
    private int maxIngredients;

    private TileEntitySmithingTable tile;
    private AbstractEntityPlayer player;
    private SmithingRecipe recipe;
    private List<ItemInstance> inputs;
    private int perfectHits;
    private int goodHits;
    private int badHits;

    private long startTick;


    public GuiSmithing(AbstractEntityPlayer player, TileEntity tile, SmithingRecipe recipe, List<ItemInstance> inputs) {
        super(TABLE_WIDTH, TABLE_HEIGHT);
        this.backgroundTex = TEX.addSuffix(".background");

        this.player = player;
        this.tile = (TileEntitySmithingTable) tile;
        this.recipe = recipe;
        this.inputs = inputs;
        this.maxIngredients = 8;

        startTick = RockBottomAPI.getGame().getTotalTicks();

        ingredientOrder = Util.makeIntList(0, maxIngredients);
        Collections.shuffle(ingredientOrder);
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        int scale = (int) (game.getRenderer().getGuiScale() * 5);
        this.y = this.y - scale;
        Pos2 start = new Pos2(this.x + 8, this.y + 8);
        ingredientPositions = Arrays.asList(
                start.copy().add(TABLE_WIDTH / 8, 10), start.copy().add(TABLE_WIDTH / 4, TABLE_HEIGHT - scale - 20),
                start.copy().add(TABLE_WIDTH - scale - 30, TABLE_HEIGHT / 2 - (int) (1.5 * scale)), start.copy().add(TABLE_WIDTH / 2, TABLE_HEIGHT / 2),
                start.copy().add(59, TABLE_HEIGHT - scale - 28), start.copy().add(41, TABLE_HEIGHT - scale - 28),
                start.copy().add(61, 10), start.copy().add(TABLE_WIDTH - scale - 18, TABLE_HEIGHT - scale - 28)
        );
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        manager.getTexture(this.backgroundTex).draw(this.x, this.y, this.width, this.height, Colors.WHITE);
        super.render(game, manager, g);
    }

    @Override
    public void renderOverlay(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.renderOverlay(game, manager, g);
        float scale = g.getGuiScale() * 5;
        int size = Math.min(maxIngredients, inputs.size());
        // Render ingredients on screen
        for (int i = 0; i < size; i++) {
            ItemInstance itemInstance = this.inputs.get(i);
            Pos2 pos = ingredientPositions.get(ingredientOrder.get(i));
            itemInstance.getItem().getRenderer().render(game, manager, g, itemInstance.getItem(), itemInstance, pos.getX(), pos.getY(), scale, Colors.WHITE);
        }
        renderHitBar(game, manager, g);
    }

    private void renderHitBar(IGameInstance game, IAssetManager manager, IRenderer renderer) {
        float sizeX = 100;
        float sizeY = 4;

        float barX = this.x + this.width / 2f - sizeX / 2;
        float barY = this.y + TABLE_HEIGHT + 20;

        float markerX;
        float markerY = barY - 3;

        long ticks = game.getTotalTicks();

        float t = (((ticks - this.startTick) % 360) / 360f) * (float) Math.PI * 2;

        markerX = barX + (float) (Math.sin(t) / 2f + 0.5f) * sizeX;


        renderer.addFilledRect(barX, barY, sizeX, sizeY, Colors.ORANGE);
        renderer.addFilledRect(markerX, markerY, 1, 2, Colors.WHITE);

    }

    @Override
    protected boolean tryEscape(IGameInstance game) {
        return player.openGuiContainer(new GuiSmithingTable(player, tile), new ContainerSmithingTable(player, tile));
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("smithing_construction");
    }
}
