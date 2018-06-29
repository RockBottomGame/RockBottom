package de.ellpeck.rockbottom.world.gen.biome;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.gen.biome.BiomeBasic;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class BiomeGrassland extends BiomeBasic {

    public BiomeGrassland(ResourceName name, int weight, BiomeLevel... levels) {
        super(name, weight, levels);
    }

    public static TileState getState(TileLayer layer, int y, int height, int stoneHeight) {
        if (layer == TileLayer.MAIN || layer == TileLayer.BACKGROUND) {
            if (y == height && layer == TileLayer.MAIN) {
                return GameContent.TILE_GRASS.getDefState();
            } else if (y <= height) {
                if (y >= stoneHeight) {
                    return GameContent.TILE_SOIL.getDefState();
                } else {
                    return GameContent.TILE_STONE.getDefState();
                }
            }
        }
        return GameContent.TILE_AIR.getDefState();
    }

    @Override
    public TileState getState(IWorld world, IChunk chunk, int x, int y, TileLayer layer, INoiseGen noise, int surfaceHeight) {
        int stoneHeight = surfaceHeight - Util.ceil(noise.make2dNoise((chunk.getX() + x) / 5D, 0D) * 3D) - 2;
        return getState(layer, chunk.getY() + y, surfaceHeight, stoneHeight);
    }

    @Override
    public boolean hasGrasslandDecoration() {
        return true;
    }

    @Override
    public float getFlowerChance() {
        return 0.35F;
    }

    @Override
    public float getPebbleChance() {
        return 0.2F;
    }

    @Override
    public boolean canTreeGrow(IWorld world, IChunk chunk, int x, int y) {
        return y > 0 && chunk.getStateInner(x, y - 1).getTile().canKeepPlants(world, chunk.getX() + x, chunk.getY() + y, TileLayer.MAIN);
    }

    @Override
    public Biome getVariationToGenerate(IWorld world, int x, int y, int surfaceHeight, Random random) {
        double chance = Math.max(0, Math.min(1, (surfaceHeight - 20) / 5D));
        random.setSeed(Util.scrambleSeed(x, y, world.getSeed()) + 12382342);
        return random.nextDouble() < chance ? GameContent.BIOME_COLD_GRASSLAND : this;
    }
}
