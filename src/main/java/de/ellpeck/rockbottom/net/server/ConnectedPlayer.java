package de.ellpeck.rockbottom.net.server;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.effect.ActiveEffect;
import de.ellpeck.rockbottom.api.effect.IEffect;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.EventResult;
import de.ellpeck.rockbottom.api.event.impl.PlayerLeaveWorldEvent;
import de.ellpeck.rockbottom.api.event.impl.ResetMovedPlayerEvent;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.world.AbstractWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectedPlayer extends EntityPlayer {

    private final List<IChunk> chunksToSend = new ArrayList<>();
    private final Channel channel;

    private int lastHealth;
    private int lastBreath;

    private double lastCalcX;
    private double lastCalcY;
    private double distanceCounter;

    public ConnectedPlayer(IWorld world, UUID uniqueId, IPlayerDesign design, Channel channel) {
        super(world, uniqueId, design);
        this.channel = channel;
    }

    public static void disconnectPlayer(AbstractEntityPlayer player) {
        RockBottomAPI.getEventHandler().fireEvent(new PlayerLeaveWorldEvent(player, true));

        player.world.savePlayer(player);
        player.world.removeEntity(player);
        player.world.removePlayer(player);

        RockBottomAPI.logger().info("Saving and removing disconnected player " + player.getName() + " with id " + player.getUniqueId() + " from world");

        RockBottomAPI.getGame().getChatLog().broadcastMessage(new ChatComponentTranslation(ResourceName.intern("info.disconnect"), player.getName()));
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);
        INetHandler net = RockBottomAPI.getNet();

        double x = this.getX();
        double y = this.getY();

        if (this.ticksExisted % 80 == 0) {
            if (!net.getConnectedClients().contains(this.channel)) {
                game.enqueueAction((inst, object) -> disconnectPlayer(this), null);
            }

            double distanceSq = Util.distanceSq(this.lastCalcX, this.lastCalcY, x, y);
            double maxDist = this.distanceCounter + 3;

            ResetMovedPlayerEvent event = new ResetMovedPlayerEvent(this, this.lastCalcX, this.lastCalcY, distanceSq, maxDist);
            boolean cancelled = RockBottomAPI.getEventHandler().fireEvent(event) == EventResult.CANCELLED;
            distanceSq = event.distanceSqMoved;
            maxDist = event.allowedDefaultDistance;

            if (!cancelled && distanceSq > maxDist * maxDist) {
                this.motionX = 0;
                this.motionY = 0;
                this.isFalling = false;
                this.fallStartY = 0;
                this.setPos(this.lastCalcX, this.lastCalcY);

                RockBottomAPI.logger().warning("Player " + this.getName() + " with id " + this.getUniqueId() + " moved a distance of " + Math.sqrt(distanceSq) + " which is more than the max " + maxDist + ", moving them back");
            } else {
                this.lastCalcX = x;
                this.lastCalcY = y;
            }

            this.distanceCounter = 0;
        }

        if (net.isWhitelistEnabled() && !net.isWhitelisted(this.getUniqueId())) {
            this.sendPacket(new PacketReject(new ChatComponentTranslation(ResourceName.intern("info.reject.whitelist"))));

            this.channel.disconnect();
            disconnectPlayer(this);
        } else if (net.isBlacklisted(this.getUniqueId())) {
            this.sendPacket(new PacketReject(new ChatComponentTranslation(ResourceName.intern("info.reject.blacklist"), net.getBlacklistReason(this.getUniqueId()))));

            this.channel.disconnect();
            disconnectPlayer(this);
        }

        if (this.isClimbing) {
            this.distanceCounter += this.getClimbSpeed();
        } else if (this.isFalling && this.fallStartY - y >= 20) {
            this.distanceCounter += 1.225;
        } else if (this.isFalling && this.fallStartY - y >= 3) {
            this.distanceCounter += 1;
        } else {
            this.distanceCounter += this.getMoveSpeed();
        }

        if (!this.chunksToSend.isEmpty()) {
            for (int i = this.chunksToSend.size() - 1; i >= 0; i--) {
                IChunk chunk = this.chunksToSend.get(i);
                if (this.sendChunk(chunk)) {
                    this.chunksToSend.remove(i);
                }
            }
        }

        if (this.getHealth() != this.lastHealth && this.world.getTotalTime() % 10 == 0) {
            this.lastHealth = this.getHealth();
            this.sendPacket(new PacketHealth(this.getHealth(), false, false));
        }

        if (this.getBreath() != this.lastBreath && this.world.getTotalTime() % 20 == 0) {
            this.lastBreath = this.getBreath();
            this.sendPacket(new PacketHealth(this.getBreath(), false, true));
        }
    }

    @Override
    public void onPositionReset() {
        super.onPositionReset();

        this.lastCalcX = this.getX();
        this.lastCalcY = this.getY();
        this.distanceCounter = 0;

        this.sendPacket(new PacketEntityUpdate(this.getUniqueId(), this.getOriginX(), this.getOriginY(), this.motionX, this.motionY, this.facing, this.isFlying));
    }

    @Override
    public void sendPacket(IPacket packet) {
        if (this.channel != null) {
            this.channel.writeAndFlush(packet);
        }
    }

    @Override
    public void onChunkLoaded(IChunk chunk) {
        if (!this.sendChunk(chunk)) {
            this.chunksToSend.add(chunk);
        }
    }

    private boolean sendChunk(IChunk chunk) {
        if (!chunk.isGenerating()) {
            RockBottomAPI.logger().finer("Sending chunk at " + chunk.getGridX() + ", " + chunk.getGridY() + " to player " + this.getName() + " with id " + this.getUniqueId());

            this.sendPacket(new PacketChunk(chunk));

            for (Entity entity : chunk.getAllEntities()) {
                if (entity != this) {
                    this.sendPacket(new PacketEntityChange(entity, false));
                }
            }

            for (TileEntity tile : chunk.getAllTileEntities()) {
                this.sendPacket(new PacketTileEntityData(tile.x, tile.y, tile.layer, tile));
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onChunkUnloaded(IChunk chunk) {
        RockBottomAPI.logger().finer("Sending chunk unloading packet for chunk at " + chunk.getGridX() + ", " + chunk.getGridY() + " to player " + this.getName() + " with id " + this.getUniqueId());

        this.sendPacket(new PacketChunkUnload(chunk.getGridX(), chunk.getGridY()));
    }

    @Override
    public void moveToWorld(IWorld world) {
        super.moveToWorld(world);

        DataSet set = new DataSet();
        ((AbstractWorld) world).saveWorldData(set);
        this.sendPacket(new PacketChangeWorld(set, world.getSubName()));
    }

    @Override
    public int addEffect(ActiveEffect effect) {
        int remaining = super.addEffect(effect);

        if (remaining != effect.getTime()) {
            this.sendPacket(new PacketEffect(effect, false));
        }

        return remaining;
    }

    @Override
    public boolean removeEffect(IEffect effect) {
        if (super.removeEffect(effect)) {
            this.sendPacket(new PacketEffect(new ActiveEffect(effect), true));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void load(DataSet set, boolean forFullSync) {
        super.load(set, forFullSync);
        this.lastCalcX = this.getX();
        this.lastCalcY = this.getY();
    }

    @Override
    public void setMaxHealth(int maxHealth) {
        super.setMaxHealth(maxHealth);
        this.sendPacket(new PacketHealth(this.getMaxHealth(), true, false));
    }

    @Override
    public void setMaxBreath(int maxBreath) {
        super.setMaxBreath(maxBreath);
        this.sendPacket(new PacketHealth(this.getMaxBreath(), true, true));
    }

    @Override
    public void setSkill(float percentage, int points) {
        super.setSkill(percentage, points);
        this.sendPacket(new PacketSkill(this.getSkillPercentage(), this.getSkillPoints()));
    }
}
