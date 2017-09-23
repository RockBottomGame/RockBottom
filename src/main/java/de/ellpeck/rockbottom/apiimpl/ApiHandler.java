package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.impl.WorldObjectCollisionEvent;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlot;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolType;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.net.packet.toclient.PacketEntityUpdate;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSlotModification;
import de.ellpeck.rockbottom.render.WorldRenderer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApiHandler implements IApiHandler{

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
            RockBottomAPI.logger().log(Level.SEVERE, "Exception saving a data set to disk!", e);
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
            RockBottomAPI.logger().log(Level.SEVERE, "Exception loading a data set from disk!", e);
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

            entity.canClimb = false;
            entity.isClimbing = false;

            entity.move(entity.motionX, entity.motionY);

            if(entity.onGround || entity.isClimbing){
                if(entity.onGround){
                    entity.motionY = 0;
                }

                if(entity.isFalling){
                    double dist = entity.fallStartY-entity.y;
                    if(dist > 0){
                        entity.onGroundHit(dist);
                    }

                    entity.isFalling = false;
                    entity.fallStartY = 0;
                }
            }
            else if(entity.motionY < 0){
                if(!entity.isFalling){
                    entity.isFalling = true;
                    entity.fallStartY = entity.y;
                }
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

        if(entity.world.isServer()){
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
    public void doWorldObjectMovement(MovableWorldObject object, double motionX, double motionY){
        if(motionX != 0 || motionY != 0){
            double motionXBefore = motionX;
            double motionYBefore = motionY;

            BoundBox ownBox = object.getBoundingBox();
            BoundBox tempBox = ownBox.copy().add(object.x+motionX, object.y+motionY);

            List<BoundBox> boxes = new ArrayList<>();

            for(int x = Util.floor(tempBox.getMinX()); x < Util.ceil(tempBox.getMaxX()); x++){
                for(int y = Util.floor(tempBox.getMinY()); y < Util.ceil(tempBox.getMaxY()); y++){
                    if(object.world.isPosLoaded(x, y)){
                        for(TileLayer layer : TileLayer.getAllLayers()){
                            TileState state = object.world.getState(x, y);

                            if(layer == TileLayer.MAIN){
                                List<BoundBox> tileBoxes = state.getTile().getBoundBoxes(object.world, x, y);
                                object.onTileCollision(object.world, x, y, layer, state, tempBox, tileBoxes);
                                boxes.addAll(tileBoxes);
                            }
                            else{
                                object.onTileCollision(object.world, x, y, layer, state, tempBox, Collections.emptyList());
                            }
                        }
                    }
                }
            }

            RockBottomAPI.getEventHandler().fireEvent(new WorldObjectCollisionEvent(object, tempBox, boxes));

            if(motionY != 0){
                if(!boxes.isEmpty()){
                    tempBox.set(ownBox).add(object.x, object.y);

                    for(BoundBox box : boxes){
                        if(motionY != 0){
                            if(!box.isEmpty()){
                                motionY = box.getYDistanceWithMax(tempBox, motionY);
                            }
                        }
                        else{
                            break;
                        }
                    }
                }

                object.y += motionY;
            }

            if(motionX != 0){
                if(!boxes.isEmpty()){
                    tempBox.set(ownBox).add(object.x, object.y);

                    for(BoundBox box : boxes){
                        if(motionX != 0){
                            if(!box.isEmpty()){
                                motionX = box.getXDistanceWithMax(tempBox, motionX);
                            }
                        }
                        else{
                            break;
                        }
                    }
                }

                object.x += motionX;
            }

            object.collidedHor = motionX != motionXBefore;
            object.collidedVert = motionY != motionYBefore;
            object.onGround = object.collidedVert && motionYBefore < 0;
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
            light[Texture.TOP_LEFT] = (lightAround[0]+lightAround[8]+lightAround[1]+lightAround[2])/4;
            light[Texture.TOP_RIGHT] = (lightAround[0]+lightAround[2]+lightAround[3]+lightAround[4])/4;
            light[Texture.BOTTOM_RIGHT] = (lightAround[0]+lightAround[4]+lightAround[5]+lightAround[6])/4;
            light[Texture.BOTTOM_LEFT] = (lightAround[0]+lightAround[6]+lightAround[7]+lightAround[8])/4;
            return light;
        }
    }

    @Override
    public int[] interpolateWorldColor(int[] interpolatedLight, TileLayer layer){
        int[] colors = new int[interpolatedLight.length];
        for(int i = 0; i < colors.length; i++){
            colors[i] = this.getColorByLight(interpolatedLight[i], layer);
        }
        return colors;
    }

    @Override
    public int getColorByLight(int light, TileLayer layer){
        return Colors.multiply(WorldRenderer.MAIN_COLORS[RockBottomAPI.getGame().isLightDebug() ? Constants.MAX_LIGHT : light], layer.getRenderLightModifier());
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
            if(player.world.getState(layer, x, y).getTile().canReplace(player.world, x, y, layer)){
                if(InteractionManager.defaultTilePlacementCheck(player.world, x, y, layer, tile) && tile.canPlace(player.world, x, y, layer)){
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

    @Override
    public Logger createLogger(String name){
        return Logging.createLogger(name);
    }

    @Override
    public Logger logger(){
        return Logging.mainLogger;
    }

    private boolean setToInv(ItemInstance inst, ComponentSlot slot){
        if(inst == null ? slot.slot.canRemove() : slot.slot.canPlace(inst)){
            slot.slot.set(inst);

            if(RockBottomAPI.getNet().isClient()){
                RockBottomAPI.getNet().sendToServer(new PacketSlotModification(RockBottomAPI.getGame().getPlayer().getUniqueId(), slot.componentId, inst));
            }
            return true;
        }
        else{
            return false;
        }
    }
}
