package de.ellpeck.rockbottom.api.item;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.render.item.ItemTileRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.util.List;

public class ItemTile extends ItemBasic{

    public ItemTile(IResourceName name){
        super(name);
    }

    @Override
    protected IItemRenderer createRenderer(IResourceName name){
        return new ItemTileRenderer();
    }

    public Tile getTile(){
        return RockBottomAPI.TILE_REGISTRY.get(this.getName());
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        super.describeItem(manager, instance, desc, isAdvanced);

        this.getTile().describeItem(manager, instance, desc, isAdvanced);
    }
}
