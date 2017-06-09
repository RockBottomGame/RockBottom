package de.ellpeck.rockbottom.api.item;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Item{

    protected final IResourceName name;
    protected final IResourceName unlocName;

    protected int maxAmount = 999;

    public Item(IResourceName name){
        this.name = name;
        this.unlocName = this.name.addPrefix("item.");
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

    public IResourceName getName(){
        return this.name;
    }

    public IResourceName getUnlocalizedName(ItemInstance instance){
        return this.unlocName;
    }

    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        desc.add(instance.getDisplayName());
    }

    @Override
    public String toString(){
        return this.getName().toString();
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
