package de.ellpeck.game.data;

import de.ellpeck.game.Game;
import de.ellpeck.game.data.set.DataSet;
import de.ellpeck.game.data.set.part.DataPart;
import de.ellpeck.game.data.set.part.PartUniqueId;
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
import java.util.UUID;

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
        PART_REGISTRY.register(8, PartUniqueId.class);
    }

    public File gameDirectory;
    public File saveDirectory;
    public File gameDataFile;

    public DataManager(Game game){
        this.gameDirectory = new File(".", "game");
        this.saveDirectory = new File(this.gameDirectory, "save");

        this.gameDataFile = new File(this.gameDirectory, "game_info.dat");

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

}
