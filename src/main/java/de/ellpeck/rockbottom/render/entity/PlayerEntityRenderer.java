package de.ellpeck.rockbottom.render.entity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAnimation;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.event.impl.PlayerRenderEvent;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

public class PlayerEntityRenderer implements IEntityRenderer<EntityPlayer>{

    private static final IResourceName SPECIAL_BASE = RockBottomAPI.createInternalRes("player.base.s");
    private static final IResourceName SPECIAL_ARMS = RockBottomAPI.createInternalRes("player.arm.skin_s");

    public static void renderPlayer(EntityPlayer player, IGameInstance game, IAssetManager manager, IGraphics g, IPlayerDesign design, float x, float y, float scale, int row, int light){
        ItemInstance holding = player != null ? player.getInv().get(player.getSelectedSlot()) : null;
        String arms = holding == null ? "hanging" : "holding";
        int base = design.getBase();

        manager.getAnimation((base == -1 ? SPECIAL_BASE : IPlayerDesign.BASE.get(base)).addSuffix("."+(design.isFemale() ? "female" : "male"))).drawRow(row, x, y, scale, 2F*scale, light);
        manager.getAnimation(IPlayerDesign.EYES).drawRow(row, x, y, scale, 2F*scale, Colors.multiply(light, design.getEyeColor()));

        IResourceName eyebrows = IPlayerDesign.EYEBROWS.get(design.getEyebrows());
        if(eyebrows != null){
            manager.getAnimation(eyebrows).drawRow(row, x, y, scale, 2F*scale, Colors.multiply(light, design.getEyebrowsColor()));
        }

        IResourceName mouth = IPlayerDesign.MOUTH.get(design.getMouth());
        if(mouth != null){
            manager.getAnimation(mouth).drawRow(row, x, y, scale, 2F*scale, light);
        }

        IResourceName beard = IPlayerDesign.BEARD.get(design.getBeard());
        if(beard != null){
            manager.getAnimation(beard).drawRow(row, x, y, scale, 2F*scale, Colors.multiply(light, design.getBeardColor()));
        }

        IResourceName pants = IPlayerDesign.PANTS.get(design.getPants());
        if(pants != null){
            manager.getAnimation(pants).drawRow(row, x, y, scale, 2F*scale, Colors.multiply(light, design.getPantsColor()));
        }

        IResourceName shirt = IPlayerDesign.SHIRT.get(design.getShirt());
        if(shirt != null){
            manager.getAnimation(shirt).drawRow(row, x, y, scale, 2F*scale, Colors.multiply(light, design.getShirtColor()));
        }

        IAnimation armAnimation = manager.getAnimation((base == -1 ? SPECIAL_ARMS : IPlayerDesign.ARMS.get(base)).addSuffix("."+arms));
        armAnimation.drawRow(row, x, y, scale, 2F*scale, light);

        IResourceName sleeves = IPlayerDesign.SLEEVES.get(design.getSleeves());
        if(sleeves != null){
            manager.getAnimation(sleeves.addSuffix("."+arms)).drawRow(row, x, y, scale, 2F*scale, Colors.multiply(light, design.getSleevesColor()));
        }

        IResourceName footwear = IPlayerDesign.FOOTWEAR.get(design.getFootwear());
        if(footwear != null){
            manager.getAnimation(footwear).drawRow(row, x, y, scale, 2F*scale, Colors.multiply(light, design.getFootwearColor()));
        }

        IResourceName hair = IPlayerDesign.HAIR.get(design.getHair());
        if(hair != null){
            manager.getAnimation(hair).drawRow(row, x, y, scale, 2F*scale, Colors.multiply(light, design.getHairColor()));
        }

        IResourceName accessory = IPlayerDesign.ACCESSORIES.get(design.getAccessory());
        if(accessory != null){
            manager.getAnimation(accessory).drawRow(row, x, y, scale, 2F*scale, light);
        }

        if(holding != null){
            Item item = holding.getItem();
            IItemRenderer renderer = item.getRenderer();
            if(renderer != null){
                JsonElement[] holdingOffsets = armAnimation.getAdditionalFrameData("holding_offset", row);
                JsonElement[] holdingAngles = armAnimation.getAdditionalFrameData("holding_angle", row);
                JsonElement[] holdingMirroreds = armAnimation.getAdditionalFrameData("holding_mirrored", row);

                if(holdingOffsets != null && holdingAngles != null && holdingMirroreds != null){
                    float itemX;
                    float itemY;
                    float holdingAngle;
                    boolean holdingMirrored;
                    float holdingScale = 1F;

                    try{
                        int frame = armAnimation.getFrameByTime(row, Util.getTimeMillis());
                        float animScale = Math.min(armAnimation.getFrameWidth(), armAnimation.getFrameHeight());

                        holdingAngle = holdingAngles[frame].getAsFloat();
                        holdingMirrored = holdingMirroreds[frame].getAsBoolean();

                        JsonArray holdingOffset = holdingOffsets[frame].getAsJsonArray();
                        itemX = x+((holdingOffset.get(0).getAsFloat()/animScale)*scale);
                        itemY = y+((holdingOffset.get(1).getAsFloat()/animScale)*scale);

                        JsonElement itemOff = renderer.getAdditionalTextureData(game, manager, g, item, holding, player, "holding_offset");
                        JsonElement itemAngle = renderer.getAdditionalTextureData(game, manager, g, item, holding, player, "holding_angle");
                        JsonElement itemMirrored = renderer.getAdditionalTextureData(game, manager, g, item, holding, player, "holding_mirrored");
                        JsonElement itemScale = renderer.getAdditionalTextureData(game, manager, g, item, holding, player, "holding_scale");

                        if(itemOff != null){
                            JsonArray itemOffFrame = itemOff.getAsJsonArray().get(row).getAsJsonArray().get(frame).getAsJsonArray();
                            itemX += (itemOffFrame.get(0).getAsFloat()/animScale)*scale;
                            itemY += (itemOffFrame.get(1).getAsFloat()/animScale)*scale;
                        }

                        if(itemAngle != null){
                            holdingAngle += itemAngle.getAsJsonArray().get(row).getAsJsonArray().get(frame).getAsFloat();
                        }

                        if(itemMirrored != null){
                            holdingMirrored = itemMirrored.getAsJsonArray().get(row).getAsJsonArray().get(frame).getAsBoolean();
                        }

                        if(itemScale != null){
                            holdingScale = itemScale.getAsJsonArray().get(row).getAsJsonArray().get(frame).getAsFloat();
                        }
                    }
                    catch(Exception e){
                        itemX = x;
                        itemY = y;
                        holdingAngle = 0F;
                        holdingMirrored = false;
                    }

                    renderer.renderHolding(game, manager, g, item, holding, player, itemX, itemY, holdingAngle, scale*holdingScale, light, holdingMirrored);
                }
            }
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, IWorld world, EntityPlayer entity, float x, float y, int light){
        IPlayerDesign design = entity.getDesign();
        boolean isRight = entity.facing == Direction.RIGHT;
        boolean isHorMovement = Math.abs(entity.motionX) >= 0.01;

        int row;
        if(entity.isClimbing){
            row = isHorMovement || Math.abs(entity.motionY) >= 0.01 ? 6 : 7;
        }
        else if(!entity.onGround){
            row = isRight ? 4 : 5;
        }
        else if(isHorMovement){
            row = isRight ? 0 : 1;
        }
        else{
            row = isRight ? 2 : 3;
        }
        renderPlayer(entity, game, manager, g, design, x-0.5F, y-1.5F, 1F, row, light);

        RockBottomAPI.getEventHandler().fireEvent(new PlayerRenderEvent(game, manager, g, entity, x, y));
    }
}
