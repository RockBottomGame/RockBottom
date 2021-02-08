package de.ellpeck.rockbottom.world.gen.feature;


import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.Random;

public class CottonWorldGen implements IWorldGenerator {
    private final Random random = new Random();
    private long seed;

    public void initWorld(IWorld world) {
        this.seed = Util.scrambleSeed(19264959, world.getSeed());
    }

    public boolean shouldGenerate(IWorld world, IChunk chunk) {
        return true;
    }

    public void generate(IWorld world, IChunk chunk) {
        this.random.setSeed(Util.scrambleSeed(chunk.getX(), chunk.getY(), this.seed));
        if (this.random.nextBoolean()) {
            int x = this.random.nextInt(Constants.CHUNK_SIZE - 4) + 2;
            int y = chunk.getHeightInner(TileLayer.MAIN, x);
            if (y > 0 && y < Constants.CHUNK_SIZE - 1) {
                for (int xOff = -2; xOff <= 2; xOff++) {
                    int tileXInChunk = x + xOff;
                    int tileX = chunk.getX() + tileXInChunk;
                    int tileY = chunk.getY() + y;

                    Tile aboveTile = chunk.getStateInner(tileXInChunk, y + 1).getTile();
                    Tile belowTile = chunk.getStateInner(tileXInChunk, y - 1).getTile();
                    Tile thisTile = chunk.getStateInner(tileXInChunk, y).getTile();
                    Tile thisLiquidTile = chunk.getStateInner(TileLayer.LIQUIDS, tileXInChunk, y).getTile();

                    if (belowTile.canKeepPlants(world, tileX, tileY - 1, TileLayer.MAIN) && thisLiquidTile.isAir()) {
                        if (thisTile.canReplace(world, tileX, tileY, TileLayer.MAIN) && aboveTile.canReplace(world, tileX, tileY + 1, TileLayer.MAIN)) {
                            chunk.setStateInner(tileXInChunk, y - 1, GameContent.Tiles.TILLED_SOIL.getDefState());
                            chunk.setStateInner(tileXInChunk, y, GameContent.Tiles.COTTON.getDefState().prop(StaticTileProps.PLANT_GROWTH, 9));
                            chunk.setStateInner(tileXInChunk, y + 1, GameContent.Tiles.COTTON.getDefState().prop(StaticTileProps.TOP_HALF, true).prop(StaticTileProps.PLANT_GROWTH, 9));
                        }
                    }
                }
            }
        }
    }

    public int getPriority() {
        return -50;
    }
}