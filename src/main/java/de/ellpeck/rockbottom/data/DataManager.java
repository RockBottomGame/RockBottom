package de.ellpeck.rockbottom.data;

import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.part.PartBoolean;
import de.ellpeck.rockbottom.api.data.set.part.PartDataSet;
import de.ellpeck.rockbottom.api.data.set.part.PartString;
import de.ellpeck.rockbottom.api.data.set.part.PartUniqueId;
import de.ellpeck.rockbottom.api.data.set.part.num.*;
import de.ellpeck.rockbottom.api.data.set.part.num.array.PartByteArray;
import de.ellpeck.rockbottom.api.data.set.part.num.array.PartIntArray;
import de.ellpeck.rockbottom.api.data.set.part.num.array.PartShortArray;
import de.ellpeck.rockbottom.api.data.settings.IJsonSettings;
import de.ellpeck.rockbottom.api.data.settings.IPropSettings;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentEmpty;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.toclient.PacketDeath;
import de.ellpeck.rockbottom.api.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.rockbottom.api.net.packet.toserver.PacketDropItem;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.net.packet.toserver.*;

import java.io.*;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager implements IDataManager{

    static{
        RockBottomAPI.PART_REGISTRY.register(0, PartInt.class);
        RockBottomAPI.PART_REGISTRY.register(1, PartFloat.class);
        RockBottomAPI.PART_REGISTRY.register(2, PartDouble.class);
        RockBottomAPI.PART_REGISTRY.register(3, PartIntArray.class);
        RockBottomAPI.PART_REGISTRY.register(4, PartShortArray.class);
        RockBottomAPI.PART_REGISTRY.register(5, PartByteArray.class);
        RockBottomAPI.PART_REGISTRY.register(6, PartDataSet.class);
        RockBottomAPI.PART_REGISTRY.register(7, PartLong.class);
        RockBottomAPI.PART_REGISTRY.register(8, PartUniqueId.class);
        RockBottomAPI.PART_REGISTRY.register(9, PartByte.class);
        RockBottomAPI.PART_REGISTRY.register(10, PartShort.class);
        RockBottomAPI.PART_REGISTRY.register(11, PartBoolean.class);
        RockBottomAPI.PART_REGISTRY.register(12, PartString.class);

        RockBottomAPI.PACKET_REGISTRY.register(0, PacketJoin.class);
        RockBottomAPI.PACKET_REGISTRY.register(1, PacketChunk.class);
        RockBottomAPI.PACKET_REGISTRY.register(2, PacketInitialServerData.class);
        RockBottomAPI.PACKET_REGISTRY.register(3, PacketDisconnect.class);
        RockBottomAPI.PACKET_REGISTRY.register(4, PacketTileChange.class);
        RockBottomAPI.PACKET_REGISTRY.register(5, PacketScheduledUpdate.class);
        RockBottomAPI.PACKET_REGISTRY.register(6, PacketEntityChange.class);
        RockBottomAPI.PACKET_REGISTRY.register(7, PacketBreakTile.class);
        RockBottomAPI.PACKET_REGISTRY.register(8, PacketParticles.class);
        RockBottomAPI.PACKET_REGISTRY.register(9, PacketEntityUpdate.class);
        RockBottomAPI.PACKET_REGISTRY.register(10, PacketPlayerMovement.class);
        RockBottomAPI.PACKET_REGISTRY.register(11, PacketInteract.class);
        RockBottomAPI.PACKET_REGISTRY.register(12, PacketHotbar.class);
        RockBottomAPI.PACKET_REGISTRY.register(13, PacketTileEntityData.class);
        RockBottomAPI.PACKET_REGISTRY.register(14, PacketSlotModification.class);
        RockBottomAPI.PACKET_REGISTRY.register(15, PacketOpenUnboundContainer.class);
        RockBottomAPI.PACKET_REGISTRY.register(16, PacketContainerData.class);
        RockBottomAPI.PACKET_REGISTRY.register(17, PacketContainerChange.class);
        RockBottomAPI.PACKET_REGISTRY.register(18, PacketChatMessage.class);
        RockBottomAPI.PACKET_REGISTRY.register(19, PacketSendChat.class);
        RockBottomAPI.PACKET_REGISTRY.register(20, PacketHealth.class);
        RockBottomAPI.PACKET_REGISTRY.register(21, PacketRespawn.class);
        RockBottomAPI.PACKET_REGISTRY.register(22, PacketDropItem.class);
        RockBottomAPI.PACKET_REGISTRY.register(23, PacketChunkUnload.class);
        RockBottomAPI.PACKET_REGISTRY.register(24, PacketManualConstruction.class);
        RockBottomAPI.PACKET_REGISTRY.register(25, PacketDeath.class);
        RockBottomAPI.PACKET_REGISTRY.register(26, PacketKnowledge.class);
        RockBottomAPI.PACKET_REGISTRY.register(27, PacketSound.class);
        RockBottomAPI.PACKET_REGISTRY.register(28, PacketActiveItem.class);
        RockBottomAPI.PACKET_REGISTRY.register(29, PacketChestOpen.class);
        RockBottomAPI.PACKET_REGISTRY.register(30, PacketSignText.class);
        RockBottomAPI.PACKET_REGISTRY.register(31, PacketReject.class);
        RockBottomAPI.PACKET_REGISTRY.register(32, PacketTime.class);
        RockBottomAPI.PACKET_REGISTRY.register(33, PacketAttack.class);
        RockBottomAPI.PACKET_REGISTRY.register(34, PacketEffect.class);

        RockBottomAPI.CHAT_COMPONENT_REGISTRY.register(0, ChatComponentText.class);
        RockBottomAPI.CHAT_COMPONENT_REGISTRY.register(1, ChatComponentTranslation.class);
        RockBottomAPI.CHAT_COMPONENT_REGISTRY.register(2, ChatComponentEmpty.class);
    }

    private final File gameDirectory;
    private final File modsDirectory;
    private final File saveDirectory;
    private final File screenshotDirectory;
    private final File modConfigDirectory;
    private final File gameDataFile;
    private final File settingsFile;
    private final File serverSettingsFile;
    private final File commandPermissionFile;
    private final File whitelistFile;
    private final File blacklistFile;
    private final File modSettingsFile;
    private final File playerDesignFile;
    private final DataSet gameInfo = new DataSet();

    public DataManager(AbstractGame game){
        this.gameDirectory = Main.gameDir;
        this.modsDirectory = new File(this.gameDirectory, "mods");
        this.saveDirectory = new File(this.gameDirectory, "save");
        this.screenshotDirectory = new File(this.gameDirectory, "screenshot");
        this.modConfigDirectory = new File(this.modsDirectory, "config");

        this.gameDataFile = new File(this.gameDirectory, "game_info.dat");
        this.playerDesignFile = new File(this.gameDirectory, "player_design.dat");
        this.settingsFile = new File(this.gameDirectory, "settings.json");
        this.serverSettingsFile = new File(this.gameDirectory, "server_settings.json");
        this.commandPermissionFile = new File(this.gameDirectory, "command_permissions.json");
        this.whitelistFile = new File(this.gameDirectory, "whitelist.json");
        this.blacklistFile = new File(this.gameDirectory, "blacklist.json");
        this.modSettingsFile = new File(this.gameDirectory, "mod_settings.json");

        if(!game.isDedicatedServer()){
            this.gameInfo.read(this.gameDataFile);

            game.setUniqueId(this.gameInfo.getUniqueId("game_id"));
            if(game.getUniqueId() == null){
                game.setUniqueId(UUID.randomUUID());

                this.gameInfo.addUniqueId("game_id", game.getUniqueId());
                this.gameInfo.write(this.gameDataFile);

                RockBottomAPI.logger().info("Created new game unique id "+game.getUniqueId()+"!");
            }
        }
    }

    @Override
    public File getGameDir(){
        return this.gameDirectory;
    }

    @Override
    public File getModsDir(){
        return this.modsDirectory;
    }

    @Override
    public File getWorldsDir(){
        return this.saveDirectory;
    }

    @Override
    public File getScreenshotDir(){
        return this.screenshotDirectory;
    }

    @Override
    public File getGameDataFile(){
        return this.gameDataFile;
    }

    @Override
    public File getSettingsFile(){
        return this.settingsFile;
    }

    @Override
    public File getServerSettingsFile(){
        return this.serverSettingsFile;
    }

    @Override
    public File getCommandPermsFile(){
        return this.commandPermissionFile;
    }

    @Override
    public File getWhitelistFile(){
        return this.whitelistFile;
    }

    @Override
    public File getBlacklistFile(){
        return this.blacklistFile;
    }

    @Override
    public File getModSettingsFile(){
        return this.modSettingsFile;
    }

    @Override
    public File getPlayerDesignFile(){
        return this.playerDesignFile;
    }

    @Override
    public File getModConfigFolder(){
        return this.modConfigDirectory;
    }

    @Override
    public DataSet getGameInfo(){
        return this.gameInfo;
    }

    @Override
    public void loadPropSettings(IPropSettings settings){
        Properties props = new Properties();
        boolean loaded = false;

        File file = settings.getFile(this);
        if(file.exists()){
            try{
                FileInputStream stream = new FileInputStream(file);
                props.load(stream);
                stream.close();
                loaded = true;
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.SEVERE, "Couldn't load "+settings.getName(), e);
            }
        }

        settings.load(props);

        if(!loaded){
            RockBottomAPI.logger().info("Creating "+settings.getName()+" from default");
            this.savePropSettings(settings);
        }
        else{
            RockBottomAPI.logger().info("Loaded "+settings.getName());
        }
    }

    @Override
    public void savePropSettings(IPropSettings settings){
        Properties props = new Properties();
        settings.save(props);

        try{
            File file = settings.getFile(this);

            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            props.store(new FileOutputStream(file), null);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't save "+settings.getName(), e);
        }
    }

    @Override
    public void loadSettings(IJsonSettings settings){
        JsonObject object = null;
        boolean loaded = false;

        File file = settings.getSettingsFile(this);
        if(file.exists() || (settings instanceof IPropSettings && ((IPropSettings)settings).getFile(this).exists())){
            try{
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                object = Util.JSON_PARSER.parse(reader).getAsJsonObject();
                reader.close();

                loaded = true;
            }
            catch(Exception e){
                if(settings instanceof IPropSettings){
                    RockBottomAPI.logger().info("Couldn't load "+settings.getName()+" as json settings, trying to load them as property settings for compatibility...");

                    try{
                        this.loadPropSettings((IPropSettings)settings);
                        ((IPropSettings)settings).getFile(this).delete();

                        settings.save();

                        return;
                    }
                    catch(Exception e2){
                        RockBottomAPI.logger().log(Level.SEVERE, "Failed loading "+settings.getName()+" as property settings", e);
                    }
                }
                else{
                    RockBottomAPI.logger().log(Level.SEVERE, "Couldn't load "+settings.getName(), e);
                }
            }
        }

        try{
            settings.load(object == null ? new JsonObject() : object);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't parse "+settings.getName(), e);
        }

        if(!loaded){
            RockBottomAPI.logger().info("Creating "+settings.getName()+" from default");
            settings.save();
        }
        else{
            RockBottomAPI.logger().info("Loaded "+settings.getName());
        }
    }

    @Override
    public void saveSettings(IJsonSettings settings){
        JsonObject object = new JsonObject();

        try{
            settings.save(object);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.WARNING, "Couldn't jsonify "+settings.getName(), e);
        }

        try{
            File file = settings.getSettingsFile(this);

            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();

                RockBottomAPI.logger().info("Creating file for "+settings.getName()+" at "+file);
            }

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            Util.GSON.toJson(object, writer);
            writer.close();
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't save "+settings.getName(), e);
        }
    }
}
