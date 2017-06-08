package de.ellpeck.rockbottom.game.world.tile;

import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.game.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.game.render.tile.DefaultTileRenderer;

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
