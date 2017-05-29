package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.render.item.IItemRenderer;
import de.ellpeck.rockbottom.render.item.ItemTileRenderer;
import de.ellpeck.rockbottom.world.tile.Tile;

import java.util.List;

public class ItemTile extends ItemBasic{

    public ItemTile(String name){
        super(name);
    }

    @Override
    protected IItemRenderer createRenderer(String name){
        return new ItemTileRenderer();
    }

    public Tile getTile(){
        return ContentRegistry.TILE_REGISTRY.get(this.getName());
    }

    @Override
    public void describeItem(AssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        super.describeItem(manager, instance, desc, isAdvanced);

        this.getTile().describeItem(manager, instance, desc, isAdvanced);
    }
}
