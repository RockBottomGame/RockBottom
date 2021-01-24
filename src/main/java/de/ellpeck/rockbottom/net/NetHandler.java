package de.ellpeck.rockbottom.net;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.net.client.Client;
import de.ellpeck.rockbottom.net.server.Server;
import io.netty.channel.group.ChannelGroup;

import java.util.UUID;

public class NetHandler implements INetHandler {

    private Client client;
    private Server server;

    @Override
    public void init(String ip, int port, boolean isServer) throws Exception {
        if (this.isActive()) {
            RockBottomAPI.logger().severe("Cannot initialize " + (isServer ? "server" : "client") + " because one is already running: Client: " + this.client + ", Server: " + this.server);
        } else {
            if (isServer) {
                this.server = new Server(ip, port);
                RockBottomAPI.logger().info("Started server with ip " + ip + " on port " + port);
            } else {
                this.client = new Client(ip, port);
                RockBottomAPI.logger().info("Started client with ip " + ip + " on port " + port);
            }
        }
    }

    @Override
    public void shutdown() {
        if (this.isClient()) {
            this.client.shutdown();
            this.client = null;

            RockBottomAPI.logger().info("Shut down client!");
        }

        if (this.isServer()) {
            this.server.shutdown();
            this.server = null;

            RockBottomAPI.logger().info("Shut down server!");
        }
    }

    @Override
    public boolean isThePlayer(Entity entity) {
        IGameInstance game = RockBottomAPI.getGame();
        return !game.isDedicatedServer() && game.getPlayer() == entity;
    }

    @Override
    public boolean isClient() {
        return this.client != null;
    }

    @Override
    public boolean isServer() {
        return this.server != null;
    }

    @Override
    public boolean isActive() {
        return this.isClient() || this.isServer();
    }

    @Override
    public boolean isConnectedToServer() {
        return this.isClient() && this.client.channel.isOpen();
    }

    @Override
    public ChannelGroup getConnectedClients() {
        if (this.isServer()) {
            return this.server.connectedChannels;
        } else {
            return null;
        }
    }

    @Override
    public void sendToServer(IPacket packet) {
        if (this.isClient()) {
            this.client.channel.writeAndFlush(packet);
        }
    }

    @Override
    public void sendToAllPlayers(IWorld world, IPacket packet) {
        this.sendToAllPlayersExcept(world, packet, null);
    }

    @Override
    public void sendToAllPlayersExcept(IWorld world, IPacket packet, Entity except) {
        if (this.isServer()) {
            for (AbstractPlayerEntity player : world.getAllPlayers()) {
                if (player != except) {
                    player.sendPacket(packet);
                }
            }
        }
    }

    @Override
    public void sendToAllPlayersInWorld(IWorld world, IPacket packet) {
        this.sendToAllPlayersInWorldExcept(world, packet, null);
    }

    @Override
    public void sendToAllPlayersInWorldExcept(IWorld world, IPacket packet, Entity except) {
        if (this.isServer()) {
            for (AbstractPlayerEntity player : world.getAllPlayers()) {
                if (player.world == world && player != except) {
                    player.sendPacket(packet);
                }
            }
        }
    }

    @Override
    public void sendToAllPlayersAround(IWorld world, IPacket packet, double x, double y, double radius) {
        this.sendToAllPlayersAroundExcept(world, packet, x, y, radius, null);
    }

    @Override
    public void sendToAllPlayersAroundExcept(IWorld world, IPacket packet, double x, double y, double radius, Entity except) {
        if (this.isServer()) {
            for (AbstractPlayerEntity player : world.getAllPlayers()) {
                if (player.world == world && player != except && Util.distanceSq(x, y, player.getX(), player.getY()) <= radius * radius) {
                    player.sendPacket(packet);
                }
            }
        }
    }

    @Override
    public void sendToAllPlayersWithLoadedPos(IWorld world, IPacket packet, double x, double y) {
        this.sendToAllPlayersWithLoadedPosExcept(world, packet, x, y, null);
    }

    @Override
    public void sendToAllPlayersWithLoadedPosExcept(IWorld world, IPacket packet, double x, double y, Entity except) {
        if (this.isServer()) {
            IChunk chunk = world.getChunk(x, y);
            for (AbstractPlayerEntity player : world.getAllPlayers()) {
                if (player.world == world && player != except && (chunk.getPlayersInRange().contains(player) || chunk.getPlayersLeftRange().contains(player))) {
                    player.sendPacket(packet);
                }
            }
        }
    }

    @Override
    public int getCommandLevel(AbstractPlayerEntity player) {
        if (this.isServer()) {
            return this.server.commandPermissions.getCommandLevel(player);
        } else {
            return 0;
        }
    }

    @Override
    public void setCommandLevel(AbstractPlayerEntity player, int level) {
        this.setCommandLevel(player.getUniqueId(), level);
    }

    @Override
    public void setCommandLevel(UUID id, int level) {
        if (this.isServer()) {
            this.server.commandPermissions.setCommandLevel(id, level);
        }
    }

    @Override
    public void whitelist(UUID id) {
        if (this.isServer()) {
            this.server.whitelist.add(id);
        }
    }

    @Override
    public void removeWhitelist(UUID id) {
        if (this.isServer()) {
            this.server.whitelist.remove(id);
        }
    }

    @Override
    public boolean isWhitelisted(UUID id) {
        return !this.isServer() || this.server.whitelist.isWhitelisted(id);
    }

    @Override
    public boolean isWhitelistEnabled() {
        return this.isServer() && this.server.whitelist.isEnabled();
    }

    @Override
    public void enableWhitelist(boolean enabled) {
        if (this.isServer()) {
            this.server.whitelist.setEnabled(enabled);
        }
    }

    @Override
    public void blacklist(UUID id, String reason) {
        if (this.isServer()) {
            this.server.blacklist.add(id, reason);
        }
    }

    @Override
    public String getBlacklistReason(UUID id) {
        if (this.isServer()) {
            return this.server.blacklist.getBlacklistReason(id);
        } else {
            return null;
        }
    }

    @Override
    public void removeBlacklist(UUID id) {
        if (this.isServer()) {
            this.server.blacklist.remove(id);
        }
    }

    @Override
    public boolean isBlacklisted(UUID id) {
        return !this.isServer() || this.server.blacklist.isBlacklisted(id);
    }

    @Override
    public void saveServerSettings() {
        if (this.isServer()) {
            this.server.commandPermissions.save();
            this.server.whitelist.save();
            this.server.blacklist.save();
        }
    }
}
