package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;

import java.util.Random;

public interface IWorldGenerator{

    boolean shouldGenerate(IWorld world, IChunk chunk, Random rand);

    void generate(IWorld world, IChunk chunk, Random rand);

    int getPriority();

}
