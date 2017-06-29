package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenTrees;

public class TileSapling extends TileBasic{

    public TileSapling(){
        super(AbstractGame.internalRes("sapling"));
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
    public void updateRandomly(IWorld world, int x, int y){
        WorldGenTrees trees = new WorldGenTrees();
        trees.generateAt(world, x, y, Util.RANDOM);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        if(super.canPlace(world, x, y, layer)){
            Tile tile = world.getTile(x, y-1);
            if(tile instanceof TileDirt || tile instanceof TileGrass){
                return true;
            }
        }
        return false;
    }
}
