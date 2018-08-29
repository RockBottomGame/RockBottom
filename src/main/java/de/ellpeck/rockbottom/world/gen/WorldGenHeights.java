package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.HeightGen;

public class WorldGenHeights extends HeightGen {

    public static final ResourceName ID = ResourceName.intern("heights");

    @Override
    public int getMinHeight(IWorld world) {
        return 0;
    }

    @Override
    public int getMaxHeight(IWorld world) {
        return 10;
    }

    @Override
    public int getMaxMountainHeight(IWorld world) {
        return 45;
    }

    @Override
    public int getNoiseScramble(IWorld world) {
        return 0;
    }

    @Override
    public int getPriority() {
        return 15000;
    }
}
