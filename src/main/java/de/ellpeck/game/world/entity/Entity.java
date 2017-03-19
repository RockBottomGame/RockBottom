package de.ellpeck.game.world.entity;

import de.ellpeck.game.Game;
import de.ellpeck.game.render.entity.IEntityRenderer;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.util.Direction;
import de.ellpeck.game.world.World;

import java.util.List;

public class Entity extends MovableWorldObject{

    protected final BoundBox boundingBox = new BoundBox(-0.5, -0.5, 0.5, 0.5);

    public int chunkX;
    public int chunkY;

    public Direction facing = Direction.NONE;

    protected boolean dead;

    public Entity(World world){
        super(world);
    }

    public IEntityRenderer getRenderer(){
        return null;
    }

    public void update(Game game){
        this.applyMotion();

        this.move(this.motionX, this.motionY);

        if(this.onGround){
            this.motionY = 0;
        }
        if(this.collidedHor){
            this.motionX = 0;
        }
    }

    protected void applyMotion(){
        this.motionY -= 0.025;

        this.motionX *= 0.5;
        this.motionY *= 0.98;
    }

    public boolean isDead(){
        return this.dead;
    }

    public void setDead(){
        this.dead = true;
    }

    public int getRenderPriority(){
        return 0;
    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }
}
