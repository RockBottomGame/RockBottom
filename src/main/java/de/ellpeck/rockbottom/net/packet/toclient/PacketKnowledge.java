package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.knowledge.IKnowledgeManager;
import de.ellpeck.rockbottom.api.entity.player.knowledge.Information;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.knowledge.KnowledgeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class PacketKnowledge implements IPacket{

    private final DataSet infoSet = new DataSet();
    private boolean announce;
    private boolean forget;

    public PacketKnowledge(EntityPlayer player, Information information, boolean announce, boolean forget){
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
    public void toBuffer(ByteBuf buf){
        buf.writeBoolean(this.announce);
        buf.writeBoolean(this.forget);
        NetUtil.writeSetToBuffer(this.infoSet, buf);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.announce = buf.readBoolean();
        this.forget = buf.readBoolean();
        NetUtil.readSetFromBuffer(this.infoSet, buf);
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        AbstractEntityPlayer player = game.getPlayer();
        if(player != null){
            IKnowledgeManager manager = player.getKnowledge();
            if(this.forget){
                manager.forgetInformation(RockBottomAPI.createRes(this.infoSet.getString("name")), this.announce);
            }
            else{
                Information information = KnowledgeManager.loadInformation(this.infoSet, manager);
                if(information != null){
                    manager.teachInformation(information);
                }
            }
        }
    }
}
