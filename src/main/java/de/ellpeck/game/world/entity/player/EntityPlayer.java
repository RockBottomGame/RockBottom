package de.ellpeck.game.world.entity.player;

import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.gui.container.ContainerInventory;
import de.ellpeck.game.gui.container.ItemContainer;
import de.ellpeck.game.inventory.InventoryPlayer;
import de.ellpeck.game.item.ItemInstance;
import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.packet.IPacket;
import de.ellpeck.game.render.entity.IEntityRenderer;
import de.ellpeck.game.render.entity.PlayerEntityRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.EntityItem;
import de.ellpeck.game.world.entity.EntityLiving;
import io.netty.channel.Channel;
import org.newdawn.slick.util.Log;

import java.util.List;
import java.util.UUID;

public class EntityPlayer extends EntityLiving{

    private final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 1.5);

    private final IEntityRenderer renderer;
    public final InventoryPlayer inv = new InventoryPlayer();

    public final ItemContainer invContainer = new ContainerInventory(this);
    private ItemContainer currentContainer;

    private int respawnTimer;

    public EntityPlayer(World world){
        super(world);
        this.renderer = new PlayerEntityRenderer();
        this.facing = Direction.RIGHT;
    }

    public EntityPlayer(World world, UUID uniqueId){
        this(world);
        this.uniqueId = uniqueId;
    }

    @Override
    public IEntityRenderer getRenderer(){
        return this.renderer;
    }

    public void openContainer(ItemContainer container){
        this.currentContainer = container;

        if(this.currentContainer == null){
            Log.info("Closed Container");
        }
        else{
            Log.info("Opened Container "+this.currentContainer);
        }
    }

    public void closeContainer(){
        this.openContainer(null);
    }

    public ItemContainer getContainer(){
        return this.currentContainer;
    }

    @Override
    public void update(Game game){
        super.update(game);

        if(this.isDead()){
            this.respawnTimer++;

            if(this.respawnTimer >= 400){
                this.resetAndSpawn(game);
            }
        }
        else{
            if(!NetHandler.isClient()){
                List<EntityItem> entities = this.world.getEntities(this.getBoundingBox().copy().add(this.x, this.y), EntityItem.class);
                for(EntityItem entity : entities){
                    if(entity.canPickUp()){
                        ItemInstance left = this.inv.addExistingFirst(entity.item, false);

                        if(left == null){
                            entity.kill();
                        }
                        else{
                            entity.item = left;
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getUpdateFrequency(){
        return 2;
    }

    public void resetAndSpawn(Game game){
        this.respawnTimer = 0;
        this.dead = false;
        this.motionX = 0;
        this.motionY = 0;
        this.fallAmount = 0;
        this.health = this.getMaxHealth();

        if(game.guiManager != null){
            game.guiManager.closeGui();
        }

        this.setPos(this.world.spawnX, this.world.spawnY);
    }

    @Override
    public void onGroundHit(){
        if(this.fallAmount >= 20){
            this.health -= this.fallAmount*1.5;
        }
    }

    @Override
    public boolean shouldBeRemoved(){
        return false;
    }

    @Override
    public int getMaxHealth(){
        return 100;
    }

    @Override
    public int getRegenRate(){
        return 10;
    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }

    @Override
    public void save(DataSet set){
        super.save(set);
        this.inv.save(set);
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.inv.load(set);
    }

    public void setChannel(Channel channel){

    }

    public void sendPacket(IPacket packet){

    }

    public void onKeepLoaded(Chunk chunk){

    }

    public void move(int type){
        if(type == 0){
            this.motionX -= 0.2;
            this.facing = Direction.LEFT;
        }
        else if(type == 1){
            this.motionX += 0.2;
            this.facing = Direction.RIGHT;
        }
        else if(type == 2){
            this.jump(0.28);
        }
    }
}
