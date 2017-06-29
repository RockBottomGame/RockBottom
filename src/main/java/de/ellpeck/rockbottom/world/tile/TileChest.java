package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.gui.GuiChest;
import de.ellpeck.rockbottom.gui.container.ContainerChest;
import de.ellpeck.rockbottom.render.tile.ChestTileRenderer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;

public class TileChest extends TileBasic{

    public TileChest(){
        super(AbstractGame.internalRes("chest"));
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new ChestTileRenderer(name);
    }

    @Override
    public boolean canProvideTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y){
        return new TileEntityChest(world, x, y);
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, AbstractEntityPlayer player){
        TileEntityChest chest = world.getTileEntity(x, y, TileEntityChest.class);
        if(chest != null){
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
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer){
        return super.canPlace(world, x, y, layer) && world.getTile(x, y-1).isFullTile();
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        if(layer == changedLayer){
            if(!world.getTile(layer, x, y-1).isFullTile()){
                world.destroyTile(x, y, layer, null, true);
            }
        }
    }

    @Override
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean forceDrop){
        super.onDestroyed(world, x, y, destroyer, layer, forceDrop);

        if(!RockBottomAPI.getNet().isClient()){
            TileEntityChest chest = world.getTileEntity(x, y, TileEntityChest.class);
            if(chest != null){
                chest.dropInventory(chest.inventory);
            }
        }
    }

    @Override
    public boolean isFullTile(){
        return false;
    }
}
