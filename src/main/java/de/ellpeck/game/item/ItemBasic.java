package de.ellpeck.game.item;

import de.ellpeck.game.render.item.DefaultItemRenderer;
import de.ellpeck.game.render.item.IItemRenderer;

public class ItemBasic extends Item{

    private final IItemRenderer renderer;

    public ItemBasic(int id, String name){
        super(id);
        this.renderer = new DefaultItemRenderer(name);
    }

    @Override
    public IItemRenderer getRenderer(){
        return this.renderer;
    }
}
