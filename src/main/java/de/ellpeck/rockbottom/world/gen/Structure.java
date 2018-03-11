package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.gen.IStructure;

public class Structure implements IStructure{

    private final TileState[][] tiles;
    private final int width;
    private final int height;

    public Structure(TileState[][] tiles, int width, int height){
        this.tiles = tiles;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getWidth(){
        return this.width;
    }

    @Override
    public int getHeight(){
        return this.height;
    }

    @Override
    public TileState getTile(int x, int y){
        return this.tiles[x][y];
    }
}
