package de.ellpeck.game.item;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.render.item.IItemRenderer;
import de.ellpeck.game.render.item.ItemTileRenderer;
import de.ellpeck.game.world.tile.Tile;

public class ItemTile extends Item{

    private final IItemRenderer renderer;

    public ItemTile(int id){
        super(id);
        this.renderer = new ItemTileRenderer();
    }

    public Tile getTile(){
        return ContentRegistry.TILE_REGISTRY.get(this.id);
    }

    @Override
    public IItemRenderer getRenderer(){
        return this.renderer;
    }
}
