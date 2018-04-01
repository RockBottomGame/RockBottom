package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IStructure;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenTrees implements IWorldGenerator{

    private final Random treeRandom = new Random();
    private List<IStructure> treeDesigns;
    private int widestTree;

    @Override
    public void initWorld(IWorld world){
        this.treeDesigns = IStructure.forNamePart("grassland_tree");
        for(IStructure structure : this.treeDesigns){
            if(this.widestTree < structure.getWidth()){
                this.widestTree = structure.getWidth();
            }
        }
    }

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

                int x = Util.ceil(this.treeRandom.nextDouble()*(double)(Constants.CHUNK_SIZE-(this.widestTree+1))+this.widestTree/2D);
                int y = chunk.getHeightInner(TileLayer.MAIN, x);
                pos = new Pos2(x, y);

                tries++;

                if(tries > 20){
                    continue amount;
                }
            }
            while(this.alreadyHasTree(alreadyGeneratedPositions, pos));

            if(pos.getY() < Constants.CHUNK_SIZE && chunk.getBiomeInner(pos.getX(), pos.getY()).canTreeGrow(world, chunk, pos.getX(), pos.getY())){
                alreadyGeneratedPositions.add(pos);

                int x = chunk.getX()+pos.getX();
                int y = chunk.getY()+pos.getY();
                if(this.makeTree(world, x, y, true)){
                    this.makeTree(world, x, y, false);
                }
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

    public boolean makeTree(IWorld world, int x, int y, boolean simulate){
        this.treeRandom.setSeed(Util.scrambleSeed(x, y, world.getSeed()));

        IStructure structure = this.treeDesigns.get(this.treeRandom.nextInt(this.treeDesigns.size()));
        for(int subY = 0; subY < structure.getHeight(); subY++){
            for(int subX = 0; subX < structure.getWidth(); subX++){
                TileState state = structure.getTile(subX, subY);
                if(!state.getTile().isAir()){
                    int theX = x+subX-structure.getWidth()/2;
                    int theY = y+subY;

                    if(((theX == x && theY == y) || world.getState(theX, theY).getTile().canReplace(world, theX, theY, TileLayer.MAIN)) && (subY > 0 || !world.getState(theX, theY-1).getTile().canReplace(world, theX, theY-1, TileLayer.MAIN))){
                        if(!simulate){
                            world.setState(theX, theY, state);
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int getPriority(){
        return -90;
    }
}
