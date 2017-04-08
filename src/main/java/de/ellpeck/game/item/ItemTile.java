package de.ellpeck.game.item;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.render.item.IItemRenderer;
import de.ellpeck.game.render.item.ItemTileRenderer;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.tile.Tile;

import java.util.List;

public class ItemTile extends Item{

    private final IItemRenderer renderer;

    public ItemTile(int id, String name){
        super(id, name);
        this.renderer = new ItemTileRenderer();
    }

    public Tile getTile(){
        return ContentRegistry.TILE_REGISTRY.get(this.id);
    }

    @Override
    public IItemRenderer getRenderer(){
        return this.renderer;
    }

    @Override
    public int getId(){
        return this.id;
    }

    @Override
    public void describeItem(AssetManager manager, ItemInstance instance, List<String> desc){
        super.describeItem(manager, instance, desc);

        Tile tile = this.getTile();
        for(TileLayer layer : TileLayer.LAYERS){
            if(tile.canPlaceInLayer(layer)){
                desc.add("&2"+manager.localize("info.layer_placement", manager.localize(layer.name)));
            }
        }
    }
}
