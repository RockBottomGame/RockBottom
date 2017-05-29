package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;
import de.ellpeck.rockbottom.construction.IRecipe;
import de.ellpeck.rockbottom.gui.container.ContainerInventory;
import de.ellpeck.rockbottom.net.packet.IPacket;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketManualConstruction implements IPacket{

    private UUID playerId;
    private int recipeIndex;
    private int amount;

    public PacketManualConstruction(UUID playerId, int recipeIndex, int amount){
        this.playerId = playerId;
        this.recipeIndex = recipeIndex;
        this.amount = amount;
    }

    public PacketManualConstruction(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.playerId.getMostSignificantBits());
        buf.writeLong(this.playerId.getLeastSignificantBits());
        buf.writeInt(this.recipeIndex);
        buf.writeInt(this.amount);
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.playerId = new UUID(buf.readLong(), buf.readLong());
        this.recipeIndex = buf.readInt();
        this.amount = buf.readInt();
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                EntityPlayer player = game.world.getPlayer(this.playerId);
                if(player != null){
                    IRecipe recipe = ConstructionRegistry.MANUAL_RECIPES.get(this.recipeIndex);
                    if(recipe != null){
                        ContainerInventory.doManualCraft(player, recipe, this.amount);
                    }
                }
            }
            return true;
        });
    }
}
