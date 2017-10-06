package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemTile;
import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class TilePebbles extends TileBasic{

    public TilePebbles(){
        super(RockBottomAPI.createInternalRes("pebbles"));
    }

    @Override
    protected ItemTile createItemTile(){
        return new ItemTile(this.getName()){
            @Override
            protected IItemRenderer createRenderer(IResourceName name){
                return new DefaultItemRenderer(name);
            }
        };
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        return world.getState(layer, x, y-1).getTile().isFullTile();
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return world.getState(layer, x, y-1).getTile().isFullTile();
    }

    @Override
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop){
        super.onDestroyed(world, x, y, destroyer, layer, shouldDrop);

        if(!world.isClient() && destroyer instanceof AbstractEntityPlayer){
            AbstractEntityPlayer player = (AbstractEntityPlayer)destroyer;
        }
    }
}
