package de.ellpeck.game.data;

import de.ellpeck.game.Game;
import de.ellpeck.game.Settings;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.data.set.part.DataPart;
import de.ellpeck.game.data.set.part.PartBoolean;
import de.ellpeck.game.data.set.part.PartDataSet;
import de.ellpeck.game.data.set.part.PartUniqueId;
import de.ellpeck.game.data.set.part.num.*;
import de.ellpeck.game.data.set.part.num.array.PartByteByteArray;
import de.ellpeck.game.data.set.part.num.array.PartIntArray;
import de.ellpeck.game.data.set.part.num.array.PartShortShortArray;
import de.ellpeck.game.util.Registry;
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
    }

    public File gameDirectory;
    public File saveDirectory;
    public File gameDataFile;
    public File settingsFile;

    public DataManager(Game game){
        this.gameDirectory = new File(".", "game");
        this.saveDirectory = new File(this.gameDirectory, "save");

        this.gameDataFile = new File(this.gameDirectory, "game_info.dat");
        this.settingsFile = new File(this.gameDirectory, "settings.properties");

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

    public Settings loadSettings(){
        Properties props = new Properties();
        boolean loaded = false;

        if(this.settingsFile.exists()){
            try{
                props.load(new FileInputStream(this.settingsFile));
                loaded = true;
            }
            catch(Exception e){
                Log.error("Couldn't load game settings!", e);
            }
        }

        Settings settings = new Settings(props);
        settings.load();

        if(!loaded){
            Log.info("Creating game settings from default.");
            this.saveSettings(settings);
        }
        else{
            Log.info("Loaded game settings.");
        }

        return settings;
    }

    public void saveSettings(Settings settings){
        Properties props = settings.save();

        try{
            if(!this.settingsFile.exists()){
                this.settingsFile.getParentFile().mkdirs();
                this.settingsFile.createNewFile();
            }

            props.store(new FileOutputStream(this.settingsFile), null);
        }
        catch(Exception e){
            Log.error("Couldn't save game settings!", e);
        }
    }
}
