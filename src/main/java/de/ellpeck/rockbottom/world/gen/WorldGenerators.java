package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.world.gen.feature.WorldGenTrees;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenBasicUnderground;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenDebugLandscape;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenHills;
import de.ellpeck.rockbottom.world.gen.landscape.WorldGenHollow;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenCoal;
import de.ellpeck.rockbottom.world.gen.ore.WorldGenCopper;
import org.newdawn.slick.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class WorldGenerators{

    private static boolean initialized;

    static{
        RockBottomAPI.WORLD_GENERATORS.add(new WorldGenDebugLandscape());
        RockBottomAPI.WORLD_GENERATORS.add(new WorldGenHills());
        RockBottomAPI.WORLD_GENERATORS.add(new WorldGenBasicUnderground());
        RockBottomAPI.WORLD_GENERATORS.add(new WorldGenTrees());
        RockBottomAPI.WORLD_GENERATORS.add(new WorldGenCoal());
        RockBottomAPI.WORLD_GENERATORS.add(new WorldGenCopper());
        RockBottomAPI.WORLD_GENERATORS.add(new WorldGenHollow());
    }

    public static List<IWorldGenerator> getGenerators(){
        if(!initialized){
            Log.info("Initializing and sorting a total of "+RockBottomAPI.WORLD_GENERATORS.size()+" world generators");

            RockBottomAPI.WORLD_GENERATORS.sort(Comparator.comparingInt(IWorldGenerator:: getPriority));
            initialized = true;
        }

        return Collections.unmodifiableList(RockBottomAPI.WORLD_GENERATORS);
    }
}
