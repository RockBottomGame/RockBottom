package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.item.FireworkItemRenderer;
import de.ellpeck.rockbottom.world.entity.EntityFirework;

public class ItemFirework extends ItemBasic{

    public ItemFirework(){
        super(ResourceName.intern("firework"));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance){
        if(!world.getState(x, y).getTile().isFullTile()){
            if(!world.isClient()){
                EntityFirework firework = new EntityFirework(world);
                firework.setPos(x+0.5, y+0.5);
                firework.motionX = Util.RANDOM.nextGaussian()*0.05;
                world.addEntity(firework);

                player.getInv().remove(player.getSelectedSlot(), 1);
            }
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    protected IItemRenderer createRenderer(ResourceName name){
        return new FireworkItemRenderer(name);
    }
}
