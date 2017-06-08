package de.ellpeck.rockbottom.game.world.entity;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.data.set.DataSet;
import de.ellpeck.rockbottom.game.net.NetHandler;
import de.ellpeck.rockbottom.game.net.packet.toclient.PacketEntityUpdate;
import de.ellpeck.rockbottom.game.render.entity.IEntityRenderer;
import org.newdawn.slick.util.Log;

import java.util.UUID;

public class Entity extends MovableWorldObject{

    protected final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 0.5);

    public int chunkX;
    public int chunkY;

    public Direction facing = Direction.NONE;

    public int ticksExisted;
    public int fallAmount;
    protected boolean dead;
    protected UUID uniqueId;

    private double lastX;
    private double lastY;

    public Entity(IWorld world){
        super(world);
        this.uniqueId = UUID.randomUUID();
    }

    public static Entity create(String name, IWorld world){
        Class<? extends Entity> entityClass = RockBottomAPI.ENTITY_REGISTRY.get(name);

        try{
            return entityClass.getConstructor(IWorld.class).newInstance(world);
        }
        catch(Exception e){
            Log.error("Couldn't initialize entity with name "+name, e);
            return null;
        }
    }

    public UUID getUniqueId(){
        return this.uniqueId;
    }

    public IEntityRenderer getRenderer(){
        return null;
    }

    public void update(RockBottom game){
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
                    NetHandler.sendToAllPlayers(this.world, new PacketEntityUpdate(this.getUniqueId(), this.x, this.y, this.motionX, this.motionY));

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

    public void moveToChunk(IChunk chunk){
        this.chunkX = chunk.getGridX();
        this.chunkY = chunk.getGridY();
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
}
