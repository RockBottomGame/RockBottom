package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.render.item.GlowClusterItemRenderer;

public class ItemGlowCluster extends ItemBasic{

    public ItemGlowCluster(){
        super(AbstractGame.internalRes("glow_cluster"));
    }

    @Override
    protected IItemRenderer createRenderer(IResourceName name){
        return new GlowClusterItemRenderer(name);
    }
}
