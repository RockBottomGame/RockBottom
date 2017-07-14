package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.render.tile.GlowOreTileRenderer;

import java.util.Collections;
import java.util.List;

public class TileGlowOre extends TileBasic{

    public TileGlowOre(){
        super(AbstractGame.internalRes("glow_ore"));
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new GlowOreTileRenderer(name);
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer){
        return Collections.singletonList(new ItemInstance(GameContent.ITEM_GLOW_CLUSTER));
    }
}
