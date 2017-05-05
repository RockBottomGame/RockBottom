package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.gui.GuiSmelter;
import de.ellpeck.rockbottom.gui.container.ContainerSmelter;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.render.tile.SmelterTileRenderer;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmelter;

public class TileSmelter extends TileBasic{

    public TileSmelter(int id){
        super(id, "smelter");
    }

    @Override
    protected ITileRenderer createRenderer(String name){
        return new SmelterTileRenderer(name);
    }

    @Override
    public boolean canProvideTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(World world, int x, int y){
        return world.getMeta(x, y) == 1 ? new TileEntitySmelter(world, x, y) : null;
    }

    @Override
    public boolean canPlace(World world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && world.getTile(x, y+1).canReplace(world, x, y+1, layer, this);
    }

    @Override
    public void doPlace(World world, int x, int y, TileLayer layer, ItemInstance instance, EntityPlayer placer){
        super.doPlace(world, x, y, layer, instance, placer);
        world.setTile(x, y+1, this);
    }

    @Override
    public int getPlacementMeta(World world, int x, int y, TileLayer layer, ItemInstance instance){
        return 1;
    }

    @Override
    public int getLight(World world, int x, int y, TileLayer layer){
        TileEntitySmelter tile = world.getTileEntity(x, y, TileEntitySmelter.class);
        return tile != null && tile.isActive() ? 20 : 0;
    }

    @Override
    public void doBreak(World world, int x, int y, TileLayer layer, EntityPlayer breaker, boolean isRightTool){
        if(world.getMeta(x, y) == 1){
            world.destroyTile(x, y+1, layer, breaker, false);
        }
        else{
            world.destroyTile(x, y-1, layer, breaker, false);
        }

        super.doBreak(world, x, y, layer, breaker, isRightTool);
    }

    @Override
    public boolean onInteractWith(World world, int x, int y, EntityPlayer player){
        TileEntitySmelter tile;

        if(world.getMeta(x, y) == 1){
            tile = world.getTileEntity(x, y, TileEntitySmelter.class);
        }
        else{
            tile = world.getTileEntity(x, y-1, TileEntitySmelter.class);
        }

        if(tile != null){
            player.openGuiContainer(new GuiSmelter(player, tile), new ContainerSmelter(player, tile));
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onDestroyed(World world, int x, int y, Entity destroyer, TileLayer layer, boolean forceDrop){
        super.onDestroyed(world, x, y, destroyer, layer, forceDrop);

        if(!NetHandler.isClient()){
            TileEntitySmelter smelter = world.getTileEntity(x, y, TileEntitySmelter.class);
            if(smelter != null){
                smelter.dropInventory(smelter.inventory);
            }
        }
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
