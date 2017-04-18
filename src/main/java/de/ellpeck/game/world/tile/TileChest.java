package de.ellpeck.game.world.tile;

import de.ellpeck.game.gui.GuiChest;
import de.ellpeck.game.gui.container.ContainerChest;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.render.tile.ChestTileRenderer;
import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Util;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.tile.entity.TileEntity;
import de.ellpeck.game.world.tile.entity.TileEntityChest;

public class TileChest extends TileBasic{

    public TileChest(int id){
        super(id, "chest");
    }

    @Override
    protected ITileRenderer createRenderer(String name){
        return new ChestTileRenderer();
    }

    @Override
    public boolean providesTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(World world, int x, int y){
        return new TileEntityChest(world, x, y);
    }

    @Override
    public boolean onInteractWith(World world, int x, int y, EntityPlayer player){
        TileEntity tile = world.getTileEntity(x, y);
        if(tile instanceof TileEntityChest){
            TileEntityChest chest = (TileEntityChest)tile;

            player.openGuiContainer(new GuiChest(player), new ContainerChest(player, chest));

            return true;
        }
        return false;
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return null;
    }

    @Override
    public boolean canPlace(World world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && world.getTile(x, y-1).isFullTile();
    }

    @Override
    public void onChangeAround(World world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(layer == changedLayer){
            if(!world.getTile(layer, x, y-1).isFullTile()){
                world.destroyTile(x, y, layer, null, true);
            }
        }
    }

    @Override
    public void onDestroyed(World world, int x, int y, Entity destroyer, TileLayer layer, boolean forceDrop){
        super.onDestroyed(world, x, y, destroyer, layer, forceDrop);

        if(!NetHandler.isClient()){
            TileEntity tile = world.getTileEntity(x, y);
            if(tile instanceof TileEntityChest){
                TileEntityChest chest = (TileEntityChest)tile;

                for(int i = 0; i < chest.inventory.getSlotAmount(); i++){
                    ItemInstance inst = chest.inventory.get(i);
                    if(inst != null){
                        EntityItem.spawn(world, inst, x+0.5, y+0.5, Util.RANDOM.nextGaussian()*0.1, Util.RANDOM.nextGaussian()*0.1);
                    }
                }
            }
        }
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
