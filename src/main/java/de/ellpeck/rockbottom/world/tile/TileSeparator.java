package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.gui.GuiSeparator;
import de.ellpeck.rockbottom.gui.container.ContainerSeparator;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.render.tile.SeparatorTileRenderer;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.util.Pos2;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySeparator;

public class TileSeparator extends MultiTile{

    public TileSeparator(int id){
        super(id, "separator");
    }

    @Override
    protected ITileRenderer createRenderer(String name){
        return new SeparatorTileRenderer(name);
    }

    @Override
    public boolean canProvideTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(World world, int x, int y){
        return this.isMainPos(x, y, world.getMeta(x, y)) ? new TileEntitySeparator(world, x, y) : null;
    }

    @Override
    public int getLight(World world, int x, int y, TileLayer layer){
        if(this.isMainPos(x, y, world.getMeta(x, y))){
            TileEntitySeparator tile = world.getTileEntity(x, y, TileEntitySeparator.class);
            if(tile != null && tile.isActive()){
                return 30;
            }
        }
        return 0;
    }

    @Override
    public boolean onInteractWith(World world, int x, int y, EntityPlayer player){
        Pos2 main = this.getMainPos(x, y, world.getMeta(x, y));
        TileEntitySeparator tile = world.getTileEntity(main.getX(), main.getY(), TileEntitySeparator.class);

        if(tile != null){
            player.openGuiContainer(new GuiSeparator(player, tile), new ContainerSeparator(player, tile));
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
            Pos2 main = this.getMainPos(x, y, world.getMeta(x, y));
            TileEntitySeparator tile = world.getTileEntity(main.getX(), main.getY(), TileEntitySeparator.class);
            if(tile != null){
                tile.dropInventory(tile.inventory);
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

    @Override
    protected boolean[][] makeStructure(){
        return new boolean[][]{
                {false, true},
                {true, true},
                {true, true},
        };
    }

    @Override
    public int getWidth(){
        return 2;
    }

    @Override
    public int getHeight(){
        return 3;
    }

    @Override
    public int getMainX(){
        return 0;
    }

    @Override
    public int getMainY(){
        return 0;
    }
}
