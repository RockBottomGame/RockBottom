package de.ellpeck.game.particle;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.util.BoundBox;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.MovableWorldObject;
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

    public void update(Game game){
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
        this.motionY -= 0.025;

        this.motionX *= 0.98;
        this.motionY *= 0.98;
    }

    public void render(Game game, AssetManager manager, Graphics g, float x, float y, Color filter){

    }

    public boolean isDead(){
        return this.dead;
    }

    public void setDead(){
        this.dead = true;
    }
}
