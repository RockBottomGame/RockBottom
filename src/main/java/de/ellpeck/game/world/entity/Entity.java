package de.ellpeck.game.world.entity;

import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.net.packet.toclient.PacketEntityUpdate;
import de.ellpeck.game.render.entity.IEntityRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.util.Log;

import java.util.UUID;

public class Entity extends MovableWorldObject{

    protected final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 0.5);

    public int chunkX;
    public int chunkY;

    public Direction facing = Direction.NONE;

    public int ticksExisted;
    protected boolean dead;

    public int fallAmount;

    protected UUID uniqueId;

    private double lastX;
    private double lastY;

    public Entity(World world){
        super(world);
        this.uniqueId = UUID.randomUUID();
    }

    public UUID getUniqueId(){
        return this.uniqueId;
    }

    public IEntityRenderer getRenderer(){
        return null;
    }

    public void update(Game game){
        if(!this.isDead()){
            this.applyMotion();

            this.move(this.motionX, this.motionY);

            if(this.onGround){
                this.motionY = 0;

                if(this.fallAmount > 0){
                    this.onGroundHit();
                    this.fallAmount = 0;
                }
            }
            else if(this.motionY < 0){
                this.fallAmount++;
            }

            if(this.collidedHor){
                this.motionX = 0;
            }
        }
        else{
            this.motionX = 0;
            this.motionY = 0;
        }

        this.ticksExisted++;

        if(NetHandler.isServer()){
            if(this.ticksExisted%this.getUpdateFrequency() == 0){
                if(this.lastX != this.x || this.lastY != this.y){
                    NetHandler.sendToAllPlayers(this.world, new PacketEntityUpdate(this.getUniqueId(), this instanceof EntityPlayer, this.x, this.y, this.motionX, this.motionY));

                    this.lastX = this.x;
                    this.lastY = this.y;
                }
            }
        }
    }

    public int getUpdateFrequency(){
        return 40;
    }

    protected void applyMotion(){
        this.motionY -= 0.025;

        this.motionX *= 0.5;
        this.motionY *= 0.98;
    }

    public boolean isDead(){
        return this.dead;
    }

    public boolean shouldBeRemoved(){
        return this.isDead();
    }

    public void onRemoveFromWorld(){

    }

    public boolean shouldRender(){
        return !this.isDead();
    }

    public void setDead(boolean dead){
        this.dead = dead;
    }

    public void kill(){
        this.setDead(true);
    }

    public int getRenderPriority(){
        return 0;
    }

    public void onGroundHit(){

    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }

    public void moveToChunk(Chunk chunk){
        this.chunkX = chunk.gridX;
        this.chunkY = chunk.gridY;
    }

    public void save(DataSet set){
        set.addDouble("x", this.x);
        set.addDouble("y", this.y);
        set.addDouble("motion_x", this.motionX);
        set.addDouble("motion_y", this.motionY);
        set.addInt("ticks", this.ticksExisted);
        set.addBoolean("dead", this.isDead());
        set.addUniqueId("uuid", this.uniqueId);
    }

    public void load(DataSet set){
        this.x = set.getDouble("x");
        this.y = set.getDouble("y");
        this.motionX = set.getDouble("motion_x");
        this.motionY = set.getDouble("motion_y");
        this.ticksExisted = set.getInt("ticks");
        this.setDead(set.getBoolean("dead"));
        this.uniqueId = set.getUniqueId("uuid");
    }

    public boolean doesSave(){
        return true;
    }

    public static Entity create(int id, World world){
        Class<? extends Entity> entityClass = ContentRegistry.ENTITY_REGISTRY.get(id);

        try{
            return entityClass.getConstructor(World.class).newInstance(world);
        }
        catch(Exception e){
            Log.error("Couldn't initialize entity with id "+id, e);
            return null;
        }
    }
}
