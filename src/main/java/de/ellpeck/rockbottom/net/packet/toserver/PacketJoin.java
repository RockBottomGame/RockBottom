package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.net.packet.toclient.PacketInitialServerData;
import de.ellpeck.rockbottom.render.PlayerDesign;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PacketJoin implements IPacket{

    private final List<ModInfo> modInfos = new ArrayList<>();
    private UUID id;
    private IPlayerDesign design;

    public PacketJoin(UUID id, IPlayerDesign design, List<IMod> mods){
        this.id = id;
        this.design = design;

        for(IMod mod : mods){
            this.modInfos.add(new ModInfo(mod.getId(), mod.getVersion(), mod.isRequiredOnServer()));
        }
    }

    public PacketJoin(){

    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.id.getMostSignificantBits());
        buf.writeLong(this.id.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(Util.GSON.toJson(this.design), buf);

        buf.writeInt(this.modInfos.size());
        for(ModInfo info : this.modInfos){
            info.toBuffer(buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.id = new UUID(buf.readLong(), buf.readLong());
        this.design = Util.GSON.fromJson(NetUtil.readStringFromBuffer(buf), PlayerDesign.class);

        int amount = buf.readInt();
        for(int i = 0; i < amount; i++){
            this.modInfos.add(ModInfo.fromBuffer(buf));
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();

        boolean shouldKick = true;
        if(this.checkMods(new ArrayList<>(RockBottomAPI.getModLoader().getActiveMods()))){
            if(world != null){
                if(world.getPlayer(this.id) == null){
                    AbstractEntityPlayer player = world.createPlayer(this.id, this.design, context.channel());
                    player.sendPacket(new PacketInitialServerData(player, world.getWorldInfo(), world.getRegInfo()));
                    world.addEntity(player);

                    shouldKick = false;
                    RockBottomAPI.logger().info("Player "+this.design.getName()+" with id "+this.id+" joined, sending initial server data");

                    RockBottomAPI.getGame().getChatLog().broadcastMessage(new ChatComponentTranslation(RockBottomAPI.createInternalRes("info.connect"), player.getName()));
                }
                else{
                    RockBottomAPI.logger().warning("Player "+this.design.getName()+" with id "+this.id+" tried joining while already connected!");
                }
            }
        }

        if(shouldKick){
            context.channel().disconnect();
            RockBottomAPI.logger().info("Disconnecting player "+this.design.getName()+" with id "+this.id);
        }
    }

    private boolean checkMods(List<IMod> mods){
        RockBottomAPI.logger().info("Player "+this.design.getName()+" with id "+this.id+" is connecting with mods "+this.modInfos);

        for(int i = 0; i < mods.size(); i++){
            IMod mod = mods.get(i);
            if(mod.isRequiredOnClient()){
                for(ModInfo info : this.modInfos){
                    if(mod.getId().equals(info.id)){
                        if(!mod.isCompatibleWithModVersion(info.version)){
                            RockBottomAPI.logger().warning("Player "+this.design.getName()+" with id "+this.id+" tried joining with incompatible version "+info.version+" of mod "+mod.getDisplayName()+", expected was "+mod.getVersion());
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
        }

        if(!mods.isEmpty()){
            RockBottomAPI.logger().warning("Player "+this.design.getName()+" with id "+this.id+" tried joining with missing required mods "+this.listMods(mods));
            return false;
        }

        if(!this.modInfos.isEmpty()){
            RockBottomAPI.logger().info("Player "+this.design.getName()+" with id "+this.id+" is attempting to join with mods that aren't on the server: "+this.modInfos);

            for(int i = 0; i < this.modInfos.size(); i++){
                if(!this.modInfos.get(i).requiredOnServer){
                    this.modInfos.remove(i);
                    i--;
                }
            }

            if(!this.modInfos.isEmpty()){
                RockBottomAPI.logger().warning("Player "+this.design.getName()+" with id "+this.id+" tried joining with required mods that aren't on the server: "+this.modInfos);
                return false;
            }
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
        public final boolean requiredOnServer;

        public ModInfo(String id, String version, boolean requiredOnServer){
            this.id = id;
            this.version = version;
            this.requiredOnServer = requiredOnServer;
        }

        public static ModInfo fromBuffer(ByteBuf buf){
            String id = NetUtil.readStringFromBuffer(buf);
            String version = NetUtil.readStringFromBuffer(buf);
            boolean required = buf.readBoolean();

            return new ModInfo(id, version, required);
        }

        public void toBuffer(ByteBuf buf){
            NetUtil.writeStringToBuffer(this.id, buf);
            NetUtil.writeStringToBuffer(this.version, buf);
            buf.writeBoolean(this.requiredOnServer);
        }

        @Override
        public String toString(){
            return this.id+" @ "+this.version;
        }
    }
}
