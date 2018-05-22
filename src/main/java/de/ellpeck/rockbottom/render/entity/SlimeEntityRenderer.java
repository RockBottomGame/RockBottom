package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAnimation;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.EntitySlime;

public class SlimeEntityRenderer implements IEntityRenderer<EntitySlime>{

    private final ResourceName animation = ResourceName.intern("monster.slime");

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, EntitySlime entity, float x, float y, int light){
        int row = entity.getVariation()*4;
        long startTime = 0;

        if(entity.facing == Direction.LEFT){
            row += 2;
        }

        if(!entity.jumping && entity.jumpTask.chargeTime <= 0){
            row += 1;
        }
        else{
            startTime = entity.jumpTask.jumpStartTime;
        }

        IAnimation animation = manager.getAnimation(this.animation);
        animation.drawRow(startTime, row, x-0.5F, y-0.5F, 1F, 1F, light);
    }
}
