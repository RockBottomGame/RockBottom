package de.ellpeck.rockbottom.api.item;

import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public class ItemBasic extends Item{

    private final IItemRenderer renderer;

    public ItemBasic(IResourceName name){
        super(name);
        this.renderer = this.createRenderer(name);
    }

    protected IItemRenderer createRenderer(IResourceName name){
        return new DefaultItemRenderer(name);
    }

    @Override
    public IItemRenderer getRenderer(){
        return this.renderer;
    }
}
