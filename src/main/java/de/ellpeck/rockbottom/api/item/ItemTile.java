package de.ellpeck.rockbottom.api.item;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.item.ItemBasic;
import de.ellpeck.rockbottom.game.render.item.IItemRenderer;
import de.ellpeck.rockbottom.game.render.item.ItemTileRenderer;

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
        return RockBottomAPI.TILE_REGISTRY.get(this.getName());
    }

    @Override
    public void describeItem(AssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        super.describeItem(manager, instance, desc, isAdvanced);

        this.getTile().describeItem(manager, instance, desc, isAdvanced);
    }
}
