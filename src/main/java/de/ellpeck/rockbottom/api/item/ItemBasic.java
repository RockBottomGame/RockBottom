package de.ellpeck.rockbottom.api.item;

import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;

public class ItemBasic extends Item{

    private final IItemRenderer renderer;

    public ItemBasic(String name){
        super(name);
        this.renderer = this.createRenderer(name);
    }

    protected IItemRenderer createRenderer(String name){
        return new DefaultItemRenderer(name);
    }

    @Override
    public IItemRenderer getRenderer(){
        return this.renderer;
    }
}
