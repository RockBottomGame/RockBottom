package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.net.packet.toclient.PacketInitialServerData;
import de.ellpeck.rockbottom.render.PlayerDesign;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.newdawn.slick.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PacketJoin implements IPacket{

    private final List<ModInfo> modInfos = new ArrayList<>();
    private String version;
    private UUID id;
    private IPlayerDesign design;

    public PacketJoin(UUID id, String version, IPlayerDesign design, List<IMod> mods){
        this.id = id;
        this.version = version;
        this.design = design;

        for(IMod mod : mods){
            this.modInfos.add(new ModInfo(mod.getId(), mod.getVersion()));
        }
    }

    public PacketJoin(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.id.getMostSignificantBits());
        buf.writeLong(this.id.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(this.version, buf);

        DataSet set = new DataSet();
        this.design.save(set);
        NetUtil.writeSetToBuffer(set, buf);

        buf.writeInt(this.modInfos.size());
        for(ModInfo info : this.modInfos){
            info.toBuffer(buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.id = new UUID(buf.readLong(), buf.readLong());
        this.version = NetUtil.readStringFromBuffer(buf);

        DataSet set = new DataSet();
        NetUtil.readSetFromBuffer(set, buf);
        this.design = new PlayerDesign();
        this.design.load(set);

        int amount = buf.readInt();
        for(int i = 0; i < amount; i++){
            this.modInfos.add(ModInfo.fromBuffer(buf));
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            IWorld world = game.getWorld();

            boolean shouldKick = true;
            if(RockBottom.VERSION.equals(this.version)){
                if(this.hasAllMods(new ArrayList<>(RockBottomAPI.getModLoader().getActiveMods()))){
                    if(world != null){
                        if(world.getPlayer(this.id) == null){
                            AbstractEntityPlayer player = world.createPlayer(this.id, this.design, context.channel());
                            player.sendPacket(new PacketInitialServerData(player, world.getWorldInfo(), world.getTileRegInfo(), world.getBiomeRegInfo()));
                            world.addEntity(player);

                            shouldKick = false;
                            Log.info("Player "+this.design.getName()+" with id "+this.id+" joined, sending initial server data");
                        }
                        else{
                            Log.error("Player "+this.design.getName()+" with id "+this.id+" tried joining while already connected!");
                        }
                    }
                }
            }
            else{
                Log.error("Player "+this.design.getName()+" with id "+this.id+" tried joining with game version "+this.version+", server version is "+RockBottom.VERSION+"!");
            }

            if(shouldKick){
                context.channel().disconnect();
                Log.info("Disconnecting player "+this.design.getName()+" with id "+this.id);
            }

            return true;
        });
    }

    private boolean hasAllMods(List<IMod> mods){
        for(int i = 0; i < mods.size(); i++){
            IMod mod = mods.get(i);
            for(ModInfo info : this.modInfos){
                if(mod.getId().equals(info.id)){
                    if(!mod.getVersion().equals(info.version)){
                        Log.error("Player "+this.design.getName()+" with id "+this.id+" tried joining with invalid version "+info.version+" of mod "+mod.getDisplayName()+", expected was "+mod.getVersion());
                        return false;
                    }
                    else{
                        mods.remove(i);
                        i--;

                        this.modInfos.remove(info);
                        break;
                    }
                }
            }
        }

        if(!mods.isEmpty()){
            Log.error("Player "+this.design.getName()+" with id "+this.id+" tried joining with missing mods "+this.listMods(mods));
            return false;
        }

        if(!this.modInfos.isEmpty()){
            Log.warn("Player "+this.design.getName()+" with id "+this.id+" has mods that aren't on the server: "+this.modInfos);
        }

        return true;
    }

    private String listMods(List<IMod> mods){
        Iterator<IMod> it = mods.iterator();

        String s = "[";
        while(it.hasNext()){
            IMod mod = it.next();
            s += mod.getDisplayName()+" @ "+mod.getVersion();

            if(!it.hasNext()){
                return s+"]";
            }
            else{
                s += ", ";
            }
        }
        return s;
    }

    private static class ModInfo{

        public final String id;
        public final String version;

        public ModInfo(String id, String version){
            this.id = id;
            this.version = version;
        }

        public static ModInfo fromBuffer(ByteBuf buf){
            String id = NetUtil.readStringFromBuffer(buf);
            String version = NetUtil.readStringFromBuffer(buf);

            return new ModInfo(id, version);
        }

        public void toBuffer(ByteBuf buf){
            NetUtil.writeStringToBuffer(this.id, buf);
            NetUtil.writeStringToBuffer(this.version, buf);
        }

        @Override
        public String toString(){
            return this.id+" @ "+this.version;
        }
    }
}
