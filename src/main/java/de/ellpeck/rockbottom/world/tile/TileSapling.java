package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenTrees;

public class TileSapling extends TileBasic{

    private static final WorldGenTrees GEN = new WorldGenTrees();

    public TileSapling(){
        super(RockBottomAPI.createInternalRes("sapling"));
        this.addProps(StaticTileProps.SAPLING_GROWTH);
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer){
        return null;
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer){
        TileState state = world.getState(layer, x, y);
        if(state.get(StaticTileProps.SAPLING_GROWTH) >= 4){
            if(GEN.makeTree(world, x, y, true)){
                GEN.makeTree(world, x, y, false);
            }
        }
        else{
            world.setState(layer, x, y, state.cycleProp(StaticTileProps.SAPLING_GROWTH));
        }
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }
}
