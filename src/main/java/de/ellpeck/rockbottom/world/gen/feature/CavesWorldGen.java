package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class CavesWorldGen implements IWorldGenerator {

    private INoiseGen noiseGen;

    @Override
    public void initWorld(IWorld world) {
        this.noiseGen = RockBottomAPI.getApiHandler().makeSimplexNoise(world.getSeed());
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                int theX = chunk.getX() + x;
                int theY = chunk.getY() + y;

                double mod = Math.min(0.65D, -0.25D * (theY - chunk.getAverageHeight(TileLayer.MAIN)));
                if (mod > 0D) {
                    double noise = this.noiseGen.make2dNoise(theX / 40D, theY / 10D);
                    noise += this.noiseGen.make2dNoise(theX / 30D, theY / 50D) * 0.5D;
                    noise += this.noiseGen.make2dNoise(theX / 10D, theY / 100D) * 0.25D;

                    if (noise <= mod) {
                        chunk.setStateInner(x, y, GameContent.TILE_AIR.getDefState());
                    }
                }
            }
        }
    }

    @Override
    public int getPriority() {
        return 1000;
    }
}
