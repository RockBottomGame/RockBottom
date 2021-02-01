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

public class UndergroundBiome extends BasicBiome {

    public UndergroundBiome(ResourceName name, int weight, BiomeLevel... levels) {
        super(name, weight, levels);
    }

    @Override
    public TileState getState(IWorld world, IChunk chunk, int x, int y, TileLayer layer, INoiseGen noise, int surfaceHeight) {
        if (layer == TileLayer.MAIN || layer == TileLayer.BACKGROUND) {
            return GameContent.Tiles.STONE.getDefState();
        } else {
            return GameContent.Tiles.AIR.getDefState();
        }
    }

    @Override
    public boolean hasUndergroundFeatures(IWorld world, IChunk chunk) {
        return true;
    }

    @Override
    public TileState getFillerTile(IWorld world, IChunk chunk, int x, int y) {
        return GameContent.Tiles.STONE.getDefState();
    }
}
