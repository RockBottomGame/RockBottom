package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class FlowersWorldGen implements IWorldGenerator {

    private final Random flowerRandom = new Random();

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            int y = chunk.getHeightInner(TileLayer.MAIN, x);
            if (y < Constants.CHUNK_SIZE && chunk.getStateInner(x, y).getTile().canReplace(world, chunk.getX() + x, chunk.getY() + y, TileLayer.MAIN)) {
                float chance = chunk.getBiomeInner(x, y).getFlowerChance();

                this.flowerRandom.setSeed(Util.scrambleSeed(x, y, world.getSeed()));
                if (chance > 0F && this.flowerRandom.nextFloat() <= chance) {
                    TileMeta tile = GameContent.TILE_FLOWER;
                    if (tile.canPlace(world, chunk.getX() + x, chunk.getY() + y, TileLayer.MAIN, null)) {
                        int type = Util.floor(this.flowerRandom.nextDouble() * (double) tile.metaProp.getVariants());
                        chunk.setStateInner(x, y, tile.getDefState().prop(tile.metaProp, type));
                    }
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return -110;
    }
}
