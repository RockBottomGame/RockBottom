package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.render.tile.ITileRenderer;

public class TileBasic extends Tile{

    protected final ITileRenderer renderer;

    public TileBasic(String name){
        super(name);
        this.renderer = this.createRenderer(name);
    }

    protected ITileRenderer createRenderer(String name){
        return new DefaultTileRenderer(name);
    }

    @Override
    public ITileRenderer getRenderer(){
        return this.renderer;
    }
}
