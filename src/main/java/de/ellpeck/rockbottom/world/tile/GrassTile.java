package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
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
        return Collections.singletonList(new ItemInstance(GameContent.Tiles.SOIL));
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        super.onChangeAround(world, x, y, layer, changedX, changedY, changedLayer);

        if (this.shouldDecay(world, x, y, layer)) {
            world.setState(layer, x, y, GameContent.Tiles.SOIL.getDefState());
        }
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        if (this.shouldDecay(world, x, y, layer)) {
            world.setState(layer, x, y, GameContent.Tiles.SOIL.getDefState());
        } else {
            for (Direction dir : Direction.SURROUNDING) {
                if (world.isPosLoaded(x + dir.x, y + dir.y)) {
                    TileState state = world.getState(layer, x + dir.x, y + dir.y);

                    if (state.getTile().canGrassSpreadTo(world, x + dir.x, y + dir.y, layer)) {
                        world.setState(layer, x + dir.x, y + dir.y, this.getDefState());
                    }
                }
            }

            // Attempt to spawn foliage on grass
            if (Util.RANDOM.nextFloat() < 0.005f) {
                int tufts = 0;
                checkTufts:
                for (int yOff = -2; yOff <= 2; yOff++) {
                    for (int xOff = -2; xOff <= 2; xOff++) {
                        int checkX = x + xOff;
                        int checkY = y + yOff + 1;
                        if (world.isPosLoaded(checkX, checkY) && world.getState(checkX, checkY).getTile() == GameContent.Tiles.GRASS_TUFT) {
                            if (tufts++ >= 3) {
                                break checkTufts;
                            }
                        }
                    }
                }

                if (tufts < 3) {
                    if (this.canKeepPlants(world, x, y, layer) && world.isPosLoaded(x, y + 1) && world.getState(x, y + 1).getTile().isAir()) {
                        TileMeta grassTuft = GameContent.Tiles.GRASS_TUFT;
                        world.setState(layer, x, y + 1, grassTuft.getDefState().prop(grassTuft.metaProp, Util.RANDOM.nextInt(grassTuft.metaProp.getVariants())));
                    }
                }
            }
        }
    }

    private boolean shouldDecay(IWorld world, int x, int y, TileLayer layer) {
        if (world.isPosLoaded(x, y + 1)) {
            Tile above = world.getState(layer, x, y + 1).getTile();
            return !world.getState(TileLayer.LIQUIDS, x, y + 1).getTile().isAir() || above.hasSolidSurface(world, x, y + 1, layer) && !above.makesGrassSnowy(world, x, y + 1, layer);
        } else {
            return false;
        }
    }

    @Override
    public TileState getPlacementState(IWorld world, int x, int y, TileLayer layer, ItemInstance instance, AbstractPlayerEntity placer) {
        return this.shouldDecay(world, x, y, layer) ? GameContent.Tiles.SOIL.getDefState() : this.getDefState();
    }

    @Override
    public boolean canKeepPlants(IWorld world, int x, int y, TileLayer layer) {
        return true;
    }

    @Override
    public boolean onInteractWithBreakKey(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        ItemInstance selected = player.getSelectedItem();
        if (selected != null && selected.getItem().hasToolProperty(selected, ToolProperty.HOE)) {
            if (!world.isClient()) {
                world.setState(layer, x, y, GameContent.Tiles.SOIL.getDefState());
                selected.getItem().takeDamage(selected, player, 1);
            }
            return true;
        }
        return false;
    }
}
