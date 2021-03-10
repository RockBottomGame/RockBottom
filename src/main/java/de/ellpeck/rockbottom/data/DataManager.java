package de.ellpeck.rockbottom.data;

import com.google.common.base.Charsets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.part.*;
import de.ellpeck.rockbottom.api.data.set.part.num.*;
import de.ellpeck.rockbottom.api.data.settings.IJsonSettings;
import de.ellpeck.rockbottom.api.net.chat.component.EmptyChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TextChatComponent;
import de.ellpeck.rockbottom.api.net.chat.component.TranslationChatComponent;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.net.packet.backandforth.OpenUnboundContainerPacket;
import de.ellpeck.rockbottom.net.packet.backandforth.StatsPacket;
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

        Registries.PACKET_REGISTRY.register(JoinPacket.NAME, 0, JoinPacket::new);
        Registries.PACKET_REGISTRY.register(ChunkPacket.NAME, 1, ChunkPacket::new);
        Registries.PACKET_REGISTRY.register(InitialServerDataPacket.NAME, 2, InitialServerDataPacket::new);
        Registries.PACKET_REGISTRY.register(DisconnectPacket.NAME, 3, DisconnectPacket::new);
        Registries.PACKET_REGISTRY.register(TileChangePacket.NAME, 4, TileChangePacket::new);
        Registries.PACKET_REGISTRY.register(ScheduledUpdatePacket.NAME, 5, ScheduledUpdatePacket::new);
        Registries.PACKET_REGISTRY.register(EntityChangePacket.NAME, 6, EntityChangePacket::new);
        Registries.PACKET_REGISTRY.register(BreakTilePacket.NAME, 7, BreakTilePacket::new);
        Registries.PACKET_REGISTRY.register(ParticlesPacket.NAME, 8, ParticlesPacket::new);
        Registries.PACKET_REGISTRY.register(EntityUpdatePacket.NAME, 9, EntityUpdatePacket::new);
        Registries.PACKET_REGISTRY.register(PlayerMovementPacket.NAME, 10, PlayerMovementPacket::new);
        Registries.PACKET_REGISTRY.register(InteractPacket.NAME, 11, InteractPacket::new);
        Registries.PACKET_REGISTRY.register(HotbarPacket.NAME, 12, HotbarPacket::new);
        Registries.PACKET_REGISTRY.register(TileEntityDataPacket.NAME, 13, TileEntityDataPacket::new);
        Registries.PACKET_REGISTRY.register(ShiftClickPacket.NAME, 14, ShiftClickPacket::new);
        Registries.PACKET_REGISTRY.register(OpenUnboundContainerPacket.NAME, 15, OpenUnboundContainerPacket::new);
        Registries.PACKET_REGISTRY.register(ContainerDataPacket.NAME, 16, ContainerDataPacket::new);
        Registries.PACKET_REGISTRY.register(ContainerChangePacket.NAME, 17, ContainerChangePacket::new);
        Registries.PACKET_REGISTRY.register(ChatMessagePacket.NAME, 18, ChatMessagePacket::new);
        Registries.PACKET_REGISTRY.register(SendChatPacket.NAME, 19, SendChatPacket::new);
        Registries.PACKET_REGISTRY.register(HealthPacket.NAME, 20, HealthPacket::new);
        Registries.PACKET_REGISTRY.register(RespawnPacket.NAME, 21, RespawnPacket::new);
        Registries.PACKET_REGISTRY.register(DropPacket.NAME, 22, DropPacket::new);
        Registries.PACKET_REGISTRY.register(ChunkUnloadPacket.NAME, 23, ChunkUnloadPacket::new);
        Registries.PACKET_REGISTRY.register(ConstructionPacket.NAME, 24, ConstructionPacket::new);
        Registries.PACKET_REGISTRY.register(DeathPacket.NAME, 25, DeathPacket::new);
        Registries.PACKET_REGISTRY.register(KnowledgePacket.NAME, 26, KnowledgePacket::new);
        Registries.PACKET_REGISTRY.register(RecipesToastPacket.NAME, 27, RecipesToastPacket::new);
        Registries.PACKET_REGISTRY.register(SoundPacket.NAME, 28, SoundPacket::new);
        Registries.PACKET_REGISTRY.register(ActiveItemPacket.NAME, 29, ActiveItemPacket::new);
        Registries.PACKET_REGISTRY.register(ChestOpenPacket.NAME, 30, ChestOpenPacket::new);
        Registries.PACKET_REGISTRY.register(SignTextPacket.NAME, 31, SignTextPacket::new);
        Registries.PACKET_REGISTRY.register(RejectPacket.NAME, 32, RejectPacket::new);
        Registries.PACKET_REGISTRY.register(TimePacket.NAME, 33, TimePacket::new);
        Registries.PACKET_REGISTRY.register(AttackPacket.NAME, 34, AttackPacket::new);
        Registries.PACKET_REGISTRY.register(EffectPacket.NAME, 35, EffectPacket::new);
        Registries.PACKET_REGISTRY.register(SetOrPickHoldingPacket.NAME, 36, SetOrPickHoldingPacket::new);
        Registries.PACKET_REGISTRY.register(StatsPacket.NAME, 37, StatsPacket::new);
        Registries.PACKET_REGISTRY.register(AITaskPacket.NAME, 38, AITaskPacket::new);
        Registries.PACKET_REGISTRY.register(ToolBreakPacket.NAME, 39, ToolBreakPacket::new);
        Registries.PACKET_REGISTRY.register(DamagePacket.NAME, 40, DamagePacket::new);
        Registries.PACKET_REGISTRY.register(SkillPacket.NAME, 41, SkillPacket::new);
        Registries.PACKET_REGISTRY.register(PlayerPacket.NAME, 42, PlayerPacket::new);
        Registries.PACKET_REGISTRY.register(ChangeWorldPacket.NAME, 43, ChangeWorldPacket::new);
        Registries.PACKET_REGISTRY.register(PickupPacket.NAME, 44, PickupPacket::new);

        Registries.CHAT_COMPONENT_REGISTRY.register(0, TextChatComponent.class);
        Registries.CHAT_COMPONENT_REGISTRY.register(1, TranslationChatComponent.class);
        Registries.CHAT_COMPONENT_REGISTRY.register(2, EmptyChatComponent.class);
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
                object = JsonParser.parseReader(reader).getAsJsonObject();
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
