package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.ContentRegistry;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.render.item.IItemRenderer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Item{

    protected int maxAmount = 999;

    protected final String name;
    protected final int id;

    public Item(int id, String name){
        this.id = id;
        this.name = name;
    }

    public IItemRenderer getRenderer(){
        return null;
    }

    public int getId(){
        return this.id;
    }

    public Item register(){
        ContentRegistry.ITEM_REGISTRY.register(this.getId(), this);
        return this;
    }

    @Override
    public int hashCode(){
        return this.getId();
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

    public void describeItem(AssetManager manager, ItemInstance instance, List<String> desc){
        desc.add(instance.getDisplayName());
    }

    @Override
    public String toString(){
        return this.getName()+"@"+this.getId();
    }

    public int getDespawnTime(ItemInstance instance){
        return 24000;
    }

    public Map<ToolType, Integer> getToolTypes(ItemInstance instance){
        return Collections.emptyMap();
    }
}
