package de.ellpeck.rockbottom.api.tile;

import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class TileBasic extends Tile{

    protected final ITileRenderer renderer;

    public TileBasic(IResourceName name){
        super(name);
        this.renderer = this.createRenderer(name);
    }

    protected ITileRenderer createRenderer(IResourceName name){
        return new DefaultTileRenderer(name);
    }

    @Override
    public ITileRenderer getRenderer(){
        return this.renderer;
    }
}
