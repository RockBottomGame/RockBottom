package de.ellpeck.rockbottom.api.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;

import java.util.UUID;

public class Entity extends MovableWorldObject{

    protected final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 0.5);

    public int chunkX;
    public int chunkY;

    public Direction facing = Direction.NONE;

    public int ticksExisted;
    public int fallAmount;
    public double lastX;
    public double lastY;
    protected boolean dead;
    protected UUID uniqueId;

    public Entity(IWorld world){
        super(world);
        this.uniqueId = UUID.randomUUID();
    }

    public UUID getUniqueId(){
        return this.uniqueId;
    }

    public IEntityRenderer getRenderer(){
        return null;
    }

    public void update(IGameInstance game){
        RockBottomAPI.getApiHandler().doDefaultEntityUpdate(this);
    }

    public int getUpdateFrequency(){
        return 40;
    }

    public void applyMotion(){
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
