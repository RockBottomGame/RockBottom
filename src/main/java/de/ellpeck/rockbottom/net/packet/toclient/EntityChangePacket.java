package de.ellpeck.rockbottom.net.packet.toclient;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.player.PlayerEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.UUID;

public class EntityChangePacket implements IPacket {

    public static final ResourceName NAME = ResourceName.intern("entity_change");

    private static final String PLAYER_NAME = ResourceName.intern("player").toString();

    private final DataSet entitySet = new DataSet();
    private String name;
    private UUID uniqueId;

    private boolean remove;

    public EntityChangePacket(Entity entity, boolean remove) {
        this.remove = remove;
        this.uniqueId = entity.getUniqueId();

        if (!this.remove) {
            if (entity instanceof PlayerEntity) {
                this.name = PLAYER_NAME;
            } else {
                this.name = entity.getRegistryName().toString();
            }
            entity.save(this.entitySet, true);
        }
    }

    public EntityChangePacket() {
    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.uniqueId.getMostSignificantBits());
        buf.writeLong(this.uniqueId.getLeastSignificantBits());
        buf.writeBoolean(this.remove);

        if (!this.remove) {
            NetUtil.writeStringToBuffer(buf, this.name);
            NetUtil.writeSetToBuffer(this.entitySet, buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.uniqueId = new UUID(buf.readLong(), buf.readLong());
        this.remove = buf.readBoolean();

        if (!this.remove) {
            this.name = NetUtil.readStringFromBuffer(buf);
            NetUtil.readSetFromBuffer(this.entitySet, buf);
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();

        if (world != null) {
            Entity entity = world.getEntity(this.uniqueId);

            if (this.remove) {
                if (entity != null) {
                    world.removeEntity(entity);
                }
            } else {
                if (entity == null) {
                    if (PLAYER_NAME.equals(this.name)) {
                        entity = world.getPlayer(this.uniqueId);
                    } else {
                        entity = Util.createEntity(new ResourceName(this.name), world);
                        if (entity != null) {
                            entity.setUniqueId(this.uniqueId);
                        }
                    }

                    if (entity != null) {
                        entity.load(this.entitySet, true);
                        world.addEntity(entity);
                    }
                } else {
                    entity.load(this.entitySet, true);
                }
            }
        }
    }

    @Override
    public ResourceName getName() {
        return NAME;
    }
}
