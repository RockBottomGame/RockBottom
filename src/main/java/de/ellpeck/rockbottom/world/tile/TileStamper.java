package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.StamperRecipe;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.BoolProp;
import de.ellpeck.rockbottom.api.tile.state.TileProp;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.render.tile.StamperTileRenderer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityStamper;

public class TileStamper extends TileBasic{

    public static final BoolProp DOWN_PROP = new BoolProp("down", false);
    private final BoundBox downBox = new BoundBox(0, 0, 1, 10D/12D);

    public TileStamper(){
        super(AbstractGame.internalRes("stamper"));
    }

    @Override
    protected ITileRenderer createRenderer(IResourceName name){
        return new StamperTileRenderer(name);
    }

    @Override
    public BoundBox getBoundBox(IWorld world, int x, int y){
        return world.getState(x, y).get(DOWN_PROP) ? this.downBox : super.getBoundBox(world, x, y);
    }

    @Override
    public boolean canProvideTileEntity(){
        return true;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y){
        return new TileEntityStamper(world, x, y);
    }

    @Override
    public void onCollideWithEntity(IWorld world, int x, int y, TileLayer layer, Entity entity){
        if(!world.isClient()){
            if(Util.floor(entity.x) == x && entity.motionY <= -0.2 && !world.getState(x, y).get(DOWN_PROP)){
                world.setState(x, y, this.getDefStateWithProp(DOWN_PROP, true));
                world.scheduleUpdate(x, y, layer, 40);

                TileEntityStamper tile = world.getTileEntity(x, y, TileEntityStamper.class);
                if(tile != null){
                    ItemInstance inst = tile.inventory.get(0);
                    if(inst != null){
                        StamperRecipe recipe = RockBottomAPI.getStamperRecipe(inst);
                        if(recipe != null && inst.getAmount() >= recipe.getInput().getAmount()){
                            for(ItemInstance output : recipe.getOutputs()){
                                EntityItem.spawn(world, output.copy(), x+0.5, y+0.35, Util.RANDOM.nextGaussian()*0.1, Util.RANDOM.nextGaussian()*0.1);
                            }

                            inst.removeAmount(recipe.getInput().getAmount());
                            if(inst.getAmount() <= 0){
                                tile.inventory.set(0, null);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, double mouseX, double mouseY, AbstractEntityPlayer player){
        TileEntityStamper tile = world.getTileEntity(x, y, TileEntityStamper.class);
        if(tile != null){
            ItemInstance playerInst = player.getInv().get(player.getSelectedSlot());
            if(playerInst != null && RockBottomAPI.getStamperRecipe(playerInst) != null){
                ItemInstance inst = tile.inventory.get(0);
                if(inst == null || (playerInst.isEffectivelyEqualWithWildcard(inst) && inst.fitsAmount(1))){
                    if(!world.isClient()){
                        if(inst != null){
                            inst.addAmount(1);
                        }
                        else{
                            tile.inventory.set(0, playerInst.copy().setAmount(1));
                        }
                        tile.sendToClients();

                        playerInst.removeAmount(1);
                        if(playerInst.getAmount() <= 0){
                            player.getInv().set(player.getSelectedSlot(), null);
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isFullTile(){
        return false;
    }

    @Override
    public void onScheduledUpdate(IWorld world, int x, int y, TileLayer layer){
        if(!world.isClient() && world.getState(x, y).get(DOWN_PROP)){
            world.setState(x, y, this.getDefStateWithProp(DOWN_PROP, false));

            for(Entity entity : world.getEntities(new BoundBox(0, 0, 1, 1).add(x, y))){
                entity.motionY += 0.2;
            }
        }
    }

    @Override
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop){
        super.onDestroyed(world, x, y, destroyer, layer, shouldDrop);

        if(!world.isClient()){
            TileEntityStamper tile = world.getTileEntity(x, y, TileEntityStamper.class);
            if(tile != null){
                tile.dropInventory(tile.inventory);
            }
        }
    }

    @Override
    public TileProp[] getProperties(){
        return new TileProp[]{DOWN_PROP};
    }
}
