package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.render.item.IItemRenderer;

public class ItemBasic extends Item{

    private final IItemRenderer renderer;

    public ItemBasic(int id, String name){
        super(id, name);
        this.renderer = new DefaultItemRenderer(name);
    }

    @Override
    public IItemRenderer getRenderer(){
        return this.renderer;
    }
}
