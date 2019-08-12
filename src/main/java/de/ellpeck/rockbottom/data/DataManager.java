package de.ellpeck.rockbottom.data;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.part.*;
import de.ellpeck.rockbottom.api.data.set.part.num.*;
import de.ellpeck.rockbottom.api.data.settings.IJsonSettings;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentEmpty;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.net.packet.backandforth.PacketOpenUnboundContainer;
import de.ellpeck.rockbottom.net.packet.backandforth.PacketStats;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.net.packet.toserver.*;

import java.io.*;
import java.util.logging.Level;

public class DataManager implements IDataManager {

    static {
        Registries.PART_REGISTRY.register(0, PartInt.FACTORY);
        Registries.PART_REGISTRY.register(1, PartFloat.FACTORY);
        Registries.PART_REGISTRY.register(2, PartDouble.FACTORY);
        Registries.PART_REGISTRY.register(6, PartDataSet.FACTORY);
        Registries.PART_REGISTRY.register(7, PartLong.FACTORY);
        Registries.PART_REGISTRY.register(8, PartUniqueId.FACTORY);
        Registries.PART_REGISTRY.register(9, PartByte.FACTORY);
        Registries.PART_REGISTRY.register(10, PartShort.FACTORY);
        Registries.PART_REGISTRY.register(11, PartBoolean.FACTORY);
        Registries.PART_REGISTRY.register(12, PartString.FACTORY);
        Registries.PART_REGISTRY.register(13, PartModBasedDataSet.FACTORY);
        Registries.PART_REGISTRY.register(14, PartList.FACTORY);

        Registries.PACKET_REGISTRY.register(0, PacketJoin.class);
        Registries.PACKET_REGISTRY.register(1, PacketChunk.class);
        Registries.PACKET_REGISTRY.register(2, PacketInitialServerData.class);
        Registries.PACKET_REGISTRY.register(3, PacketDisconnect.class);
        Registries.PACKET_REGISTRY.register(4, PacketTileChange.class);
        Registries.PACKET_REGISTRY.register(5, PacketScheduledUpdate.class);
        Registries.PACKET_REGISTRY.register(6, PacketEntityChange.class);
        Registries.PACKET_REGISTRY.register(7, PacketBreakTile.class);
        Registries.PACKET_REGISTRY.register(8, PacketParticles.class);
        Registries.PACKET_REGISTRY.register(9, PacketEntityUpdate.class);
        Registries.PACKET_REGISTRY.register(10, PacketPlayerMovement.class);
        Registries.PACKET_REGISTRY.register(11, PacketInteract.class);
        Registries.PACKET_REGISTRY.register(12, PacketHotbar.class);
        Registries.PACKET_REGISTRY.register(13, PacketTileEntityData.class);
        Registries.PACKET_REGISTRY.register(14, PacketShiftClick.class);
        Registries.PACKET_REGISTRY.register(15, PacketOpenUnboundContainer.class);
        Registries.PACKET_REGISTRY.register(16, PacketContainerData.class);
        Registries.PACKET_REGISTRY.register(17, PacketContainerChange.class);
        Registries.PACKET_REGISTRY.register(18, PacketChatMessage.class);
        Registries.PACKET_REGISTRY.register(19, PacketSendChat.class);
        Registries.PACKET_REGISTRY.register(20, PacketHealth.class);
        Registries.PACKET_REGISTRY.register(21, PacketRespawn.class);
        Registries.PACKET_REGISTRY.register(22, PacketDrop.class);
        Registries.PACKET_REGISTRY.register(23, PacketChunkUnload.class);
        Registries.PACKET_REGISTRY.register(24, PacketManualConstruction.class);
        Registries.PACKET_REGISTRY.register(25, PacketDeath.class);
        Registries.PACKET_REGISTRY.register(26, PacketKnowledge.class);
        Registries.PACKET_REGISTRY.register(27, PacketRecipesToast.class);
        Registries.PACKET_REGISTRY.register(28, PacketSound.class);
        Registries.PACKET_REGISTRY.register(29, PacketActiveItem.class);
        Registries.PACKET_REGISTRY.register(30, PacketChestOpen.class);
        Registries.PACKET_REGISTRY.register(31, PacketSignText.class);
        Registries.PACKET_REGISTRY.register(32, PacketReject.class);
        Registries.PACKET_REGISTRY.register(33, PacketTime.class);
        Registries.PACKET_REGISTRY.register(34, PacketAttack.class);
        Registries.PACKET_REGISTRY.register(35, PacketEffect.class);
        Registries.PACKET_REGISTRY.register(36, PacketSetOrPickHolding.class);
        Registries.PACKET_REGISTRY.register(37, PacketStats.class);
        Registries.PACKET_REGISTRY.register(38, PacketAITask.class);
        Registries.PACKET_REGISTRY.register(39, PacketToolBreak.class);
        Registries.PACKET_REGISTRY.register(40, PacketDamage.class);
        Registries.PACKET_REGISTRY.register(41, PacketSkill.class);
        Registries.PACKET_REGISTRY.register(42, PacketPlayer.class);
        Registries.PACKET_REGISTRY.register(43, PacketChangeWorld.class);
        Registries.PACKET_REGISTRY.register(44, PacketPickup.class);

        Registries.CHAT_COMPONENT_REGISTRY.register(0, ChatComponentText.class);
        Registries.CHAT_COMPONENT_REGISTRY.register(1, ChatComponentTranslation.class);
        Registries.CHAT_COMPONENT_REGISTRY.register(2, ChatComponentEmpty.class);
    }

