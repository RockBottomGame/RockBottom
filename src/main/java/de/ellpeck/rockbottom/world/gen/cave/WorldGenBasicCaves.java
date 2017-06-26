package de.ellpeck.rockbottom.world.gen.cave;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.util.Direction;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.apiimpl.SimplexNoise;

import java.util.Arrays;
import java.util.Random;

public class WorldGenBasicCaves implements IWorldGenerator{

    private INoiseGen noiseGen;

    @Override
    public void initWorld(IWorld world, Random rand){
        this.noiseGen = new SimplexNoise(rand);
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        /*double[][] noise = new double[Constants.CHUNK_SIZE][Constants.CHUNK_SIZE];

        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                noise[x][y] = this.noiseGen.make2dNoise(chunk.getX()+x, chunk.getY()+y);
            }
        }

        double[][] output = noise.clone();

        if(chunk.getX() == 0 && chunk.getY() == 0){
            System.out.println("INPUT: "+Arrays.deepToString(noise));
        }

        int iterations = 2;
        for(int i = 1; i <= iterations; i++){
            for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                    double sum = 0;

                    for(Direction dir : Direction.SURROUNDING){
                        int offX = x+dir.x;
                        int offY = y+dir.y;

                        if(offX >= 0 && offY >= 0 && offX < Constants.CHUNK_SIZE && offY < Constants.CHUNK_SIZE){
                            sum += noise[offX][offY];
                        }
                        else{
                            sum += this.noiseGen.make2dNoise(chunk.getX()+offX, chunk.getY()+offY);
                        }
                    }

                    if(sum >= 3){
                       // output[x][y] = Math.max(-1, Math.min(1, noise[x][y]+0.5));
                    }
                    else if(sum <= 1){
                        output[x][y] = Math.max(-1, Math.min(1, noise[x][y]-0.5));
                    }
                }
            }

            if(i < iterations){
                noise = output.clone();
            }

            if(chunk.getX() == 0 && chunk.getY() == 0){
                System.out.println("ITERATION "+i+": "+Arrays.deepToString(output));
            }
        }

        for(int x = 0; x < Constants.CHUNK_SIZE; x++){
            for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                if(output[x][y] <= 0){
                    chunk.setTileInner(x, y, GameContent.TILE_AIR);
                }
            }
        }*/
    }

    @Override
    public int getPriority(){
        return 5000;
    }
}
