package de.ellpeck.game.data;

import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.part.DataPart;
import de.ellpeck.game.data.set.part.PartDataSet;
import de.ellpeck.game.data.set.part.num.PartDouble;
import de.ellpeck.game.data.set.part.num.PartFloat;
import de.ellpeck.game.data.set.part.num.PartInt;
import de.ellpeck.game.data.set.part.num.PartLong;
import de.ellpeck.game.data.set.part.num.array.PartByteByteArray;
import de.ellpeck.game.data.set.part.num.array.PartIntArray;
import de.ellpeck.game.data.set.part.num.array.PartIntIntArray;
import de.ellpeck.game.util.Registry;
import org.newdawn.slick.util.Log;

import java.io.File;

public class DataManager{

    public static final Registry<Class<? extends DataPart>> PART_REGISTRY = new Registry<>();

    static{
        PART_REGISTRY.register(0, PartInt.class);
        PART_REGISTRY.register(1, PartFloat.class);
        PART_REGISTRY.register(2, PartDouble.class);
        PART_REGISTRY.register(3, PartIntArray.class);
        PART_REGISTRY.register(4, PartIntIntArray.class);
        PART_REGISTRY.register(5, PartByteByteArray.class);
        PART_REGISTRY.register(6, PartDataSet.class);
        PART_REGISTRY.register(7, PartLong.class);
    }

    public File gameDirectory;
    public File saveDirectory;
    public File worldFile;

    public DataManager(Game game){
        this.gameDirectory = new File(".", "game");
        if(!this.gameDirectory.exists()){
            this.gameDirectory.mkdirs();
            Log.info("Created game directory at "+this.gameDirectory.getAbsolutePath());
        }

        this.saveDirectory = new File(this.gameDirectory, "save");
        if(!this.saveDirectory.exists()){
            this.saveDirectory.mkdir();
            Log.info("Created save directory at "+this.saveDirectory.getAbsolutePath());
        }

        this.worldFile = new File(this.saveDirectory, "world.dat");
    }

}
