package de.ellpeck.rockbottom.world.gen.biome;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.BasicBiome;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class SeaBiome extends BasicBiome {

    public SeaBiome(ResourceName name, int weight, BiomeLevel... levels) {
        super(name, weight, levels);
    }

    @Override
    public TileState getState(IWorld world, IChunk chunk, int x, int y, TileLayer layer, INoiseGen noise, int surfaceHeight) {
        int xPos = chunk.getX() + x;
        int yPos = chunk.getY() + y;
        if (layer == TileLayer.MAIN) {
            if (noise.make2dNoise(xPos, yPos) < 0.3) {
                return GameContent.TILE_SAND.getDefState();
            }
        } else if (layer == TileLayer.LIQUIDS) {
            if (yPos <= world.getSeaLevel()) {
                return GameContent.TILE_WATER.getFullState();
            }
        } else {
            return GameContent.TILE_AIR.getDefState();
        }
        return GameContent.TILE_AIR.getDefState();
    }

    @Override
    public TileState getFillerTile(IWorld world, IChunk chunk, int x, int y) {
        return GameContent.TILE_SOIL.getDefState();
    }

    @Override
    public float getLakeChance(IWorld world, IChunk chunk) {
        return 0F;
    }
}
