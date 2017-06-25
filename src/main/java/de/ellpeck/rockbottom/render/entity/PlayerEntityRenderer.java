package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerEntityRenderer implements IEntityRenderer<EntityPlayer>{

    private static final IResourceName SPECIAL_BASE = RockBottom.internalRes("player.base.male_skin_s");
    private static final IResourceName SPECIAL_ARMS = RockBottom.internalRes("player.arm.skin_s");

    public static void renderPlayer(IAssetManager manager, IPlayerDesign design, float x, float y, float scale, int row, int time, String arms, Color light){
        int base = design.getBase();

        manager.getAnimation(base == -1 ? SPECIAL_BASE : IPlayerDesign.BASE.get(base)).drawRow(time, row, x, y, scale, light);
        manager.getAnimation(IPlayerDesign.EYES).drawRow(time, row, x, y, scale, light.multiply(design.getEyeColor()));

        IResourceName pants = IPlayerDesign.PANTS.get(design.getPants());
        if(pants != null){
            manager.getAnimation(pants).drawRow(time, row, x, y, scale, light.multiply(design.getPantsColor()));
        }

        IResourceName shirt = IPlayerDesign.SHIRT.get(design.getShirt());
        if(shirt != null){
            manager.getAnimation(shirt).drawRow(time, row, x, y, scale, light.multiply(design.getShirtColor()));
        }

        manager.getAnimation((base == -1 ? SPECIAL_ARMS : IPlayerDesign.ARMS.get(base)).addSuffix(arms)).drawRow(time, row, x, y, scale, light);

        IResourceName sleeves = IPlayerDesign.SLEEVES.get(design.getSleeves());
        if(sleeves != null){
            manager.getAnimation(sleeves.addSuffix(arms)).drawRow(time, row, x, y, scale, light.multiply(design.getSleevesColor()));
        }

        IResourceName footwear = IPlayerDesign.FOOTWEAR.get(design.getFootwear());
        if(footwear != null){
            manager.getAnimation(footwear).drawRow(time, row, x, y, scale, light.multiply(design.getFootwearColor()));
        }

        IResourceName hair = IPlayerDesign.HAIR.get(design.getHair());
        if(hair != null){
            manager.getAnimation(hair).drawRow(time, row, x, y, scale, light.multiply(design.getHairColor()));
        }

        IResourceName accessory = IPlayerDesign.ACCESSORIES.get(design.getAccessory());
        if(accessory != null){
            manager.getAnimation(accessory).drawRow(time, row, x, y, scale, light);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, EntityPlayer entity, float x, float y, Color light){
        IPlayerDesign design = entity.getDesign();
        boolean isMoving = Math.abs(entity.motionX) >= 0.01;
        int row = entity.facing == Direction.RIGHT ? (isMoving ? 0 : 2) : (isMoving ? 1 : 3);

        renderPlayer(manager, design, x-0.5F, y-1.5F, 1F, row, entity.ticksExisted, ".hanging", light);
    }
}
