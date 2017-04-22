package de.ellpeck.rockbottom.world.gen.ore;

import de.ellpeck.rockbottom.Constants;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.Chunk;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.world.tile.Tile;

import java.util.Random;

public abstract class WorldGenOre implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(World world, Chunk chunk, Random rand){
        return chunk.gridY <= this.getHighestGridPos() && chunk.gridY >= this.getLowestGridPos();
    }

    @Override
    public void generate(World world, Chunk chunk, Random rand){
        int amount = rand.nextInt(this.getMaxAmount());
        if(amount > 0){
            int radX = this.getClusterRadiusX();
            int radY = this.getClusterRadiusY();
            int radXHalf = Util.ceil((double)radX/2);
            int radYHalf = Util.ceil((double)radY/2);

            for(int i = 0; i < amount; i++){
                int startX = chunk.x+radX+rand.nextInt(Constants.CHUNK_SIZE-radX*2);
                int startY = chunk.y+radY+rand.nextInt(Constants.CHUNK_SIZE-radY*2);

                int thisRadX = rand.nextInt(radXHalf)+radXHalf;
                int thisRadY = rand.nextInt(radYHalf)+radYHalf;

                for(int x = -thisRadX; x <= thisRadX; x++){
                    for(int y = -thisRadY; y <= thisRadY; y++){
                        if(rand.nextInt(thisRadX) == x || rand.nextInt(thisRadY) == y){
                            world.setTile(startX+x, startY+y, this.getOreTile());

                            int meta = this.getOreMeta();
                            if(meta != 0){
                                world.setMeta(startX+x, startY+y, meta);
                            }
                        }
                    }
                }
            }
        }
    }

    public abstract int getHighestGridPos();

    public int getLowestGridPos(){
        return -Integer.MAX_VALUE;
    }

    public abstract int getMaxAmount();

    public abstract int getClusterRadiusX();

    public abstract int getClusterRadiusY();

    public abstract Tile getOreTile();

    public abstract int getOreMeta();
}
