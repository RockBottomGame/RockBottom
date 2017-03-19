package de.ellpeck.game.item;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.render.item.IItemRenderer;

public class Item{

    protected final int id;

    public Item(int id){
        this.id = id;
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
}
