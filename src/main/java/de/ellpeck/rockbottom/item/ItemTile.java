package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.render.item.IItemRenderer;
import de.ellpeck.rockbottom.render.item.ItemTileRenderer;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.tile.Tile;

import java.util.List;

public class ItemTile extends ItemBasic{

    public ItemTile(int id, String name){
        super(id, name);
    }

    @Override
    protected IItemRenderer createRenderer(String name){
        return new ItemTileRenderer();
    }

    public Tile getTile(){
        return ContentRegistry.TILE_REGISTRY.get(this.id);
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
