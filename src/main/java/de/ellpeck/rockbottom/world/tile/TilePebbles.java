package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ItemTile;
import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;

import java.util.Collections;
import java.util.List;

public class TilePebbles extends TileBasic{

    public TilePebbles(){
        super(ResourceName.intern("pebbles"));
    }

    @Override
    protected ItemTile createItemTile(){
        return new ItemTile(this.getName()){
            @Override
            protected IItemRenderer createRenderer(ResourceName name){
                return new DefaultItemRenderer(name);
            }

            @Override
            public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance){
                if(instance.getAmount() >= 3){
                    if(super.onInteractWith(world, x, y, layer, mouseX, mouseY, player, instance)){
                        player.getInv().remove(player.getSelectedSlot(), 2);
                        return true;
                    }
                }
                return false;
            }
        };
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y, TileLayer layer){
        return null;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        return world.getState(layer, x, y-1).getTile().hasSolidSurface(world, x, y-1, layer);
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractEntityPlayer player){
        return world.isPosLoaded(x, y-1,false) && world.getState(layer, x, y-1).getTile().hasSolidSurface(world, x, y-1, layer);
    }

    @Override
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop){
        super.onDestroyed(world, x, y, destroyer, layer, shouldDrop);

        if(!world.isClient() && destroyer instanceof AbstractEntityPlayer){
            AbstractEntityPlayer player = (AbstractEntityPlayer)destroyer;
            for(IRecipe recipe : ConstructionRegistry.BRITTLE_TOOLS){
                if(recipe != null){
                    player.getKnowledge().teachRecipe(recipe, true);
                }
            }
        }
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer){
        return Collections.singletonList(new ItemInstance(this, Util.RANDOM.nextInt(3)+1));
    }
}
