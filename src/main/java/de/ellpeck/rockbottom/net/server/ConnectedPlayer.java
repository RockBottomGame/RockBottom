package de.ellpeck.rockbottom.net.server;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
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
import de.ellpeck.rockbottom.api.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectedPlayer extends EntityPlayer{

    private final List<IChunk> chunksToSend = new ArrayList<>();
    private final Channel channel;

    private int lastHealth;

    private double lastCalcX;
    private double lastCalcY;
    private int fallCalcTicks;
    private int climbingCalcTicks;

    public ConnectedPlayer(World world, UUID uniqueId, IPlayerDesign design, Channel channel){
        super(world, uniqueId, design);
        this.channel = channel;
    }

    public static void disconnectPlayer(IGameInstance game, AbstractEntityPlayer player){
        RockBottomAPI.getEventHandler().fireEvent(new PlayerLeaveWorldEvent(player, true));

        game.getWorld().savePlayer(player);
        game.getWorld().removeEntity(player);

        RockBottomAPI.logger().info("Saving and removing disconnected player "+player.getName()+" with id "+player.getUniqueId()+" from world");

        RockBottomAPI.getGame().getChatLog().broadcastMessage(new ChatComponentTranslation(ResourceName.intern("info.disconnect"), player.getName()));
    }

    @Override
    public void update(IGameInstance game){
        super.update(game);
        INetHandler net = RockBottomAPI.getNet();

        if(this.ticksExisted%80 == 0){
            if(!net.getConnectedClients().contains(this.channel)){
                game.enqueueAction((inst, object) -> disconnectPlayer(inst, this), null);
            }

            double distanceSq = Util.distanceSq(this.lastCalcX, this.lastCalcY, this.x, this.y);
            double maxDist = 1.25*this.fallCalcTicks
                    +this.getClimbSpeed()*this.climbingCalcTicks
                    +this.getMoveSpeed()*(80-this.fallCalcTicks-this.climbingCalcTicks)
                    +3;

            ResetMovedPlayerEvent event = new ResetMovedPlayerEvent(this, this.lastCalcX, this.lastCalcY, this.fallCalcTicks, this.climbingCalcTicks, distanceSq, maxDist);
            boolean cancelled = RockBottomAPI.getEventHandler().fireEvent(event) == EventResult.CANCELLED;
            distanceSq = event.distanceSqMoved;
            maxDist = event.allowedDefaultDistance;

            if(!cancelled && distanceSq > maxDist*maxDist){
                this.motionX = 0;
                this.motionY = 0;
                this.isFalling = false;
                this.fallStartY = 0;
                this.setPos(this.lastCalcX, this.lastCalcY);

                RockBottomAPI.logger().warning("Player "+this.getName()+" with id "+this.getUniqueId()+" moved a distance of "+Math.sqrt(distanceSq)+" which is more than the max "+maxDist+", moving them back");
            }
            else{
                this.lastCalcX = this.x;
                this.lastCalcY = this.y;
            }

            this.fallCalcTicks = 0;
            this.climbingCalcTicks = 0;
        }

        if(net.isWhitelistEnabled() && !net.isWhitelisted(this.getUniqueId())){
            this.sendPacket(new PacketReject(new ChatComponentTranslation(ResourceName.intern("info.reject.whitelist"))));

            this.channel.disconnect();
            disconnectPlayer(game, this);
        }
        else if(net.isBlacklisted(this.getUniqueId())){
            this.sendPacket(new PacketReject(new ChatComponentTranslation(ResourceName.intern("info.reject.blacklist"), net.getBlacklistReason(this.getUniqueId()))));

            this.channel.disconnect();
            disconnectPlayer(game, this);
        }

        if(this.isClimbing){
            this.climbingCalcTicks++;
        }
        else if(this.isFalling && this.fallStartY-this.y >= 5){
            this.fallCalcTicks++;
        }

        if(!this.chunksToSend.isEmpty()){
            for(int i = this.chunksToSend.size()-1; i >= 0; i--){
                IChunk chunk = this.chunksToSend.get(i);
                if(this.sendChunk(chunk)){
                    this.chunksToSend.remove(i);
                }
            }
        }

        if(this.getHealth() != this.lastHealth && this.world.getTotalTime()%10 == 0){
            this.lastHealth = this.getHealth();
            this.sendPacket(new PacketHealth(this.getHealth()));
        }
    }

    @Override
    public void setPos(double x, double y){
        super.setPos(x, y);

        this.lastCalcX = x;
        this.lastCalcY = y;
        this.fallCalcTicks = 0;

        this.sendPacket(new PacketEntityUpdate(this.getUniqueId(), this.x, this.y, this.motionX, this.motionY, this.facing, this.collidedHor, this.collidedVert, this.onGround));
    }

    @Override
    public void sendPacket(IPacket packet){
        if(this.channel != null){
            this.channel.writeAndFlush(packet);
        }
    }

    @Override
    public void onChunkLoaded(IChunk chunk){
        if(!this.sendChunk(chunk)){
            this.chunksToSend.add(chunk);
        }
    }

    private boolean sendChunk(IChunk chunk){
        if(!chunk.isGenerating()){
            RockBottomAPI.logger().finer("Sending chunk at "+chunk.getGridX()+", "+chunk.getGridY()+" to player "+this.getName()+" with id "+this.getUniqueId());

            this.sendPacket(new PacketChunk(chunk));

            for(Entity entity : chunk.getAllEntities()){
                if(entity != this){
                    this.sendPacket(new PacketEntityChange(entity, false));
                }
            }

            for(TileEntity tile : chunk.getAllTileEntities()){
                this.sendPacket(new PacketTileEntityData(tile.x, tile.y, tile.layer, tile));
            }

            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onChunkUnloaded(IChunk chunk){
        RockBottomAPI.logger().finer("Sending chunk unloading packet for chunk at "+chunk.getGridX()+", "+chunk.getGridY()+" to player "+this.getName()+" with id "+this.getUniqueId());

        this.sendPacket(new PacketChunkUnload(chunk.getGridX(), chunk.getGridY()));
    }

    @Override
    public int addEffect(ActiveEffect effect){
        int remaining = super.addEffect(effect);

        if(remaining != effect.getTime()){
            this.sendPacket(new PacketEffect(effect, false));
        }

        return remaining;
    }

    @Override
    public boolean removeEffect(IEffect effect){
        if(super.removeEffect(effect)){
            this.sendPacket(new PacketEffect(new ActiveEffect(effect), true));
            return true;
        }
        else{
            return false;
        }
    }
}
