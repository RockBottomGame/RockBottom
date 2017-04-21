package de.ellpeck.game.world.gen.ore;

import de.ellpeck.game.Constants;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.gen.IWorldGenerator;
import de.ellpeck.game.world.tile.Tile;

import java.util.Random;

public abstract class WorldGenOre implements IWorldGenerator{

    @Override
    public boolean shouldGenerate(World world, Chunk chunk, Random rand){
        return chunk.gridY <= this.getHighestGridPos();
    }

    @Override
    public void generate(World world, Chunk chunk, Random rand){
        int amount = rand.nextInt(this.getMaxAmount())+1;
        int radX = this.getClusterRadiusX();
        int radY = this.getClusterRadiusY();

        for(int i = 0; i < amount; i++){
            int startX = chunk.x+radX+rand.nextInt(Constants.CHUNK_SIZE-radX*2);
            int startY = chunk.y+radY+rand.nextInt(Constants.CHUNK_SIZE-radY*2);

            int thisRadX = rand.nextInt(radX/2)+radX/2;
            int thisRadY = rand.nextInt(radY/2)+radY/2;

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

    public abstract int getHighestGridPos();

    public abstract int getMaxAmount();

    public abstract int getClusterRadiusX();

    public abstract int getClusterRadiusY();

    public abstract Tile getOreTile();

    public abstract int getOreMeta();
}
