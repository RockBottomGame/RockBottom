package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class StardropsWorldGen implements IWorldGenerator {

    private final Random random = new Random();
    private long seed;

    @Override
    public void initWorld(IWorld world) {
        this.seed = Util.scrambleSeed(1239879812, world.getSeed());
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return chunk.getMostProminentBiome().hasUndergroundFeatures(world, chunk);
    }

    @Override
    public void generate(IWorld world, IChunk chunk) {
        this.random.setSeed(Util.scrambleSeed(chunk.getGridX(), chunk.getGridY(), this.seed));
        for (int i = this.random.nextInt(30) + 20; i >= 0; i--) {
            int x = this.random.nextInt(Constants.CHUNK_SIZE);
            int y = this.random.nextInt(Constants.CHUNK_SIZE);

            int worldX = chunk.getX() + x;
            int worldY = chunk.getY() + y;
            TileState state = chunk.getStateInner(x, y);

            if (state.getTile().canReplace(world, worldX, worldY, TileLayer.MAIN)) {
                if (y < Constants.CHUNK_SIZE - 1 && chunk.getStateInner(x, y + 1).getTile().isFullTile()) {
                    this.random.setSeed(Util.scrambleSeed(worldX, worldY, this.seed));
                    if (this.random.nextFloat() >= 0.95F) {
                        chunk.setStateInner(x, y, GameContent.Tiles.STARDROP.getDefState().prop(StaticTileProps.STARDROP_GROWTH, this.random.nextInt(StaticTileProps.STARDROP_GROWTH.getVariants())));
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
