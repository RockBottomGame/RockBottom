package de.ellpeck.rockbottom.world.gen.feature;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.inventory.Inventory;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.IStructure;
import de.ellpeck.rockbottom.api.world.gen.IWorldGenerator;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.item.ItemStartNote;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenStartHut implements IWorldGenerator{

    private static final ResourceName HOUSE_CREATED = ResourceName.intern("start_house_created");

    private final Random generatorRandom = new Random();
    private int chunkX;

    @Override
    public void initWorld(IWorld world){
        this.generatorRandom.setSeed(Util.scrambleSeed(7834, world.getSeed()));
        this.chunkX = this.generatorRandom.nextInt(2)-1;
    }

    @Override
    public boolean shouldGenerate(IWorld world, IChunk chunk){
        return world.isStoryMode() && chunk.getGridX() == this.chunkX && (!world.hasAdditionalData() || !world.getAdditionalData().getBoolean(HOUSE_CREATED));
    }

    @Override
    public void generate(IWorld world, IChunk chunk){
        IStructure structure = IStructure.forName(ResourceName.intern("start_hut"));
        if(structure != null){
            this.generatorRandom.setSeed(Util.scrambleSeed(chunk.getGridX(), chunk.getGridY(), world.getSeed()));

            for(int i = 0; i < 50; i++){
                int x = this.generatorRandom.nextInt(Constants.CHUNK_SIZE-structure.getWidth());

                int y = chunk.getHeightInner(TileLayer.MAIN, x);
                if(y > 0 && y < Constants.CHUNK_SIZE-structure.getHeight()){
                    if(chunk.getStateInner(x, y-1).getTile().isFullTile()){
                        this.genStartHouse(structure, chunk, x, y-1);

                        world.getOrCreateAdditionalData().addBoolean(HOUSE_CREATED, true);
                        chunk.setDirty();

                        break;
                    }
                }
            }
        }
    }

    private void genStartHouse(IStructure structure, IChunk chunk, int startX, int startY){
        this.generatorRandom.setSeed(Util.scrambleSeed(chunk.getX()+startX, chunk.getY()+startY, chunk.getSeed()));

        //Prepare area
        for(int x = 0; x < structure.getWidth(); x++){
            for(int y = 0; y < structure.getHeight(); y++){
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

        for(TileLayer layer : structure.getInvolvedLayers()){
            for(int x = 0; x < structure.getWidth(); x++){
                for(int y = 0; y < structure.getHeight(); y++){
                    TileState state = structure.getTile(layer, x, y);
                    if(state.getTile() == GameContent.TILE_SOIL){
                        state = chunk.getBiomeInner(startX+x, startY+y).getFillerTile(chunk.getWorld(), chunk, startX+x, startY);
                    }
                    chunk.setStateInner(layer, startX+x, startY+y, state);
                }
            }
        }

        //Fill chest
        TileEntityChest chest = chunk.getTileEntity(chunk.getX()+startX+5, chunk.getY()+startY+1, TileEntityChest.class);
        if(chest != null){
            List<ItemInstance> items = new ArrayList<>();

            ItemInstance note = new ItemInstance(GameContent.ITEM_STAT_NOTE, 1, this.generatorRandom.nextInt(ItemStartNote.TEXT_VARIATIONS));
            items.add(note);

            for(int i = this.generatorRandom.nextInt(5)+5; i >= 0; i--){
                ItemInstance torch = new ItemInstance(GameContent.TILE_GRASS_TORCH);
                items.add(torch);
            }

            for(int i = this.generatorRandom.nextInt(5)+3; i >= 0; i--){
                ItemInstance twig = new ItemInstance(GameContent.ITEM_TWIG);
                items.add(twig);
            }

            chest.getTileInventory().fillRandomly(this.generatorRandom, items);
        }
    }

    @Override
    public int getPriority(){
        return 0;
    }
}
