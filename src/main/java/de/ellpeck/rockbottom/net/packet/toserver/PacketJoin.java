package de.ellpeck.rockbottom.net.packet.toserver;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.net.INetHandler;
import de.ellpeck.rockbottom.api.net.NetUtil;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.IPacket;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.net.packet.toclient.PacketInitialServerData;
import de.ellpeck.rockbottom.net.packet.toclient.PacketReject;
import de.ellpeck.rockbottom.render.design.PlayerDesign;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PacketJoin implements IPacket {

    private final List<ModInfo> modInfos = new ArrayList<>();
    private UUID id;
    private IPlayerDesign design;

    public PacketJoin(UUID id, IPlayerDesign design, List<IMod> mods) {
        this.id = id;
        this.design = design;

        for (IMod mod : mods) {
            this.modInfos.add(new ModInfo(mod.getId(), mod.getVersion(), mod.isRequiredOnServer()));
        }
    }

    public PacketJoin() {

    }

    @Override
    public void toBuffer(ByteBuf buf) {
        buf.writeLong(this.id.getMostSignificantBits());
        buf.writeLong(this.id.getLeastSignificantBits());
        NetUtil.writeStringToBuffer(Util.GSON.toJson(this.design), buf);

        buf.writeInt(this.modInfos.size());
        for (ModInfo info : this.modInfos) {
            info.toBuffer(buf);
        }
    }

    @Override
    public void fromBuffer(ByteBuf buf) {
        this.id = new UUID(buf.readLong(), buf.readLong());
        this.design = Util.GSON.fromJson(NetUtil.readStringFromBuffer(buf), PlayerDesign.class);

        int amount = buf.readInt();
        for (int i = 0; i < amount; i++) {
            this.modInfos.add(ModInfo.fromBuffer(buf));
        }
    }

    @Override
    public void handle(IGameInstance game, ChannelHandlerContext context) {
        IWorld world = game.getWorld();
        ChatComponentTranslation reject = null;

        if (world != null && world.getAllPlayers().size() >= game.getPlayerCap()) {
            reject = new ChatComponentTranslation(ResourceName.intern("info.reject.server_full"));
        }

        if (reject == null) {
            INetHandler net = RockBottomAPI.getNet();
            if (net.isWhitelistEnabled() && !net.isWhitelisted(this.id)) {
                reject = new ChatComponentTranslation(ResourceName.intern("info.reject.whitelist"));
            } else if (net.isBlacklisted(this.id)) {
                reject = new ChatComponentTranslation(ResourceName.intern("info.reject.blacklist"), net.getBlacklistReason(this.id));
            }
        }

        if (reject == null) {
            ChatComponentTranslation mods = this.checkMods(new ArrayList<>(RockBottomAPI.getModLoader().getActiveMods()));
            if (mods == null) {
                if (world != null) {
                    if (world.getPlayer(this.id) == null) {
                        AbstractEntityPlayer player = world.createPlayer(this.id, this.design, context.channel(), false);
                        player.sendPacket(new PacketInitialServerData(player, world.getWorldInfo(), world.getRegInfo()));
                        world.addEntity(player);

                        RockBottomAPI.logger().info("Player " + this.design.getName() + " with id " + this.id + " joined, sending initial server data");

                        RockBottomAPI.getGame().getChatLog().broadcastMessage(new ChatComponentTranslation(ResourceName.intern("info.connect"), player.getName()));
                    } else {
                        RockBottomAPI.logger().warning("Player " + this.design.getName() + " with id " + this.id + " tried joining while already connected!");
                        reject = new ChatComponentTranslation(ResourceName.intern("info.reject.connected_already"));
                    }
                } else {
                    reject = new ChatComponentTranslation(ResourceName.intern("info.reject.starting_up"));
                }
            } else {
                reject = mods;
            }
        }

        if (reject != null) {
            context.writeAndFlush(new PacketReject(reject));
            context.disconnect();
            RockBottomAPI.logger().info("Disconnecting player " + this.design.getName() + " with id " + this.id);
        }
    }

    private ChatComponentTranslation checkMods(List<IMod> mods) {
        RockBottomAPI.logger().info("Player " + this.design.getName() + " with id " + this.id + " is connecting with mods " + this.modInfos);

        for (int i = mods.size() - 1; i >= 0; i--) {
            IMod mod = mods.get(i);
            if (mod.isRequiredOnClient()) {
                for (ModInfo info : this.modInfos) {
                    if (mod.getId().equals(info.id)) {
                        if (!mod.isCompatibleWithModVersion(info.version)) {
                            RockBottomAPI.logger().warning("Player " + this.design.getName() + " with id " + this.id + " tried joining with incompatible version " + info.version + " of mod " + mod.getId() + ", expected was " + mod.getVersion());
                            return new ChatComponentTranslation(ResourceName.intern("info.reject.incompatible_version"), mod.getId(), info.version, mod.getVersion());
                        } else {
                            mods.remove(i);

                            this.modInfos.remove(info);
                            break;
                        }
                    }
                }
            }
        }

        if (!mods.isEmpty()) {
            String modList = this.listMods(mods);
            RockBottomAPI.logger().warning("Player " + this.design.getName() + " with id " + this.id + " tried joining with missing required mods " + modList);
            return new ChatComponentTranslation(ResourceName.intern("info.reject.missing_mods"), modList);
        }

        if (!this.modInfos.isEmpty()) {
            RockBottomAPI.logger().info("Player " + this.design.getName() + " with id " + this.id + " is attempting to join with mods that aren't on the server: " + this.modInfos);

            for (int i = this.modInfos.size() - 1; i >= 0; i--) {
                if (!this.modInfos.get(i).requiredOnServer) {
                    this.modInfos.remove(i);
                }
            }

            if (!this.modInfos.isEmpty()) {
                RockBottomAPI.logger().warning("Player " + this.design.getName() + " with id " + this.id + " tried joining with serverside required mods that aren't on the server: " + this.modInfos);
                return new ChatComponentTranslation(ResourceName.intern("info.reject.too_many_mods"), this.modInfos.toString());
            }
        }

        return null;
    }

    private String listMods(List<IMod> mods) {
        Iterator<IMod> it = mods.iterator();

        StringBuilder s = new StringBuilder("[");
        while (it.hasNext()) {
            IMod mod = it.next();
            s.append(mod.getId()).append(" @ ").append(mod.getVersion());

            if (!it.hasNext()) {
                return s.toString() + ']';
            } else {
                s.append(", ");
            }
        }
        return s.toString();
    }

    private static class ModInfo {

        public final String id;
        public final String version;
        public final boolean requiredOnServer;

        public ModInfo(String id, String version, boolean requiredOnServer) {
            this.id = id;
            this.version = version;
            this.requiredOnServer = requiredOnServer;
        }

        public static ModInfo fromBuffer(ByteBuf buf) {
            String id = NetUtil.readStringFromBuffer(buf);
            String version = NetUtil.readStringFromBuffer(buf);
            boolean required = buf.readBoolean();

            return new ModInfo(id, version, required);
        }

        public void toBuffer(ByteBuf buf) {
            NetUtil.writeStringToBuffer(this.id, buf);
            NetUtil.writeStringToBuffer(this.version, buf);
            buf.writeBoolean(this.requiredOnServer);
        }

        @Override
        public String toString() {
            return this.id + " @ " + this.version;
        }
    }
}
