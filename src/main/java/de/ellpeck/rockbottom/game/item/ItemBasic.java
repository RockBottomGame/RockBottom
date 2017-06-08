package de.ellpeck.rockbottom.game.item;

import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.game.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.game.render.item.IItemRenderer;

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
