package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenTrees;

public class TileSapling extends TileBasic {

    private static final WorldGenTrees GEN = new WorldGenTrees();

    public TileSapling() {
        super(ResourceName.intern("sapling"));
        this.addProps(StaticTileProps.SAPLING_GROWTH);
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer) {
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
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player) {
        return world.getState(x, y - 1).getTile().canKeepPlants(world, x, y - 1, layer);
    }
}
