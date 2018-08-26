package de.ellpeck.rockbottom.world.gen;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldGenBiomes implements IWorldGenerator {

    public static final ResourceName ID = ResourceName.intern("biomes");
    private static final int LEVEL_TRANSITION = 7;
    private static final int BIOME_TRANSITION = 5;
    private static final int BIOME_BLOB_SIZE = 6;

    private final Map<Biome, INoiseGen> biomeNoiseGens = new HashMap<>();
    private final ListMultimap<BiomeLevel, Biome> biomesPerLevel = ArrayListMultimap.create();
    private final Map<BiomeLevel, Integer> totalWeights = new HashMap<>();
    private final long[] layerSeeds = new long[BIOME_BLOB_SIZE];
    private final Random biomeRandom = new Random();
    private INoiseGen levelHeightNoise;

    @Override
    public void initWorld(IWorld world) {
        this.levelHeightNoise = RockBottomAPI.getApiHandler().makeSimplexNoise(Util.scrambleSeed(12396837, world.getSeed()));

        Random rand = new Random(Util.scrambleSeed(827398433, world.getSeed()));
        for (int i = 0; i < BIOME_BLOB_SIZE; i++) {
            this.layerSeeds[i] = rand.nextLong();
        }

        for (Biome biome : Registries.BIOME_REGISTRY.values()) {
            List<BiomeLevel> levels = biome.getGenerationLevels(world);
            for (BiomeLevel level : Registries.BIOME_LEVEL_REGISTRY.values()) {
                if (levels.contains(level) || level.getAdditionalGenBiomes(world).contains(biome)) {
                    this.biomesPerLevel.put(level, biome);
                }
            }
        }

        for (BiomeLevel level : this.biomesPerLevel.keySet()) {
            int total = 0;
            for (Biome biome : this.biomesPerLevel.get(level)) {
                total += biome.getWeight(world);
            }
            this.totalWeights.put(level, total);
        }

        int levels = this.biomesPerLevel.keySet().size();
        Preconditions.checkState(levels == this.totalWeights.size(), "BiomesPerLevel and TotalWeights are out of sync!");
        RockBottomAPI.logger().info("Initialized " + levels + " biome levels to generate for world " + world.getName());
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk) {
        for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
            Map<TileLayer, Integer> heights = new HashMap<>();
            for (TileLayer layer : TileLayer.getAllLayers()) {
                heights.put(layer, world.getExpectedSurfaceHeight(layer, chunk.getX() + x));
            }

            for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                Biome biome = this.getBiome(world, chunk.getX() + x, chunk.getY() + y, heights.get(TileLayer.MAIN));
                chunk.setBiomeInner(x, y, biome);

                INoiseGen noise = this.getBiomeNoise(world, biome);
                for (TileLayer layer : TileLayer.getAllLayers()) {
                    chunk.setStateInner(layer, x, y, biome.getState(world, chunk, x, y, layer, noise, heights.get(layer)));
                }
            }
        }
    }

    public Biome getBiome(IWorld world, int x, int y, int height) {
        BiomeLevel level = this.getSmoothedLevelForPos(world, x, y, height);

        this.biomeRandom.setSeed(Util.scrambleSeed(x, y, world.getSeed()));
        int addX = this.biomeRandom.nextInt(BIOME_TRANSITION) - BIOME_TRANSITION / 2;

        if (level.isForcedSideBySide()) {
            return this.getBiomeFromWeightPercentage(world, x, y, x + addX, 0, level, height);
        } else {
            int addY = this.biomeRandom.nextInt(BIOME_TRANSITION) - BIOME_TRANSITION / 2;
            return this.getBiomeFromWeightPercentage(world, x, y, x + addX, y + addY, level, height);
        }
    }

    private Biome getBiomeFromWeightPercentage(IWorld world, int x, int y, int percentageX, int percentageY, BiomeLevel level, int height) {
        int totalWeight = this.totalWeights.get(level);
        int chosenWeight = Util.ceil(totalWeight * this.getBiomePercentage(world, percentageX, percentageY));

        Biome chosen = null;

        int weightCounter = 0;
        for (Biome biome : this.biomesPerLevel.get(level)) {
            weightCounter += biome.getWeight(world);

            if (weightCounter >= chosenWeight) {
                chosen = biome;
                break;
            }
        }

        if (chosen == null) {
            RockBottomAPI.logger().warning("Couldn't find a biome to generate for " + x + ", " + y + " with level " + level.getName());
            chosen = GameContent.BIOME_SKY;
        }

        return chosen.getVariationToGenerate(world, x, y, height, this.biomeRandom);
    }

    public BiomeLevel getSmoothedLevelForPos(IWorld world, int x, int y, int height) {
        BiomeLevel level = this.getLevelForPos(world, x, y, height);

        int maxY = level.getMaxY(world, x, y, height);
        if (Math.abs(maxY - y) <= LEVEL_TRANSITION) {
            int changeHeight = Util.floor(LEVEL_TRANSITION * this.levelHeightNoise.make2dNoise(x / 10D, maxY));
            if (y >= maxY - changeHeight + Util.ceil(LEVEL_TRANSITION / 2D)) {
                return this.getLevelForPos(world, x, maxY + 1, height);
            }
        } else {
            int minY = level.getMinY(world, x, y, height);
            if (Math.abs(minY - y) <= LEVEL_TRANSITION) {
                int changeHeight = Util.ceil(LEVEL_TRANSITION * (1D - this.levelHeightNoise.make2dNoise(x / 10D, minY)));
                if (y <= minY + changeHeight - Util.floor(LEVEL_TRANSITION / 2D)) {
                    return this.getLevelForPos(world, x, minY - 1, height);
                }
            }
        }
        return level;
    }

    private BiomeLevel getLevelForPos(IWorld world, int x, int y, int height) {
        BiomeLevel chosen = null;
        for (BiomeLevel level : this.biomesPerLevel.keySet()) {
            if (y >= level.getMinY(world, x, y, height) && y <= level.getMaxY(world, x, y, height)) {
                if (chosen == null || level.getPriority() >= chosen.getPriority()) {
                    chosen = level;
                }
            }
        }
        return chosen;
    }

    private double getBiomePercentage(IWorld world, int x, int y) {
        Pos2 blobPos = this.getBlobPos(x, y, world);
        this.biomeRandom.setSeed(Util.scrambleSeed(blobPos.getX(), blobPos.getY(), world.getSeed()) + world.getSeed());
        return this.biomeRandom.nextDouble();
    }

    private Pos2 getBlobPos(int x, int y, IWorld world) {
        Pos2 offset = new Pos2(x, y);
        for (int i = 0; i < BIOME_BLOB_SIZE; i++) {
            offset = this.zoomFromPos(offset, this.layerSeeds[i], world);
        }
        return offset;
    }

    private Pos2 zoomFromPos(Pos2 pos, long seed, IWorld world) {
        boolean xEven = (pos.getX() & 1) == 0;
        boolean yEven = (pos.getY() & 1) == 0;

        int halfX = pos.getX() / 2;
        int halfY = pos.getY() / 2;

        if (xEven && yEven) {
            return new Pos2(halfX, halfY);
        } else {
            this.biomeRandom.setSeed(Util.scrambleSeed(pos.getX(), pos.getY(), world.getSeed()) + seed);
            int offX = this.biomeRandom.nextBoolean() ? (pos.getX() < 0 ? -1 : 1) : 0;
            int offY = this.biomeRandom.nextBoolean() ? (pos.getY() < 0 ? -1 : 1) : 0;

            if (xEven) {
                return new Pos2(halfX, halfY + offY);
            } else if (yEven) {
                return new Pos2(halfX + offX, halfY);
            } else {
                return new Pos2(halfX + offX, halfY + offY);
            }
        }
    }

    public INoiseGen getBiomeNoise(IWorld world, Biome biome) {
        return this.biomeNoiseGens.computeIfAbsent(biome, b -> RockBottomAPI.getApiHandler().makeSimplexNoise(b.getBiomeSeed(world)));
    }

    @Override
    public int getPriority() {
        return 10000;
    }
}
