package de.ellpeck.rockbottom.apiimpl;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IApiHandler;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.part.DataPart;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.entity.MovableWorldObject;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.impl.WorldObjectCollisionEvent;
import de.ellpeck.rockbottom.api.gui.GuiContainer;
import de.ellpeck.rockbottom.api.gui.GuiContainer.ShiftClickBehavior;
import de.ellpeck.rockbottom.api.gui.component.ComponentSlot;
import de.ellpeck.rockbottom.api.gui.component.GuiComponent;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolType;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileLiquid;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.net.packet.toclient.PacketEntityUpdate;
import de.ellpeck.rockbottom.net.packet.toserver.PacketSlotModification;
import de.ellpeck.rockbottom.render.WorldRenderer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public int[] interpolateLight(IWorld world, int x, int y){
        if(!RockBottomAPI.getGame().getSettings().smoothLighting){
            int light = world.getCombinedVisualLight(x, y);
            return new int[]{light, light, light, light};
        }
        else{
            Direction[] dirs = Direction.SURROUNDING_INCLUDING_NONE;
            byte[] lightAround = new byte[dirs.length];
            for(int i = 0; i < dirs.length; i++){
                Direction dir = dirs[i];
                if(world.isPosLoaded(x+dir.x, y+dir.y)){
                    lightAround[i] = world.getCombinedVisualLight(x+dir.x, y+dir.y);
                }
            }

            int[] light = new int[4];
            light[ITexture.TOP_LEFT] = (lightAround[0]+lightAround[8]+lightAround[1]+lightAround[2])/4;
            light[ITexture.TOP_RIGHT] = (lightAround[0]+lightAround[2]+lightAround[3]+lightAround[4])/4;
            light[ITexture.BOTTOM_RIGHT] = (lightAround[0]+lightAround[4]+lightAround[5]+lightAround[6])/4;
            light[ITexture.BOTTOM_LEFT] = (lightAround[0]+lightAround[6]+lightAround[7]+lightAround[8])/4;
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
        return Colors.multiply(WorldRenderer.MAIN_COLORS[light], layer.getRenderLightModifier());
    }

    @Override
    public INoiseGen makeSimplexNoise(long seed){
        return new SimplexNoise(seed);
    }

    @Override
    public Logger createLogger(String name){
        return Logging.createLogger(name);
    }

    @Override
    public Logger logger(){
        return Logging.mainLogger;
    }
}
