package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.init.AbstractGame;
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

    private static final IResourceName SPECIAL_BASE = AbstractGame.internalRes("player.base.male_skin_s");
    private static final IResourceName SPECIAL_ARMS = AbstractGame.internalRes("player.arm.skin_s");

    public static void renderPlayer(IAssetManager manager, IPlayerDesign design, float x, float y, float scale, int row, String arms, Color light){
        int base = design.getBase();

        manager.getAnimation(base == -1 ? SPECIAL_BASE : IPlayerDesign.BASE.get(base)).drawRow(row, x, y, scale, light);
        manager.getAnimation(IPlayerDesign.EYES).drawRow(row, x, y, scale, light.multiply(design.getEyeColor()));

        IResourceName eyebrows = IPlayerDesign.EYEBROWS.get(design.getEyebrows());
        if(eyebrows != null){
            manager.getAnimation(eyebrows).drawRow(row, x, y, scale, light.multiply(design.getEyebrowsColor()));
        }

        IResourceName mouth = IPlayerDesign.MOUTH.get(design.getMouth());
        if(mouth != null){
            manager.getAnimation(mouth).drawRow(row, x, y, scale, light);
        }

        IResourceName beard = IPlayerDesign.BEARD.get(design.getBeard());
        if(beard != null){
            manager.getAnimation(beard).drawRow(row, x, y, scale, light.multiply(design.getBeardColor()));
        }

        IResourceName pants = IPlayerDesign.PANTS.get(design.getPants());
        if(pants != null){
            manager.getAnimation(pants).drawRow(row, x, y, scale, light.multiply(design.getPantsColor()));
        }

        IResourceName shirt = IPlayerDesign.SHIRT.get(design.getShirt());
        if(shirt != null){
            manager.getAnimation(shirt).drawRow(row, x, y, scale, light.multiply(design.getShirtColor()));
        }

        manager.getAnimation((base == -1 ? SPECIAL_ARMS : IPlayerDesign.ARMS.get(base)).addSuffix(arms)).drawRow(row, x, y, scale, light);

        IResourceName sleeves = IPlayerDesign.SLEEVES.get(design.getSleeves());
        if(sleeves != null){
            manager.getAnimation(sleeves.addSuffix(arms)).drawRow(row, x, y, scale, light.multiply(design.getSleevesColor()));
        }

        IResourceName footwear = IPlayerDesign.FOOTWEAR.get(design.getFootwear());
        if(footwear != null){
            manager.getAnimation(footwear).drawRow(row, x, y, scale, light.multiply(design.getFootwearColor()));
        }

        IResourceName hair = IPlayerDesign.HAIR.get(design.getHair());
        if(hair != null){
            manager.getAnimation(hair).drawRow(row, x, y, scale, light.multiply(design.getHairColor()));
        }

        IResourceName accessory = IPlayerDesign.ACCESSORIES.get(design.getAccessory());
        if(accessory != null){
            manager.getAnimation(accessory).drawRow(row, x, y, scale, light);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, EntityPlayer entity, float x, float y, Color light){
        IPlayerDesign design = entity.getDesign();
        boolean isMoving = Math.abs(entity.motionX) >= 0.01;
        boolean isJumping = !entity.onGround && !entity.isClimbing;
        int row = entity.facing == Direction.RIGHT ? (isJumping ? 4 : (isMoving ? 0 : 2)) : (isJumping ? 5 : (isMoving ? 1 : 3));

        renderPlayer(manager, design, x-0.5F, y-1.5F, 1F, row, ".hanging", light);
    }
}
