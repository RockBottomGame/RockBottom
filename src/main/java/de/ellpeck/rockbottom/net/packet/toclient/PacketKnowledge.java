package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.knowledge.KnowledgeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketKnowledge implements IPacket{

    private UUID playerId;
    private final DataSet infoSet = new DataSet();
    private boolean announce;
    private boolean forget;

    public PacketKnowledge(EntityPlayer player, Information information, boolean announce, boolean forget){
        this.playerId = player.getUniqueId();
        this.announce = announce;
        this.forget = forget;

        if(forget){
            this.infoSet.addString("name", information.getName().toString());
        }
        else{
            KnowledgeManager.saveInformation(this.infoSet, player.getKnowledge(), information);
        }
    }

    public PacketKnowledge(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeBoolean(this.announce);
        buf.writeBoolean(this.forget);
        NetUtil.writeSetToBuffer(this.infoSet, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.announce = buf.readBoolean();
        this.forget = buf.readBoolean();
        NetUtil.readSetFromBuffer(this.infoSet, buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            IWorld world = game.getWorld();
            if(world != null){
                AbstractEntityPlayer player = world.getPlayer(this.playerId);
                if(player != null){
                    if(this.forget){
                        player.getKnowledge().forgetInformation(RockBottomAPI.createRes(this.infoSet.getString("name")), this.announce);
                    }
                    else{
                        KnowledgeManager.loadInformation(this.infoSet, player.getKnowledge(), this.announce);
                    }
                }
            }
            return true;
        });
    }
}