    private final File gameDirectory;
    private final File modsDirectory;
    private final File contentPacksDirectory;
    private final File saveDirectory;
    private final File screenshotDirectory;
    private final File modConfigDirectory;
    private final File settingsDirectory;
    private final File settingsFile;
    private final File serverSettingsFile;
    private final File commandPermissionFile;
    private final File whitelistFile;
    private final File blacklistFile;
    private final File modSettingsFile;
    private final File contentPackSettingsFile;
    private final File playerDesignFile;

    public DataManager() {
        this.gameDirectory = Main.gameDir;
        this.modsDirectory = new File(this.gameDirectory, "mods");
        this.contentPacksDirectory = new File(this.gameDirectory, "contentpacks");
        this.saveDirectory = new File(this.gameDirectory, "save");
        this.screenshotDirectory = new File(this.gameDirectory, "screenshot");
        this.modConfigDirectory = new File(this.modsDirectory, "config");
        this.settingsDirectory = new File(this.gameDirectory, "settings");

        this.playerDesignFile = new File(this.gameDirectory, "player_design.dat");
        this.settingsFile = new File(this.settingsDirectory, "settings.json");
        this.serverSettingsFile = new File(this.settingsDirectory, "server_settings.json");
        this.commandPermissionFile = new File(this.settingsDirectory, "command_permissions.json");
        this.whitelistFile = new File(this.settingsDirectory, "whitelist.json");
        this.blacklistFile = new File(this.settingsDirectory, "blacklist.json");
        this.modSettingsFile = new File(this.settingsDirectory, "mod_settings.json");
        this.contentPackSettingsFile = new File(this.settingsDirectory, "content_pack_settings.json");
    }

    @Override
    public File getGameDir() {
        return this.gameDirectory;
    }

    @Override
    public File getModsDir() {
        return this.modsDirectory;
    }

    @Override
    public File getContentPacksDir() {
        return this.contentPacksDirectory;
    }

    @Override
    public File getWorldsDir() {
        return this.saveDirectory;
    }

    @Override
    public File getScreenshotDir() {
        return this.screenshotDirectory;
    }

    @Override
    public File getSettingsFile() {
        return this.settingsFile;
    }

    @Override
    public File getServerSettingsFile() {
        return this.serverSettingsFile;
    }

    @Override
    public File getCommandPermsFile() {
        return this.commandPermissionFile;
    }

    @Override
    public File getWhitelistFile() {
        return this.whitelistFile;
    }

    @Override
    public File getBlacklistFile() {
        return this.blacklistFile;
    }

    @Override
    public File getModSettingsFile() {
        return this.modSettingsFile;
    }

    @Override
    public File getContentPackSettingsFile() {
        return this.contentPackSettingsFile;
    }

    @Override
    public File getPlayerDesignFile() {
        return this.playerDesignFile;
    }

    @Override
    public File getModConfigFolder() {
        return this.modConfigDirectory;
    }

    @Override
    public File getSettingsFolder() {
        return this.settingsDirectory;
    }

    @Override
    public void loadSettings(IJsonSettings settings) {
        JsonObject object = null;
        boolean loaded = false;

        File file = settings.getSettingsFile(this);
        if (file.exists()) {
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), Charsets.UTF_8);
                object = Util.JSON_PARSER.parse(reader).getAsJsonObject();
                reader.close();

                loaded = true;
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.WARNING, "Couldn't load " + settings.getName(), e);
            }
        }

        try {
            settings.load(object == null ? new JsonObject() : object);
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't parse " + settings.getName(), e);
        }

        if (!loaded) {
            RockBottomAPI.logger().info("Creating " + settings.getName() + " from default");
            settings.save();
        } else {
            RockBottomAPI.logger().info("Loaded " + settings.getName());
        }
    }

    @Override
    public void saveSettings(IJsonSettings settings) {
        JsonObject object = new JsonObject();

        try {
            settings.save(object);
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't jsonify " + settings.getName(), e);
        }

        try {
            File file = settings.getSettingsFile(this);

            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();

                RockBottomAPI.logger().info("Creating file for " + settings.getName() + " at " + file);
            }

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
            Util.GSON.toJson(object, writer);
            writer.close();
        } catch (Exception e) {
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't save " + settings.getName(), e);
        }
    }
}
