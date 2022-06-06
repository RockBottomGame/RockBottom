package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.IPotPlantable;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.gen.feature.TreesWorldGen;

public class SaplingTile extends BasicTile implements IPotPlantable {

    private static final TreesWorldGen GEN = new TreesWorldGen();

    public SaplingTile() {
        super(ResourceName.intern("sapling"));
        this.addProps(StaticTileProps.SAPLING_GROWTH);
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        if (Util.RANDOM.nextDouble() >= 0.75) {
            if (world.isDaytime() && world.getSkyLight(x, y) >= 25) {
                TileState state = world.getState(layer, x, y);
                if (state.get(StaticTileProps.SAPLING_GROWTH) >= 4) {
                    if (GEN.makeTree(world, x, y, true)) {
                        GEN.makeTree(world, x, y, false);
                    }
                } else {
                    world.setState(layer, x, y, state.cycleProp(StaticTileProps.SAPLING_GROWTH));
                }
            }
        }
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return world.getState(x, y - 1).getTile().canKeepPlants(world, x, y - 1, layer);
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        if (changedLayer == layer && changedX == x && changedY == y - 1) {
            return world.getState(changedX, changedY).getTile().canKeepPlants(world, changedX, changedY, changedLayer);
        }

        return true;
    }

    @Override
    public float getRenderYOffset(IWorld world, TileState pot, int x, int y, ItemInstance item) {
        return -3/12f;
    }

    @Override
    public float getRenderXOffset(IWorld world, TileState pot, int x, int y, ItemInstance item) {
        return -0.5f/12;
    }
}
