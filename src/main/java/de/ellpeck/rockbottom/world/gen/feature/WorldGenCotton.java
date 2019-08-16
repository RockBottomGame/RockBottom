package de.ellpeck.rockbottom.world.gen.feature;


import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class WorldGenCotton implements IWorldGenerator {
    private final Random lakeRand = new Random();
    private final Random cottonRand = new Random();

    public WorldGenCotton() {
    }

    public void initWorld(IWorld var1) {
        this.cottonRand.setSeed(Util.scrambleSeed(93719028, var1.getSeed()));
    }

    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return true;
    }

    public void generate(IWorld world, IChunk chunk) {
        this.lakeRand.setSeed(Util.scrambleSeed(chunk.getGridX(), chunk.getGridY(), world.getSeed()));
        if (lakeRand.nextFloat() <= chunk.getMostProminentBiome().getLakeChance(world, chunk)) {
            for (int x = 0; x < 31; x++) {
                int height = chunk.getHeightInner(TileLayer.LIQUIDS, x);
                if (height > 0 && height < 31) {
                    TileState water = chunk.getStateInner(TileLayer.LIQUIDS, x, height - 1);
                    height -= 1;
                    x += 1;
                    int left = x - 2;
                    if (water.getTile() == GameContent.TILE_WATER && chunk.getStateInner(x, height).getTile().canKeepPlants(world, chunk.getX() + x, chunk.getY() + height, TileLayer.MAIN) && chunk.getStateInner(TileLayer.LIQUIDS, x, height).getTile().isAir() && chunk.getStateInner(x, height + 1).getTile().canReplace(world, chunk.getX() + x, chunk.getY() + height + 1, TileLayer.MAIN) && chunk.getStateInner(x, height + 2).getTile().canReplace(world, chunk.getX() + x, chunk.getY() + height + 2, TileLayer.MAIN)) {
                        placeCotton(chunk, x, height);
                    } else if (water.getTile() == GameContent.TILE_WATER && chunk.getStateInner(left, height).getTile().canKeepPlants(world, chunk.getX() + left, chunk.getY() + height, TileLayer.MAIN) && chunk.getStateInner(TileLayer.LIQUIDS, left, height).getTile().isAir() && chunk.getStateInner(left, height + 1).getTile().canReplace(world, chunk.getX() + left, chunk.getY() + height + 1, TileLayer.MAIN) && chunk.getStateInner(left, height + 2).getTile().canReplace(world, chunk.getX() + left, chunk.getY() + height + 2, TileLayer.MAIN) && cottonRand.nextBoolean()) {
                        placeCotton(chunk, left, height);
                        return;
                    }

                }
            }
        }

    }

    private void placeCotton(IChunk chunk, int x, int y) {
        chunk.setStateInner(x, y, GameContent.TILE_SOIL_TILLED.getDefState());
        chunk.setStateInner(x, y + 1, GameContent.TILE_COTTON.getDefState().prop(StaticTileProps.PLANT_GROWTH, 9).prop(StaticTileProps.ALIVE, true));
        chunk.setStateInner(x, y + 2, GameContent.TILE_COTTON.getDefState().prop(StaticTileProps.TOP_HALF, true).prop(StaticTileProps.PLANT_GROWTH, 9).prop(StaticTileProps.ALIVE, true));
    }

    public int getPriority() {
        return -50;
    }
}