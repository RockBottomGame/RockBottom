package de.ellpeck.game.world.gen.feature;

import de.ellpeck.game.Constants;
import de.ellpeck.game.ContentRegistry;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.gen.IWorldGenerator;
import de.ellpeck.game.world.tile.Tile;
import de.ellpeck.game.world.tile.TileDirt;
import de.ellpeck.game.world.tile.TileGrass;

import java.util.Random;

public class WorldGenTrees implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(World world, Chunk chunk, Random rand){
        return chunk.gridY == 0;
    }

    @Override
    public void generate(World world, Chunk chunk, Random rand){
        int randX = chunk.x+8+rand.nextInt(Constants.CHUNK_SIZE-16);
        int randY = world.getLowestAirUpwards(TileLayer.MAIN, randX, 0);

        if(randY > 0 && randY < 16){
            Tile tile = world.getTile(randX, randY-1);
            if(tile instanceof TileDirt || tile instanceof TileGrass){
                int height = rand.nextInt(6)+8;
                for(int y = 0; y <= height; y++){
                    world.setTile(randX, randY+y, ContentRegistry.TILE_LOG);
                }

                int branches = rand.nextInt(2)+1;
                for(int b = 0; b <= branches; b++){
                    this.makeBranch(world, randX, randY+rand.nextInt(height/2)+height/2, b%2 == 0, rand);
                }

                this.makeBranch(world, randX, randY+height, rand.nextBoolean(), rand);
            }
        }
    }

    private void makeBranch(World world, int startX, int startY, boolean left, Random rand){
        int yAdd = 0;

        int length = rand.nextInt(4)+3;
        int lengthAdd = rand.nextInt(2)+1;
        for(int l = 1; l <= length+lengthAdd; l++){
            int x = left ? startX-l : startX+l;

            if(l <= length){
                if(world.getTile(x, startY+yAdd).isAir()){
                    world.setTile(x, startY+yAdd, ContentRegistry.TILE_LOG);
                }
            }

            int leafAmountDown = rand.nextInt(3)+1;
            int leafAmountUp = rand.nextInt(3)+1;
            for(int lY = -leafAmountDown; lY <= leafAmountUp; lY++){
                if(world.getTile(x, startY+yAdd+lY).isAir()){
                    world.setTile(x, startY+yAdd+lY, ContentRegistry.TILE_LEAVES);
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
