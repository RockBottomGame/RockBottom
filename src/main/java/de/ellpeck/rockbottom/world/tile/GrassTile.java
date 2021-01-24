package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.GrassTileRenderer;

import java.util.Collections;
import java.util.List;

public class GrassTile extends BasicTile {

    public GrassTile() {
        super(ResourceName.intern("grass"));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new GrassTileRenderer(name);
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        return Collections.singletonList(new ItemInstance(GameContent.TILE_SOIL));
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        super.onChangeAround(world, x, y, layer, changedX, changedY, changedLayer);

        if (this.shouldDecay(world, x, y, layer)) {
            world.setState(layer, x, y, GameContent.TILE_SOIL.getDefState());
        }
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        if (this.shouldDecay(world, x, y, layer)) {
            world.setState(layer, x, y, GameContent.TILE_SOIL.getDefState());
        } else {
            for (Direction dir : Direction.SURROUNDING) {
                if (world.isPosLoaded(x + dir.x, y + dir.y)) {
                    TileState state = world.getState(layer, x + dir.x, y + dir.y);

                    if (state.getTile().canGrassSpreadTo(world, x + dir.x, y + dir.y, layer)) {
                        world.setState(layer, x + dir.x, y + dir.y, this.getDefState());
                    }
                }
            }
        }
    }

    private boolean shouldDecay(IWorld world, int x, int y, TileLayer layer) {
        if (world.isPosLoaded(x, y + 1)) {
            Tile tile = world.getState(layer, x, y + 1).getTile();
            return tile.hasSolidSurface(world, x, y + 1, layer) && !tile.makesGrassSnowy(world, x, y + 1, layer);
        } else {
            return false;
        }
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
        return this.shouldDecay(world, x, y, layer) ? GameContent.TILE_SOIL.getDefState() : this.getDefState();
    }

    @Override
    public boolean canKeepPlants(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    public boolean onInteractWithBreakKey(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        ItemInstance selected = player.getSelectedItem();
        if (selected != null && selected.getItem().getToolProperties(selected).containsKey(ToolProperty.HOE)) {
            if (!world.isClient()) {
                world.setState(layer, x, y, GameContent.TILE_SOIL.getDefState());
                selected.getItem().takeDamage(selected, player, 1);
            }
            return true;
        }
        return false;
    }
}
