package de.ellpeck.game.world.tile;

import de.ellpeck.game.render.tile.DefaultTileRenderer;
import de.ellpeck.game.render.tile.ITileRenderer;

public class TileBasic extends Tile{

    private final ITileRenderer renderer;

    public TileBasic(int id, String name){
        super(id);
        this.renderer = new DefaultTileRenderer(name);
    }

    @Override
    public ITileRenderer getRenderer(){
        return this.renderer;
    }
}
