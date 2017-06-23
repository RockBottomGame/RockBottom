package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.anim.Animation;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerEntityRenderer implements IEntityRenderer<EntityPlayer>{

    private static final IResourceName ANIM_BASE = RockBottom.internalRes("player.base.male_white");
    private static final IResourceName ANIM_ARMS = RockBottom.internalRes("player.arm.hanging_white");

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, EntityPlayer entity, float x, float y, Color light){
        x -= 0.5F;
        y -= 1.5F;

        boolean isMoving = Math.abs(entity.motionX) >= 0.01;
        int row = entity.facing == Direction.RIGHT ? (isMoving ? 0 : 2) : (isMoving ? 1 : 3);
        int time = entity.ticksExisted;

        Animation base = manager.getAnimation(ANIM_BASE);
        Animation arms = manager.getAnimation(ANIM_ARMS);

        base.drawRow(time, row, x, y, 1F, light);
        arms.drawRow(time, row, x, y, 1F, light);
    }
}
