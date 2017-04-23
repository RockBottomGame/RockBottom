package de.ellpeck.rockbottom.data;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.data.set.DataSet;
import de.ellpeck.rockbottom.data.set.part.*;
import de.ellpeck.rockbottom.data.set.part.num.*;
import de.ellpeck.rockbottom.data.set.part.num.array.PartByteByteArray;
import de.ellpeck.rockbottom.data.set.part.num.array.PartIntArray;
import de.ellpeck.rockbottom.data.set.part.num.array.PartShortShortArray;
import de.ellpeck.rockbottom.settings.IPropSettings;
import de.ellpeck.rockbottom.util.Registry;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.UUID;

public class DataManager{

    public static final Registry<Class<? extends DataPart>> PART_REGISTRY = new Registry<>("part_registry", Byte.MAX_VALUE);

    static{
        PART_REGISTRY.register(0, PartInt.class);
        PART_REGISTRY.register(1, PartFloat.class);
        PART_REGISTRY.register(2, PartDouble.class);
        PART_REGISTRY.register(3, PartIntArray.class);
        PART_REGISTRY.register(4, PartShortShortArray.class);
        PART_REGISTRY.register(5, PartByteByteArray.class);
        PART_REGISTRY.register(6, PartDataSet.class);
        PART_REGISTRY.register(7, PartLong.class);
        PART_REGISTRY.register(8, PartUniqueId.class);
        PART_REGISTRY.register(9, PartByte.class);
        PART_REGISTRY.register(10, PartShort.class);
        PART_REGISTRY.register(11, PartBoolean.class);
        PART_REGISTRY.register(12, PartString.class);
    }

    public File gameDirectory;
    public File saveDirectory;
    public File gameDataFile;
    public File settingsFile;
    public File commandPermissionFile;

    public DataManager(RockBottom game){
        this.gameDirectory = new File(".", "rockbottom");
        this.saveDirectory = new File(this.gameDirectory, "save");

        this.gameDataFile = new File(this.gameDirectory, "game_info.dat");
        this.settingsFile = new File(this.gameDirectory, "settings.properties");
        this.commandPermissionFile = new File(this.gameDirectory, "command_permissions.properties");

        DataSet set = new DataSet();
        set.read(this.gameDataFile);

        game.uniqueId = set.getUniqueId("game_id");

        if(game.uniqueId == null){
            game.uniqueId = UUID.randomUUID();
            set.addUniqueId("game_id", game.uniqueId);

            Log.info("Created new game unique id "+game.uniqueId+"!");
        }

        set.write(this.gameDataFile);
    }

    public void loadPropSettings(IPropSettings settings, File file){
        Properties props = new Properties();
        boolean loaded = false;

        if(file.exists()){
            try{
                props.load(new FileInputStream(file));
                loaded = true;
            }
            catch(Exception e){
                Log.error("Couldn't load game settings!", e);
            }
        }

        settings.load(props);

        if(!loaded){
            Log.info("Creating game settings from default.");
            this.savePropSettings(settings, file);
        }
        else{
            Log.info("Loaded game settings.");
        }
    }

    public void savePropSettings(IPropSettings settings, File file){
        Properties props = new Properties();
        settings.save(props);

        try{
            if(!file.exists()){
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            props.store(new FileOutputStream(file), null);
        }
        catch(Exception e){
            Log.error("Couldn't save game settings!", e);
        }
    }
}
