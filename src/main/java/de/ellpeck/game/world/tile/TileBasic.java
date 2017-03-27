package de.ellpeck.game.world.tile;

import de.ellpeck.game.render.tile.DefaultTileRenderer;
import de.ellpeck.game.render.tile.ITileRenderer;

public class TileBasic extends Tile{

    protected final ITileRenderer renderer;

    public TileBasic(int id, String name){
        super(id, name);
        this.renderer = new DefaultTileRenderer(name);
    }

    @Override
    public ITileRenderer getRenderer(){
        return this.renderer;
    }
}
