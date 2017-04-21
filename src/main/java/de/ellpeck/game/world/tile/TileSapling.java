package de.ellpeck.game.world.tile;

import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.gen.feature.WorldGenTrees;

public class TileSapling extends TileBasic{

    public TileSapling(int id){
        super(id, "sapling");
    }

    @Override
    public boolean doesRandomUpdates(){
        return true;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer){
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public void updateRandomly(World world, int x, int y){
        WorldGenTrees trees = new WorldGenTrees();
        trees.generateAt(world, x, y, Util.RANDOM);
    }

    @Override
    public boolean canPlace(World world, int x, int y, TileLayer layer){
        if(super.canPlace(world, x, y, layer)){
            Tile tile = world.getTile(x, y-1);
            if(tile instanceof TileDirt || tile instanceof TileGrass){
                return true;
            }
        }
        return false;
    }
}
