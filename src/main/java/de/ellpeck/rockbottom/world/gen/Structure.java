package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.gen.IStructure;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Map;
import java.util.Set;

public class Structure implements IStructure{

    private final Map<TileLayer, TileState[][]> tiles;
    private final int width;
    private final int height;

    public Structure(Map<TileLayer, TileState[][]> tiles, int width, int height){
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
        return this.getTile(TileLayer.MAIN, x, y);
    }

    @Override
    public TileState getTile(TileLayer layer, int x, int y){
        TileState[][] tiles = this.tiles.get(layer);
        if(tiles != null){
            return tiles[x][this.height-1-y];
        }
        else{
            return GameContent.TILE_AIR.getDefState();
        }
    }

    @Override
    public Set<TileLayer> getInvolvedLayers(){
        return this.tiles.keySet();
    }
}
