package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class WorldGenCorn implements IWorldGenerator {

    private final Random cornRandom = new Random();
    private long seed;

    @Override
    public void initWorld(IWorld world) {
        this.seed = Util.scrambleSeed(12378123, world.getSeed());
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk) {
        this.cornRandom.setSeed(Util.scrambleSeed(chunk.getX(), chunk.getY(), this.seed));
        if (this.cornRandom.nextBoolean()) {
            int x = this.cornRandom.nextInt(Constants.CHUNK_SIZE - 4) + 2;
            int y = chunk.getHeightInner(TileLayer.MAIN, x);
            if (y > 0 && y < Constants.CHUNK_SIZE - 1) {
                for (int xOff = -2; xOff <= 2; xOff++) {
                    if (chunk.getStateInner(x + xOff, y - 1).getTile().canKeepPlants(world, chunk.getX() + x + xOff, chunk.getY() + y - 1, TileLayer.MAIN) && chunk.getStateInner(TileLayer.LIQUIDS, x + xOff, y).getTile().isAir()) {
                        if (chunk.getStateInner(x + xOff, y).getTile().canReplace(world, chunk.getX() + x + xOff, chunk.getY() + y, TileLayer.MAIN) && chunk.getStateInner(x + xOff, y + 1).getTile().canReplace(world, chunk.getX() + x + xOff, chunk.getY() + y + 1, TileLayer.MAIN)) {
                            chunk.setStateInner(x + xOff, y - 1, GameContent.TILE_SOIL_TILLED.getDefState());

                            chunk.setStateInner(x + xOff, y, GameContent.TILE_CORN.getDefState().prop(StaticTileProps.CORN_GROWTH, 9));
                            chunk.setStateInner(x + xOff, y + 1, GameContent.TILE_CORN.getDefState().prop(StaticTileProps.TOP_HALF, true).prop(StaticTileProps.CORN_GROWTH, 9));
                        }
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
