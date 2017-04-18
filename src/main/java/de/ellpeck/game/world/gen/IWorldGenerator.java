package de.ellpeck.game.world.gen;

import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.World;

import java.util.Random;

public interface IWorldGenerator{

    boolean shouldGenerate(World world, Chunk chunk);

    void generate(World world, Chunk chunk, Random rand);

    int getPriority();

}
