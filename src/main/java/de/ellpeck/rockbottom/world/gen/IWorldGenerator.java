package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;

import java.util.Random;

public interface IWorldGenerator{

    boolean shouldGenerate(World world, Chunk chunk, Random rand);

    void generate(World world, Chunk chunk, Random rand);

    int getPriority();

}
