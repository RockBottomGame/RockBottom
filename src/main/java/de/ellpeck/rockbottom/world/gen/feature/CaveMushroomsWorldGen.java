package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class CaveMushroomsWorldGen implements IWorldGenerator {

    private final Random random = new Random();
    private long seed;

    @Override
    public void initWorld(IWorld world) {
        this.seed = Util.scrambleSeed(12378123, world.getSeed());
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return chunk.getMostProminentBiome().hasUndergroundFeatures(world, chunk);
    }

    @Override
    public void generate(IWorld world, IChunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                int worldX = chunk.getX() + x;
                int worldY = chunk.getY() + y;
                TileState state = chunk.getStateInner(x, y);

                if (state.getTile().canReplace(world, worldX, worldY, TileLayer.MAIN)) {
                    TileMeta tile = GameContent.TILE_CAVE_MUSHROOM;
                    if (y > 0 && tile.canPlace(world, worldX, worldY, TileLayer.MAIN, null)) {
                        this.random.setSeed(Util.scrambleSeed(worldX, worldY, this.seed));
                        if (this.random.nextFloat() >= 0.8F) {
                            int variation = this.random.nextInt(tile.metaProp.getVariants());
                            chunk.setStateInner(x, y, tile.getDefState().prop(tile.metaProp, variation));
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return -100;
    }
}
