package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.item.ItemStartNote;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;

import java.util.Random;

public class WorldGenStartHut implements IWorldGenerator{

    private static final int WIDTH = 9;
    private static final int HEIGHT = 4;

    private final Random generatorRandom = new Random();
    private int chunkX;

    @Override
    public void initWorld(IWorld world){
        this.generatorRandom.setSeed(Util.scrambleSeed(7834, world.getSeed()));
        this.chunkX = this.generatorRandom.nextInt(2)-1;
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return chunk.getGridX() == this.chunkX && (!world.hasAdditionalData() || !world.getAdditionalData().getBoolean("start_house_created"));
    }

    @Override
    public void generate(IWorld world, IChunk chunk){
        this.generatorRandom.setSeed(Util.scrambleSeed(chunk.getGridX(), chunk.getGridY(), world.getSeed()));

        for(int i = 0; i < 50; i++){
            int x = this.generatorRandom.nextInt(Constants.CHUNK_SIZE-WIDTH);

            int y = chunk.getLowestAirUpwardsInner(TileLayer.MAIN, x, 0, true);
            if(y > 0 && y < Constants.CHUNK_SIZE-HEIGHT){
                if(chunk.getStateInner(x, y-1).getTile().isFullTile()){
                    this.genStartHouse(chunk, x, y-1);

                    world.getOrCreateAdditionalData().addBoolean("start_house_created", true);
                    break;
                }
            }
        }
    }

    private void genStartHouse(IChunk chunk, int startX, int startY){
        this.generatorRandom.setSeed(Util.scrambleSeed(chunk.getX()+startX, chunk.getY()+startY, chunk.getSeed()));

        TileState board = GameContent.WOOD_BOARDS.getDefState();
        TileState oldBoard = board.prop(GameContent.WOOD_BOARDS.metaProp, 1);

        //Prepare area
        for(int x = 0; x < WIDTH; x++){
            for(int y = 0; y < HEIGHT; y++){
                for(TileLayer layer : TileLayer.getAllLayers()){
                    chunk.setStateInner(layer, startX+x, startY+y, GameContent.TILE_AIR.getDefState());
                }
            }

            for(int y = 0; y <= startY; y++){
                if(!chunk.getStateInner(startX+x, y).getTile().isFullTile()){
                    chunk.setStateInner(startX+x, y, chunk.getBiomeInner(startX+x, y).getFillerTile(chunk.getWorld(), chunk, startX+x, y));
                }
            }
        }

        //Set background
        for(int y = 0; y < HEIGHT; y++){
            for(int x = 1; x < WIDTH; x++){
                TileState tile;
                if(this.generatorRandom.nextFloat() >= 0.25F){
                    tile = this.generatorRandom.nextFloat() >= 0.75F ? board : oldBoard;
                }
                else{
                    tile = chunk.getBiomeInner(startX+x, startY+y).getFillerTile(chunk.getWorld(), chunk, startX+x, startY+y);
                }

                chunk.setStateInner(TileLayer.BACKGROUND, startX+x, startY+y, tile);

                if(y >= HEIGHT-1 && x >= 4){
                    break;
                }
            }
        }

        //Set walls
        int[][] wallPositions = new int[][]{
                {0, 1},
                {1, 0}, {1, 3},
                {2, 0}, {3, 0}, {4, 0}, {5, 0}, {6, 0}, {7, 0}, {8, 0},
                {8, 1}, {8, 2}
        };
        for(int[] pos : wallPositions){
            chunk.setStateInner(startX+pos[0], startY+pos[1], this.generatorRandom.nextFloat() >= 0.75F ? board : oldBoard);
        }

        //Set door
        TileState state = GameContent.TILE_WOOD_DOOR_OLD.getDefState();
        chunk.setStateInner(startX+1, startY+1, state.prop(StaticTileProps.TOP_HALF, false));
        chunk.setStateInner(startX+1, startY+2, state.prop(StaticTileProps.TOP_HALF, true));

        //Set dirt piles
        int[][] dirtPositions = new int[][]{
                {2, 1}, {2, 2},
                {3, 1},
                {6, 1}, {7, 1}
        };
        for(int[] pos : dirtPositions){
            chunk.setStateInner(startX+pos[0], startY+pos[1], chunk.getBiomeInner(startX+pos[0], startY+pos[1]).getFillerTile(chunk.getWorld(), chunk, startX+pos[0], startY+pos[1]));
        }

        //Set chest
        chunk.setStateInner(startX+5, startY+1, GameContent.TILE_CHEST.getDefState());

        //Fill chest
        TileEntityChest chest = chunk.getTileEntity(chunk.getX()+startX+5, chunk.getY()+startY+1, TileEntityChest.class);
        if(chest != null){
            IInventory inv = chest.getInventory();

            ItemInstance note = new ItemInstance(GameContent.ITEM_STAT_NOTE, 1, this.generatorRandom.nextInt(ItemStartNote.TEXT_VARIATIONS));
            inv.set(this.generatorRandom.nextInt(inv.getSlotAmount()), note);
        }

        //Goo
        chunk.setStateInner(TileLayer.LIQUIDS, startX+4, startY+1, GameContent.TILE_REMAINS_GOO.getDefState());
    }

    @Override
    public int getPriority(){
        return 0;
    }
}
