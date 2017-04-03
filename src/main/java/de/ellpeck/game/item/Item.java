package de.ellpeck.game.item;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.render.item.IItemRenderer;

public class Item{

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
        ContentRegistry.ITEM_REGISTRY.register(this.id, this);
        return this;
    }

    @Override
    public int hashCode(){
        return this.getId();
    }

    public int getMaxAmount(){
        return 999;
    }

    public String getName(){
        return this.name;
    }

    public String getUnlocalizedName(ItemInstance instance){
        return "name."+this.name;
    }

    @Override
    public String toString(){
        return this.getName()+"@"+this.getId();
    }
}
