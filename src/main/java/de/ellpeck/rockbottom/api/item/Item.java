package de.ellpeck.rockbottom.api.item;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Item{

    protected final String name;
    protected int maxAmount = 999;

    public Item(String name){
        this.name = name;
    }

    public IItemRenderer getRenderer(){
        return null;
    }

    public Item register(){
        RockBottomAPI.ITEM_REGISTRY.register(this.getName(), this);
        return this;
    }

    public int getMaxAmount(){
        return this.maxAmount;
    }

    public String getName(){
        return this.name;
    }

    public String getUnlocalizedName(ItemInstance instance){
        return "name."+this.name;
    }

    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        desc.add(instance.getDisplayName());
    }

    @Override
    public String toString(){
        return this.getName();
    }

    public int getDespawnTime(ItemInstance instance){
        return 24000;
    }

    public Map<ToolType, Integer> getToolTypes(ItemInstance instance){
        return Collections.emptyMap();
    }

    public float getMiningSpeed(IWorld world, int x, int y, TileLayer layer, Tile tile, boolean isRightTool){
        return 1F;
    }
}
