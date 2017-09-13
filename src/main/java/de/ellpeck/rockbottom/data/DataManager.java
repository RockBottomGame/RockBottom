package de.ellpeck.rockbottom.data;

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
import de.ellpeck.rockbottom.api.data.settings.IPropSettings;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentText;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponentTranslation;
import de.ellpeck.rockbottom.api.net.packet.toclient.PacketDeath;
import de.ellpeck.rockbottom.api.net.packet.toclient.PacketTileEntityData;
import de.ellpeck.rockbottom.api.net.packet.toserver.PacketDropItem;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.net.packet.toclient.*;
import de.ellpeck.rockbottom.net.packet.toserver.*;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

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
        RockBottomAPI.PACKET_REGISTRY.register(26, PacketTableConstruction.class);

        RockBottomAPI.CHAT_COMPONENT_REGISTRY.register(0, ChatComponentText.class);
        RockBottomAPI.CHAT_COMPONENT_REGISTRY.register(1, ChatComponentTranslation.class);
    }

    private final File gameDirectory;
    private final File modsDirectory;
    private final File saveDirectory;
    private final File screenshotDirectory;
    private final File gameDataFile;
    private final File settingsFile;
    private final File commandPermissionFile;
    private final File modSettingsFile;
    private final File playerDesignFile;
    private final DataSet gameInfo = new DataSet();

    public DataManager(AbstractGame game){
        this.gameDirectory = Main.gameDir;
        this.modsDirectory = new File(this.gameDirectory, "mods");
        this.saveDirectory = new File(this.gameDirectory, "save");
        this.screenshotDirectory = new File(this.gameDirectory, "screenshot");

        this.gameDataFile = new File(this.gameDirectory, "game_info.dat");
        this.playerDesignFile = new File(this.gameDirectory, "player_design.dat");
        this.settingsFile = new File(this.gameDirectory, "settings.properties");
        this.commandPermissionFile = new File(this.gameDirectory, "command_permissions.properties");
        this.modSettingsFile = new File(this.gameDirectory, "mod_settings.properties");

        if(!game.isDedicatedServer()){
            this.gameInfo.read(this.gameDataFile);

            game.setUniqueId(this.gameInfo.getUniqueId("game_id"));
            if(game.getUniqueId() == null){
                game.setUniqueId(UUID.randomUUID());

                this.gameInfo.addUniqueId("game_id", game.getUniqueId());
                this.gameInfo.write(this.gameDataFile);

                Log.info("Created new game unique id "+game.getUniqueId()+"!");
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
    public File getCommandPermsFile(){
        return this.commandPermissionFile;
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
                props.load(new FileInputStream(file));
                loaded = true;
            }
            catch(Exception e){
                Log.error("Couldn't load "+settings.getName(), e);
            }
        }

        settings.load(props);

        if(!loaded){
            Log.info("Creating "+settings.getName()+" from default");
            this.savePropSettings(settings);
        }
        else{
            Log.info("Loaded "+settings.getName());
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
            Log.error("Couldn't save "+settings.getName(), e);
        }
    }
}
