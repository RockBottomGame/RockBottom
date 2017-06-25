package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class PlayerEntityRenderer implements IEntityRenderer<EntityPlayer>{

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, EntityPlayer entity, float x, float y, Color light){
        IPlayerDesign design = entity.getDesign();
        boolean isMoving = Math.abs(entity.motionX) >= 0.01;
        String armMod = ".hanging";
        int row = entity.facing == Direction.RIGHT ? (isMoving ? 0 : 2) : (isMoving ? 1 : 3);
        int time = entity.ticksExisted;

        x -= 0.5F;
        y -= 1.5F;

        manager.getAnimation(IPlayerDesign.BASE.get(design.getBase())).drawRow(time, row, x, y, 1F, light);
        manager.getAnimation(IPlayerDesign.EYES).drawRow(time, row, x, y, 1F, light.multiply(design.getEyeColor()));
        manager.getAnimation(IPlayerDesign.SHIRT.get(design.getShirt())).drawRow(time, row, x, y, 1F, light.multiply(design.getShirtColor()));
        manager.getAnimation(IPlayerDesign.PANTS.get(design.getPants())).drawRow(time, row, x, y, 1F, light.multiply(design.getPantsColor()));
        manager.getAnimation(IPlayerDesign.ARMS.get(design.getBase()).addSuffix(armMod)).drawRow(time, row, x, y, 1F, light);
        manager.getAnimation(IPlayerDesign.SLEEVES.get(design.getSleeves()).addSuffix(armMod)).drawRow(time, row, x, y, 1F, light.multiply(design.getSleevesColor()));
        manager.getAnimation(IPlayerDesign.FOOTWEAR.get(design.getFootwear())).drawRow(time, row, x, y, 1F, light.multiply(design.getFootwearColor()));
        manager.getAnimation(IPlayerDesign.HAIR.get(design.getHair())).drawRow(time, row, x, y, 1F, light.multiply(design.getHairColor()));
        manager.getAnimation(IPlayerDesign.ACCESSORIES.get(design.getAccessory())).drawRow(time, row, x, y, 1F, light);
    }
}
