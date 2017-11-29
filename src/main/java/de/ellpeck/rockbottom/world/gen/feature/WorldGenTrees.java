package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.gen.feature.trees.TreeDesigns;

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

                int x = Util.ceil(this.treeRandom.nextDouble()*(double)(Constants.CHUNK_SIZE-8))+4;
                int y = chunk.getLowestAirUpwardsInner(TileLayer.MAIN, x, 1, true);
                pos = new Pos2(x, y);

                tries++;

                if(tries > 20){
                    continue amount;
                }
            }
            while(this.alreadyHasTree(alreadyGeneratedPositions, pos));

            if(pos.getY() >= 0 && chunk.getBiomeInner(pos.getX(), pos.getY()).canTreeGrow(world, chunk, pos.getX(), pos.getY())){
                alreadyGeneratedPositions.add(pos);

                this.makeTree(chunk, world, pos.getX(), pos.getY());
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

    private void makeTree(IChunk chunk, IWorld world, int x, int y){
        this.treeRandom.setSeed(Util.scrambleSeed(chunk.getX()+x, chunk.getY()+y, chunk.getSeed()));

        String[] variant = TreeDesigns.DESIGNS[this.treeRandom.nextInt(TreeDesigns.DESIGNS.length)];
        for(int subY = 0; subY < variant.length; subY++){
            String line = variant[subY];
            int length = line.length();

            for(int subX = 0; subX < length; subX++){
                char c = line.charAt(subX);
                if(c != ' '){
                    TileState state = TreeDesigns.STATE_MAP.get(c);
                    if(state != null){
                        int theX = x+subX-length/2;
                        int innerY = (variant.length-1-subY);
                        int theY = y+innerY;

                        if(chunk.getStateInner(theX, theY).getTile().canReplace(world, theX, theY, TileLayer.MAIN)){
                            if(innerY > 0 || !chunk.getStateInner(theX, theY-1).getTile().canReplace(world, theX, theY-1, TileLayer.MAIN)){
                                chunk.setStateInner(theX, theY, state);
                            }
                        }
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
