package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.world.tile.TileDirt;
import de.ellpeck.rockbottom.world.tile.TileGrass;

import java.util.Random;

public class WorldGenTrees implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return chunk.getGridY() == 0;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        int randX = chunk.getX()+8+rand.nextInt(Constants.CHUNK_SIZE-16);
        int randY = world.getLowestAirUpwards(TileLayer.MAIN, randX, 0);

        if(randY > 0 && randY < 16){
            this.generateAt(world, randX, randY, rand);
        }
    }

    public void generateAt(IWorld world, int x, int y, Random rand){
        Tile tile = world.getState(x, y-1).getTile();
        if(tile instanceof TileDirt || tile instanceof TileGrass){
            int height = rand.nextInt(6)+8;

            for(int h = 0; h <= height; h++){
                if(!world.getState(x, y+h).getTile().isAir()){
                    return;
                }
            }

            for(int h = 0; h <= height; h++){
                world.setState(x, y+h, GameContent.TILE_LOG.getDefState());
            }

            int branches = rand.nextInt(2)+1;
            for(int b = 0; b <= branches; b++){
                this.makeBranch(world, x, y+rand.nextInt(height/2)+height/2, b%2 == 0, rand);
            }

            this.makeBranch(world, x, y+height, rand.nextBoolean(), rand);
        }
    }

    private void makeBranch(IWorld world, int startX, int startY, boolean left, Random rand){
        int yAdd = 0;

        int length = rand.nextInt(4)+3;
        int lengthAdd = rand.nextInt(2)+1;
        for(int l = 1; l <= length+lengthAdd; l++){
            int x = left ? startX-l : startX+l;

            if(l <= length){
                if(world.getState(x, startY+yAdd).getTile().isAir()){
                    world.setState(x, startY+yAdd, GameContent.TILE_LOG.getDefState());
                }
            }

            int leafAmountDown = rand.nextInt(3)+1;
            int leafAmountUp = rand.nextInt(3)+1;
            for(int lY = -leafAmountDown; lY <= leafAmountUp; lY++){
                if(world.getState(x, startY+yAdd+lY).getTile().isAir()){
                    world.setState(x, startY+yAdd+lY, GameContent.TILE_LEAVES.getDefState());
                }
            }

            if(rand.nextBoolean()){
                yAdd++;
            }
        }
    }

    @Override
    public int getPriority(){
        return 200;
    }
}
