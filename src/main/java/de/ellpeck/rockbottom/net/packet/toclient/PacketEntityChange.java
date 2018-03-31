package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.design.PlayerDesign;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class PacketEntityChange implements IPacket{

    private static final String PLAYER_NAME = RockBottomAPI.createInternalRes("player").toString();

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

                EntityPlayer player = (EntityPlayer)entity;
                this.entitySet.addString("design", Util.GSON.toJson(player.getDesign()));
            }
            else{
                this.name = RockBottomAPI.ENTITY_REGISTRY.getId(entity.getClass()).toString();
            }

            entity.save(this.entitySet);
        }
    }

    public PacketEntityChange(){
    }

    @Override
    public void toBuffer(ByteBuf buf){
        buf.writeLong(this.uniqueId.getMostSignificantBits());
        buf.writeLong(this.uniqueId.getLeastSignificantBits());
        buf.writeBoolean(this.remove);

        if(!this.remove){
            NetUtil.writeStringToBuffer(this.name, buf);
            NetUtil.writeSetToBuffer(this.entitySet, buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf){
        this.uniqueId = new UUID(buf.readLong(), buf.readLong());
        this.remove = buf.readBoolean();

        if(!this.remove){
            this.name = NetUtil.readStringFromBuffer(buf);
            NetUtil.readSetFromBuffer(this.entitySet, buf);
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context){
        IWorld world = game.getWorld();

        if(world != null){
            Entity entity = world.getEntity(this.uniqueId);

            if(this.remove){
                if(entity != null){
                    world.removeEntity(entity);
                }
            }
            else{
                if(entity == null){
                    if(PLAYER_NAME.equals(this.name)){
                        PlayerDesign design = Util.GSON.fromJson(this.entitySet.getString("design"), PlayerDesign.class);
                        entity = new EntityPlayer(world, this.uniqueId, design);
                    }
                    else{
                        entity = Util.createEntity(RockBottomAPI.createRes(this.name), world);
                    }

                    if(entity != null){
                        entity.load(this.entitySet);
                        world.addEntity(entity);
                    }
                }
                else{
                    entity.load(this.entitySet);
                }
            }
        }
    }

    @Override
    public void enqueueAsAction(IGameInstance game, ChannelHandlerContext context){
        game.enqueueAction(this :: handle, context, inst -> inst.getWorld() != null);
    }
}
