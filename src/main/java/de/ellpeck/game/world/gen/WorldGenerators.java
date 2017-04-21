package de.ellpeck.game.world.gen;

import de.ellpeck.game.world.gen.feature.WorldGenTrees;
import de.ellpeck.game.world.gen.landscape.WorldGenBasicUnderground;
import de.ellpeck.game.world.gen.landscape.WorldGenDebugLandscape;
import de.ellpeck.game.world.gen.landscape.WorldGenHills;
import de.ellpeck.game.world.gen.ore.WorldGenCoal;
import org.newdawn.slick.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class WorldGenerators{

    private static final List<IWorldGenerator> GENERATORS = new ArrayList<>();
    private static boolean initialized;

    static {
        registerGenerator(new WorldGenDebugLandscape());
        registerGenerator(new WorldGenHills());
        registerGenerator(new WorldGenBasicUnderground());
        registerGenerator(new WorldGenTrees());
        registerGenerator(new WorldGenCoal());
    }

    public static List<IWorldGenerator> getGenerators(){
        if(!initialized){
            Log.info("Initializing and sorting a total of "+GENERATORS.size()+" world generators");

            GENERATORS.sort(Comparator.comparingInt(IWorldGenerator:: getPriority));
            initialized = true;
        }

        return Collections.unmodifiableList(GENERATORS);
    }

    public static void registerGenerator(IWorldGenerator generator){
        if(!initialized){
            GENERATORS.add(generator);
        }
        else{
            throw new RuntimeException("Tried registering world generator "+generator+" after generators were already accessed!");
        }
    }
}
