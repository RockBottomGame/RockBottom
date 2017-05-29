package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.gui.GuiSmelter;
import de.ellpeck.rockbottom.gui.container.ContainerSmelter;
import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.particle.ParticleManager;
import de.ellpeck.rockbottom.particle.ParticleSmoke;
import de.ellpeck.rockbottom.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.render.tile.SmelterTileRenderer;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.util.Pos2;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.Entity;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntity;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmelter;

public class TileSmelter extends MultiTile{

    public TileSmelter(){
        super("smelter");
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
        return this.isMainPos(x, y, world.getMeta(x, y)) ? new TileEntitySmelter(world, x, y) : null;
    }

    @Override
    public int getLight(World world, int x, int y, TileLayer layer){
        if(this.isMainPos(x, y, world.getMeta(x, y))){
            TileEntitySmelter tile = world.getTileEntity(x, y, TileEntitySmelter.class);
            if(tile != null && tile.isActive()){
                return 20;
            }
        }
        return 0;
    }

    @Override
    public boolean onInteractWith(World world, int x, int y, EntityPlayer player){
        Pos2 main = this.getMainPos(x, y, world.getMeta(x, y));
        TileEntitySmelter tile = world.getTileEntity(main.getX(), main.getY(), TileEntitySmelter.class);

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
            Pos2 main = this.getMainPos(x, y, world.getMeta(x, y));
            TileEntitySmelter tile = world.getTileEntity(main.getX(), main.getY(), TileEntitySmelter.class);
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
                {true},
                {true}
        };
    }

    @Override
    public int getWidth(){
        return 1;
    }

    @Override
    public int getHeight(){
        return 2;
    }

    @Override
    public int getMainX(){
        return 0;
    }

    @Override
    public int getMainY(){
        return 0;
    }

    @Override
    public void updateRandomlyForRendering(World world, int x, int y, TileLayer layer, EntityPlayer player){
        if(this.isMainPos(x, y, world.getMeta(x, y))){
            TileEntitySmelter tile = world.getTileEntity(x, y, TileEntitySmelter.class);
            if(tile != null && tile.isActive()){
                ParticleManager manager = RockBottom.get().particleManager;

                if(Util.RANDOM.nextFloat() >= 0.25F){
                    manager.addParticle(new ParticleSmoke(world, x+0.4, y+2, Util.RANDOM.nextGaussian()*0.01, 0, 0.08F));
                }
                if(Util.RANDOM.nextBoolean()){
                    manager.addParticle(new ParticleSmoke(world, x+0.7, y+1.54, Util.RANDOM.nextGaussian()*0.01, 0.05, 0.05F));
                }
            }
        }
    }
}
