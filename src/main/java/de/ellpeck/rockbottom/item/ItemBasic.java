package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.render.item.IItemRenderer;

public class ItemBasic extends Item{

    private final IItemRenderer renderer;

    public ItemBasic(int id, String name){
        super(id, name);
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
