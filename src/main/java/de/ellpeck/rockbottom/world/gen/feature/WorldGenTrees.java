package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldGenTrees implements IWorldGenerator{

    private final Random treeRandom = new Random();

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk){
        this.treeRandom.setSeed(Util.scrambleSeed(chunk.getX(), chunk.getY(), world.getSeed()));
        int amount = Util.ceil(this.treeRandom.nextDouble()*4D);

        Set<Pos2> alreadyGeneratedPositions = new HashSet<>();

        amount:
        for(int i = 1; i <= amount; i++){
            Pos2 pos;

            int tries = 0;
            do{
                int mod = i*2000+tries*500;
                this.treeRandom.setSeed(Util.scrambleSeed(chunk.getX()+mod, chunk.getY()+mod, world.getSeed()));

                int x = Util.ceil(this.treeRandom.nextDouble()*(double)(Constants.CHUNK_SIZE-6))+3;
                int y = chunk.getLowestAirUpwardsInner(TileLayer.MAIN, x, 0, true);
                pos = new Pos2(x, y);

                tries++;

                if(tries > 20){
                    continue amount;
                }
            }
            while(this.alreadyHasTree(alreadyGeneratedPositions, pos));

            if(pos.getY() >= 0 && chunk.getBiomeInner(pos.getX(), pos.getY()).canTreeGrow(world, chunk, pos.getX(), pos.getY())){
                alreadyGeneratedPositions.add(pos);

                this.makeTree(chunk, pos.getX(), pos.getY());
            }
        }
    }

    private boolean alreadyHasTree(Set<Pos2> list, Pos2 pos){
        for(Pos2 already : list){
            if(Util.distanceSq(already.getX(), 0, pos.getX(), 0) <= 25){
                return true;
            }
        }
        return false;
    }

    private void makeTree(IChunk chunk, int x, int y){
        this.treeRandom.setSeed(Util.scrambleSeed(chunk.getX()+x, chunk.getY()+y, chunk.getSeed()));
        int height = 5+(int)(this.treeRandom.nextDouble()*8D);
        while(y+height >= Constants.CHUNK_SIZE-4){
            height--;
        }

        int treeSize = Util.ceil(this.treeRandom.nextDouble()*((double)height/3D));
        while(x-treeSize < 0 || x+treeSize >= Constants.CHUNK_SIZE-1){
            treeSize--;
        }

        boolean isDoubleTree = treeSize >= 3;
        int treeSizeMinus = -treeSize;
        int treeSizePlus = isDoubleTree ? treeSize+1 : treeSize;

        for(int i = 0; i < height; i++){
            chunk.setStateInner(x, y+i, GameContent.TILE_LOG.getDefState());

            if(isDoubleTree){
                if(!chunk.getStateInner(x+1, y+i).getTile().isFullTile()){
                    chunk.setStateInner(x+1, y+i, GameContent.TILE_LOG.getDefState());
                }
            }
        }

        if(isDoubleTree){
            for(int i = y; i > 0; i--){
                if(!chunk.getStateInner(x+1, i).getTile().isFullTile()){
                    chunk.setStateInner(x+1, i, GameContent.TILE_LOG.getDefState());
                    break;
                }
            }
        }

        for(int subX = treeSizeMinus; subX <= treeSizePlus; subX++){
            for(int subY = treeSizeMinus-1; subY <= treeSizePlus+1; subY++){
                if((subX > treeSizeMinus && subX < treeSizePlus) || (subY > treeSizeMinus-1 && subY < treeSizePlus+1)){
                    int theX = x+subX;
                    int theY = y+subY+height-treeSize;

                    if(!chunk.getStateInner(theX, theY).getTile().isFullTile()){
                        chunk.setStateInner(theX, theY, GameContent.TILE_LEAVES.getDefState());
                    }
                }
            }
        }
    }

    @Override
    public int getPriority(){
        return -90;
    }
}
