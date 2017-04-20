package de.ellpeck.game.world.entity;

import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.world.World;

public abstract class EntityLiving extends Entity{

    protected int health;
    protected boolean jumping;

    public EntityLiving(World world){
        super(world);
        this.health = this.getMaxHealth();
    }

    @Override
    public void update(Game game){
        super.update(game);

        if(this.jumping && this.collidedVert){
            this.motionY = 0;
            this.jumping = false;
        }

        if(this.health <= 0){
            this.kill();
        }
        else{
            if(!NetHandler.isClient()){
                if(this.health < this.getMaxHealth()){
                    if(this.world.info.totalTimeInWorld%this.getRegenRate() == 0){
                        this.health++;
                    }
                }
            }
        }
    }

    public void jump(double motion){
        if(this.onGround && !this.jumping){
            this.motionY += motion;
            this.jumping = true;
        }
    }

    public int getHealth(){
        return this.health;
    }

    public void setHealth(int health){
        this.health = health;
    }

    public abstract int getMaxHealth();

    public abstract int getRegenRate();

    @Override
    public void save(DataSet set){
        super.save(set);

        set.addBoolean("jumping", this.jumping);
        set.addInt("health", this.health);
    }

    @Override
    public void load(DataSet set){
        super.load(set);

        this.jumping = set.getBoolean("jumping");
        this.health = set.getInt("health");
    }
}
