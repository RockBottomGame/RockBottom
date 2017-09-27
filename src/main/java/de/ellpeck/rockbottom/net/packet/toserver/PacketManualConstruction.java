package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.construction.IRecipe;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketManualConstruction implements IPacket{

    private UUID playerId;
    private IResourceName recipeName;
    private int amount;

    public PacketManualConstruction(UUID playerId, IResourceName recipeName, int amount){
        this.playerId = playerId;
        this.recipeName = recipeName;
        this.amount = amount;
    }

    public PacketManualConstruction(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(this.recipeName.toString(), buf);
        buf.writeInt(this.amount);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.recipeName = RockBottomAPI.createRes(NetUtil.readStringFromBuffer(buf));
        this.amount = buf.readInt();
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.getWorld() != null){
                AbstractEntityPlayer player = game.getWorld().getPlayer(this.playerId);
                if(player != null){
                    IRecipe recipe = RockBottomAPI.ALL_CONSTRUCTION_RECIPES.get(this.recipeName);
                    if(recipe != null){
                        ContainerInventory.doInvBasedConstruction(player, recipe, this.amount);
                    }
                }
            }
            return true;
        });
    }
}
