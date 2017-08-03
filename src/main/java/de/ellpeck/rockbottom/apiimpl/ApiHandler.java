package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.Font;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.construction.resource.ResInfo;
import de.ellpeck.rockbottom.api.construction.resource.ResourceRegistry;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.TooltipEvent;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlot;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolType;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.net.packet.toclient.PacketEntityUpdate;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSlotModification;
import de.ellpeck.rockbottom.render.WorldRenderer;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.util.Log;

import java.io.*;
import java.util.*;

public class ApiHandler implements IApiHandler{

    private static final IResourceName SLOT_NAME = AbstractGame.internalRes("gui.slot");

    @Override
    public void writeDataSet(DataSet set, File file){
        try{
            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));
            this.writeSet(stream, set);
            stream.close();
        }
        catch(Exception e){
            Log.error("Exception saving a data set to disk!", e);
        }
    }

    @Override
    public void readDataSet(DataSet set, File file){
        if(!set.data.isEmpty()){
            set.data.clear();
        }

        try{
            if(file.exists()){
                DataInputStream stream = new DataInputStream(new FileInputStream(file));
                this.readSet(stream, set);
                stream.close();
            }
        }
        catch(Exception e){
            Log.error("Exception loading a data set from disk!", e);
        }
    }

    @Override
    public void writeSet(DataOutput stream, DataSet set) throws Exception{
        stream.writeInt(set.data.size());

        for(DataPart part : set.data.values()){
            this.writePart(stream, part);
        }
    }

    @Override
    public void readSet(DataInput stream, DataSet set) throws Exception{
        int amount = stream.readInt();

        for(int i = 0; i < amount; i++){
            DataPart part = this.readPart(stream);
            set.data.put(part.getName(), part);
        }
    }

    @Override
    public void writePart(DataOutput stream, DataPart part) throws Exception{
        stream.writeByte(RockBottomAPI.PART_REGISTRY.getId(part.getClass()));
        stream.writeUTF(part.getName());
        part.write(stream);
    }

    @Override
    public DataPart readPart(DataInput stream) throws Exception{
        int id = stream.readByte();
        String name = stream.readUTF();

        Class<? extends DataPart> partClass = RockBottomAPI.PART_REGISTRY.get(id);
        DataPart part = partClass.getConstructor(String.class).newInstance(name);
        part.read(stream);

        return part;
    }

    @Override
    public void doDefaultEntityUpdate(Entity entity){
        if(!entity.isDead()){
            entity.applyMotion();

            entity.move(entity.motionX, entity.motionY);

            entity.canClimb = false;
            entity.isClimbing = false;

            BoundBox area = entity.getBoundingBox().copy().add(entity.x+entity.motionX, entity.y+entity.motionY);
            for(int x = Util.floor(area.getMinX()); x < Util.ceil(area.getMaxX()); x++){
                for(int y = Util.floor(area.getMinY()); y < Util.ceil(area.getMaxY()); y++){
                    for(TileLayer layer : TileLayer.LAYERS){
                        if(entity.world.isPosLoaded(x, y)){
                            Tile tile = entity.world.getState(layer, x, y).getTile();

                            if(tile.canClimb(entity.world, x, y, layer, entity)){
                                entity.canClimb = true;

                                if(!entity.onGround){
                                    entity.isClimbing = true;
                                }
                            }

                            tile.onCollideWithEntity(entity.world, x, y, layer, entity);
                            entity.onCollideWithTile(x, y, layer, tile);
                        }
                    }
                }
            }

            if(entity.onGround || entity.isClimbing){
                if(entity.onGround){
                    entity.motionY = 0;
                }

                if(entity.fallAmount > 0){
                    entity.onGroundHit();
                    entity.fallAmount = 0;
                }
            }
            else if(entity.motionY < 0){
                entity.fallAmount++;
            }

            if(entity.collidedHor){
                entity.motionX = 0;
            }
        }
        else{
            entity.motionX = 0;
            entity.motionY = 0;
        }

        entity.ticksExisted++;

        if(RockBottomAPI.getNet().isServer()){
            if(entity.doesSync()){
                if(entity.ticksExisted%entity.getSyncFrequency() == 0){
                    if(entity.lastX != entity.x || entity.lastY != entity.y){
                        RockBottomAPI.getNet().sendToAllPlayers(entity.world, new PacketEntityUpdate(entity.getUniqueId(), entity.x, entity.y, entity.motionX, entity.motionY, entity.facing));

                        entity.lastX = entity.x;
                        entity.lastY = entity.y;
                    }
                }
            }
        }
    }

    @Override
    public boolean doDefaultSlotMovement(IGameInstance game, int button, float x, float y, ComponentSlot slot){
        if(slot.isMouseOver(game)){
            ItemInstance slotInst = slot.slot.get();
            ItemInstance slotCopy = slotInst == null ? null : slotInst.copy();

            if(Settings.KEY_GUI_ACTION_1.isKey(button)){
                if(slot.container.holdingInst == null){
                    if(slotCopy != null){
                        if(this.setToInv(null, slot)){
                            slot.container.holdingInst = slotCopy;

                            return true;
                        }
                    }
                }
                else{
                    if(slotCopy == null){
                        if(this.setToInv(slot.container.holdingInst, slot)){
                            slot.container.holdingInst = null;

                            return true;
                        }
                    }
                    else{
                        if(slotCopy.isEffectivelyEqual(slot.container.holdingInst)){
                            int possible = Math.min(slotCopy.getMaxAmount()-slotCopy.getAmount(), slot.container.holdingInst.getAmount());
                            if(possible > 0){
                                if(this.setToInv(slotCopy.addAmount(possible), slot)){
                                    slot.container.holdingInst.removeAmount(possible);
                                    if(slot.container.holdingInst.getAmount() <= 0){
                                        slot.container.holdingInst = null;
                                    }

                                    return true;
                                }
                            }
                        }
                        else{
                            ItemInstance copy = slot.container.holdingInst.copy();
                            if(this.setToInv(copy, slot)){
                                slot.container.holdingInst = slotCopy;

                                return true;
                            }
                        }
                    }
                }
            }
            else if(Settings.KEY_GUI_ACTION_2.isKey(button)){
                if(slot.container.holdingInst == null){
                    if(slotCopy != null){
                        int half = Util.ceil((double)slotCopy.getAmount()/2);
                        slotCopy.removeAmount(half);

                        if(this.setToInv(slotCopy.getAmount() <= 0 ? null : slotCopy, slot)){
                            slot.container.holdingInst = slotCopy.copy().setAmount(half);

                            return true;
                        }
                    }
                }
                else{
                    if(slotCopy == null){
                        if(this.setToInv(slot.container.holdingInst.copy().setAmount(1), slot)){
                            slot.container.holdingInst.removeAmount(1);
                            if(slot.container.holdingInst.getAmount() <= 0){
                                slot.container.holdingInst = null;
                            }

                            return true;
                        }
                    }
                    else if(slotCopy.isEffectivelyEqual(slot.container.holdingInst)){
                        if(slotCopy.getAmount() < slotCopy.getMaxAmount()){
                            if(this.setToInv(slotCopy.addAmount(1), slot)){
                                slot.container.holdingInst.removeAmount(1);
                                if(slot.container.holdingInst.getAmount() <= 0){
                                    slot.container.holdingInst = null;
                                }

                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void renderSlotInGui(IGameInstance game, IAssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale, boolean hovered){
        Image texture = manager.getTexture(SLOT_NAME);

        Color color = game.getSettings().guiColor;
        if(hovered){
            color = color.brighter(0.4F);
        }

        texture.draw(x, y, texture.getWidth()*scale, texture.getHeight()*scale, color);

        if(slot != null){
            this.renderItemInGui(game, manager, g, slot, x+3F*scale, y+3F*scale, scale, Color.white);
        }
    }

    @Override
    public void renderItemInGui(IGameInstance game, IAssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale, Color color){
        Item item = slot.getItem();
        IItemRenderer renderer = item.getRenderer();
        if(renderer != null){
            renderer.render(game, manager, g, item, slot, x, y, 12F*scale, color);
        }

        manager.getFont().drawStringFromRight(x+15F*scale, y+9F*scale, String.valueOf(slot.getAmount()), 0.25F*scale);
    }

    @Override
    public void describeItem(IGameInstance game, IAssetManager manager, Graphics g, ItemInstance instance){
        boolean advanced = Settings.KEY_ADVANCED_INFO.isDown();

        List<String> desc = new ArrayList<>();
        instance.getItem().describeItem(manager, instance, desc, advanced);

        if(game.isItemInfoDebug()){
            desc.add("");
            desc.add(FormattingCode.GRAY+"Name: "+instance.getItem().getName().toString());
            desc.add(FormattingCode.GRAY+"Meta: "+instance.getMeta());
            desc.add(FormattingCode.GRAY+"Data: "+instance.getAdditionalData());
            desc.add(FormattingCode.GRAY+"Max Amount: "+instance.getMaxAmount());
            desc.add(FormattingCode.GRAY+"Resources: "+ResourceRegistry.getNames(new ResInfo(instance)));
        }

        if(RockBottomAPI.getEventHandler().fireEvent(new TooltipEvent(instance, game, manager, g, desc)) != EventResult.CANCELLED){
            this.drawHoverInfoAtMouse(game, manager, g, true, 500, desc);
        }
    }

    @Override
    public void drawHoverInfoAtMouse(IGameInstance game, IAssetManager manager, Graphics g, boolean firstLineOffset, int maxLength, String... text){
        this.drawHoverInfoAtMouse(game, manager, g, firstLineOffset, maxLength, Arrays.asList(text));
    }

    @Override
    public void drawHoverInfoAtMouse(IGameInstance game, IAssetManager manager, Graphics g, boolean firstLineOffset, int maxLength, List<String> text){
        float mouseX = game.getMouseInGuiX();
        float mouseY = game.getMouseInGuiY();

        this.drawHoverInfo(game, manager, g, mouseX+18F/game.getGuiScale(), mouseY+18F/game.getGuiScale(), 0.25F, firstLineOffset, false, maxLength, text);
    }

    @Override
    public void drawHoverInfo(IGameInstance game, IAssetManager manager, Graphics g, float x, float y, float scale, boolean firstLineOffset, boolean canLeaveScreen, int maxLength, List<String> text){
        Font font = manager.getFont();

        float boxWidth = 0F;
        float boxHeight = 0F;

        if(maxLength > 0){
            text = font.splitTextToLength(maxLength, scale, true, text);
        }

        for(String s : text){
            float length = font.getWidth(s, scale);
            if(length > boxWidth){
                boxWidth = length;
            }

            if(firstLineOffset && boxHeight == 0F && text.size() > 1){
                boxHeight += 3F;
            }
            boxHeight += font.getHeight(scale);
        }

        if(boxWidth > 0F && boxHeight > 0F){
            boxWidth += 4F;
            boxHeight += 4F;

            if(!canLeaveScreen){
                x = Math.max(0, Math.min(x, (float)game.getWidthInGui()-boxWidth));
                y = Math.max(0, Math.min(y, (float)game.getHeightInGui()-boxHeight));
            }

            g.setColor(Gui.HOVER_INFO_BACKGROUND);
            g.fillRect(x, y, boxWidth, boxHeight);

            g.setColor(Color.black);
            g.drawRect(x, y, boxWidth, boxHeight);

            float yOffset = 0F;
            for(String s : text){
                font.drawString(x+2F, y+2F+yOffset, s, scale);

                if(firstLineOffset && yOffset == 0F){
                    yOffset += 3F;
                }
                yOffset += font.getHeight(scale);
            }
        }
    }

    @Override
    public int[] interpolateLight(IWorld world, int x, int y){
        if(!RockBottomAPI.getGame().getSettings().smoothLighting){
            int light = world.getCombinedLight(x, y);
            return new int[]{light, light, light, light};
        }
        else{
            Direction[] dirs = Direction.SURROUNDING_INCLUDING_NONE;
            byte[] lightAround = new byte[dirs.length];
            for(int i = 0; i < dirs.length; i++){
                Direction dir = dirs[i];
                if(world.isPosLoaded(x+dir.x, y+dir.y)){
                    lightAround[i] = world.getCombinedLight(x+dir.x, y+dir.y);
                }
            }

            int[] light = new int[4];
            light[Image.TOP_LEFT] = (lightAround[0]+lightAround[8]+lightAround[1]+lightAround[2])/4;
            light[Image.TOP_RIGHT] = (lightAround[0]+lightAround[2]+lightAround[3]+lightAround[4])/4;
            light[Image.BOTTOM_RIGHT] = (lightAround[0]+lightAround[4]+lightAround[5]+lightAround[6])/4;
            light[Image.BOTTOM_LEFT] = (lightAround[0]+lightAround[6]+lightAround[7]+lightAround[8])/4;
            return light;
        }
    }

    @Override
    public Color[] interpolateWorldColor(int[] interpolatedLight, TileLayer layer){
        Color[] colors = new Color[interpolatedLight.length];
        for(int i = 0; i < colors.length; i++){
            colors[i] = this.getColorByLight(interpolatedLight[i], layer);
        }
        return colors;
    }

    @Override
    public Color getColorByLight(int light, TileLayer layer){
        return (layer == TileLayer.BACKGROUND ? WorldRenderer.BACKGROUND_COLORS : WorldRenderer.MAIN_COLORS)[RockBottomAPI.getGame().isLightDebug() ? Constants.MAX_LIGHT : light];
    }

    @Override
    public INoiseGen makeSimplexNoise(Random random){
        return new SimplexNoise(random);
    }

    @Override
    public boolean isToolEffective(AbstractEntityPlayer player, ItemInstance instance, Tile tile, TileLayer layer, int x, int y){
        if(instance != null){
            Map<ToolType, Integer> tools = instance.getItem().getToolTypes(instance);
            if(!tools.isEmpty()){
                for(Map.Entry<ToolType, Integer> entry : tools.entrySet()){
                    if(tile.isToolEffective(player.world, x, y, layer, entry.getKey(), entry.getValue())){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean placeTile(int x, int y, TileLayer layer, AbstractEntityPlayer player, ItemInstance selected, Tile tile){
        if(layer != TileLayer.MAIN || player.world.getEntities(new BoundBox(x, y, x+1, y+1), entity -> !(entity instanceof EntityItem)).isEmpty()){
            if(player.world.getState(layer, x, y).getTile().canReplace(player.world, x, y, layer, tile)){
                if(tile.canPlace(player.world, x, y, layer)){

                    if(!RockBottomAPI.getNet().isClient()){
                        tile.doPlace(player.world, x, y, layer, selected, player);
                        player.getInv().remove(player.getSelectedSlot(), 1);
                    }

                    return true;
                }
            }
        }
        return false;
    }

    private boolean setToInv(ItemInstance inst, ComponentSlot slot){
        if(inst == null ? slot.slot.canRemove() : slot.slot.canPlace(inst)){
            slot.slot.set(inst);

            if(RockBottomAPI.getNet().isClient()){
                RockBottomAPI.getNet().sendToServer(new PacketSlotModification(AbstractGame.get().getPlayer().getUniqueId(), slot.componentId, inst));
            }
            return true;
        }
        else{
            return false;
        }
    }
}
