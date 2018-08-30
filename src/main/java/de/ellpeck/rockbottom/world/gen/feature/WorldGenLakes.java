package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.TileLiquid;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class WorldGenLakes implements IWorldGenerator {

    private final Random genRandom = new Random();

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return chunk.getFlatness(TileLayer.MAIN) >= 0.5F;
    }

    @Override
    public void generate(IWorld world, IChunk chunk) {
        this.genRandom.setSeed(Util.scrambleSeed(chunk.getGridX(), chunk.getGridY(), world.getSeed()));
        if (this.genRandom.nextFloat() <= chunk.getMostProminentBiome().getLakeChance(world, chunk)) {

            int width = this.genRandom.nextInt(15) + 8;
            int startX = this.genRandom.nextInt(Constants.CHUNK_SIZE - width - 2) + 1;
            int endX = startX + width;

            int startY = Math.min(chunk.getHeightInner(TileLayer.MAIN, startX - 1), chunk.getHeightInner(TileLayer.MAIN, endX + 1)) - 1;
            if (startY > 2 && startY < Constants.CHUNK_SIZE - 3) {
                int depth = Util.clamp(this.genRandom.nextInt(startY - 2), 3, 5);

                for (int x = startX; x <= endX; x++) {
                    int depthX = Util.ceil(depth * (1F - Math.abs(x - startX - width / 2F) / width * 2F)) - this.genRandom.nextInt(2);
                    for (int y = startY - depthX + 1; y <= startY; y++) {
                        chunk.setStateInner(x, y, GameContent.TILE_AIR.getDefState());

                        TileLiquid water = GameContent.TILE_WATER;
                        if (y == startY) {
                            chunk.setStateInner(TileLayer.LIQUIDS, x, y, water.getDefState().prop(water.level, water.getLevels() - 3));
                        } else {
                            chunk.setStateInner(TileLayer.LIQUIDS, x, y, water.getFullState());
                        }
                    }

                    if (chunk.getStateInner(x, startY - depthX).getTile().canLiquidSpreadInto(world, x, startY - depthX, GameContent.TILE_WATER)) {
                        chunk.setStateInner(x, startY - depthX, chunk.getBiomeInner(x, startY - depthX).getFillerTile(world, chunk, x, startY - depthX));
                    }

                    for (int y = this.genRandom.nextInt(3) + 1; y > 0; y--) {
                        chunk.setStateInner(x, startY + y, GameContent.TILE_AIR.getDefState());
                    }
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return 200;
    }
}
