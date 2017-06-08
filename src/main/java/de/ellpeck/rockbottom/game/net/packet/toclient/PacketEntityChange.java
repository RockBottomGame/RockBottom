package de.ellpeck.rockbottom.game.net.packet.toclient;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.data.set.DataSet;
import de.ellpeck.rockbottom.game.net.NetUtil;
import de.ellpeck.rockbottom.game.net.packet.IPacket;
import de.ellpeck.rockbottom.game.world.entity.Entity;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;
import java.util.UUID;

public class PacketEntityChange implements IPacket{

    private static final String PLAYER_NAME = "player";

    private final DataSet entitySet = new DataSet();
    private String name;
    private UUID uniqueId;

    private boolean remove;

    public PacketEntityChange(Entity entity, boolean remove){
        this.remove = remove;
        this.uniqueId = entity.getUniqueId();

        if(!this.remove){
            if(entity instanceof EntityPlayer){
                this.name = PLAYER_NAME;
            }
            else{
                this.name = RockBottomAPI.ENTITY_REGISTRY.getId(entity.getClass());
            }

            entity.save(this.entitySet);
        }
    }

    public PacketEntityChange(){
    }

    @Override
    public void toBuffer(ByteBuf buf) throws IOException{
        buf.writeLong(this.uniqueId.getMostSignificantBits());
        buf.writeLong(this.uniqueId.getLeastSignificantBits());
        buf.writeBoolean(this.remove);

        if(!this.remove){
            NetUtil.writeStringToBuffer(this.name, buf);
            NetUtil.writeSetToBuffer(this.entitySet, buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) throws IOException{
        this.uniqueId = new UUID(buf.readLong(), buf.readLong());
        this.remove = buf.readBoolean();

        if(!this.remove){
            this.name = NetUtil.readStringFromBuffer(buf);
            NetUtil.readSetFromBuffer(this.entitySet, buf);
        }
    }

    @Override
    public void handle(RockBottom game, ChannelHandlerContext context){
        game.scheduleAction(() -> {
            if(game.world != null){
                Entity entity = game.world.getEntity(this.uniqueId);

                if(this.remove){
                    if(entity != null){
                        game.world.removeEntity(entity);
                    }
                }
                else{
                    if(entity == null){
                        if(PLAYER_NAME.equals(this.name)){
                            entity = new EntityPlayer(game.world);
                        }
                        else{
                            entity = Entity.create(this.name, game.world);
                        }

                        if(entity != null){
                            entity.load(this.entitySet);
                            game.world.addEntity(entity);
                        }
                    }
                    else{
                        entity.load(this.entitySet);
                    }
                }
                return true;
            }
            else{
                return false;
            }
        });
    }
}
