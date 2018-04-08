package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketManualConstruction implements IPacket{

    private UUID playerId;
    private ResourceName recipeName;
    private int amount;

    public PacketManualConstruction(UUID playerId, ResourceName recipeName, int amount){
        this.playerId = playerId;
        this.recipeName = recipeName;
        this.amount = amount;
    }

    public PacketManualConstruction(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(this.recipeName.toString(), buf);
        buf.writeInt(this.amount);
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.recipeName = new ResourceName(NetUtil.readStringFromBuffer(buf));
        this.amount = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        if(game.getWorld() != null){
            AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
            if(player != null){
                IRecipe recipe = IRecipe.forName(this.recipeName);
                if(recipe != null && recipe.isKnown(player)){
                    recipe.construct(player.world, player.x, player.y, player.getInv(), this.amount);
                }
            }
        }
    }
}
