package de.ellpeck.rockbottom.world.gen;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.INoiseGen;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldGenTrees implements IWorldGenerator{

    private INoiseGen treeNoise;

    @Override
    public void initWorld(IWorld world, Random rand){
        this.treeNoise = RockBottomAPI.getApiHandler().makeSimplexNoise(rand);
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk, Random rand){
        return true;
    }

    @Override
    public void generate(IWorld world, IChunk chunk, Random rand){
        int amount = Util.ceil(this.treeNoise.make2dNoise(chunk.getX()*200D, chunk.getY()*200D)*4D);

        Set<Pos2> alreadyGeneratedPositions = new HashSet<>();

        for(int i = 1; i <= amount; i++){
            Pos2 pos;

            int tries = 0;
            do{
                double mod = i*2000+tries*500;
                int x = Util.ceil(this.treeNoise.make2dNoise(chunk.getX()*200+mod, chunk.getY()*200+mod)*(double)(Constants.CHUNK_SIZE-6))+3;
                int y = chunk.getLowestAirUpwardsInner(TileLayer.MAIN, x, 0);
                pos = new Pos2(x, y);

                tries++;
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
        int height = 5+(int)(this.treeNoise.make2dNoise(chunk.getX()+x, chunk.getY()+y)*8D);
        while(y+height >= Constants.CHUNK_SIZE-4){
            height--;
        }

        int treeSize = Util.ceil(this.treeNoise.make2dNoise((chunk.getX()+x)*20, (chunk.getY()+y)*20)*((double)height/3D));
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
