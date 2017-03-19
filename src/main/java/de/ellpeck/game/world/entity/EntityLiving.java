package de.ellpeck.game.world.entity;

import de.ellpeck.game.Game;
import de.ellpeck.game.world.World;
import org.newdawn.slick.GameContainer;

public class EntityLiving extends Entity{

    protected boolean jumping;

    public EntityLiving(World world){
        super(world);
    }

    @Override
    public void update(Game game){
        super.update(game);

        if(this.jumping && this.collidedVert){
            this.motionY = 0;
            this.jumping = false;
        }
    }

    public void jump(double motion){
        if(this.onGround && !this.jumping){
            this.motionY += motion;
            this.jumping = true;
        }
    }
}
