package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.util.BoundBox;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.MovableWorldObject;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class Particle extends MovableWorldObject{

    protected final BoundBox boundingBox = new BoundBox(-0.1, -0.1, 0.1, 0.1);

    protected int maxLife;
    protected int life;

    protected boolean dead;

    public Particle(World world, double x, double y, double motionX, double motionY, int maxLife){
        super(world);
        this.motionX = motionX;
        this.motionY = motionY;
        this.maxLife = maxLife;

        this.setPos(x, y);
    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }

    public void update(RockBottom game){
        this.life++;

        if(this.life >= this.maxLife){
            this.setDead();
        }

        this.applyMotion();

        this.move(this.motionX, this.motionY);

        if(this.onGround){
            this.motionY = 0;
        }

        if(this.onGround || this.collidedHor){
            this.motionX = 0;
        }
    }

    protected void applyMotion(){
        this.motionY -= 0.02;

        this.motionX *= 0.99;
        this.motionY *= 0.99;
    }

    public void render(RockBottom game, AssetManager manager, Graphics g, float x, float y, Color filter){

    }

    public boolean isDead(){
        return this.dead;
    }

    public void setDead(){
        this.dead = true;
    }
}
