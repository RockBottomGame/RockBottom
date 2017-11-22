package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.EntitySand;

public class TileSand extends TileBasic{

    public TileSand(){
        super(RockBottomAPI.createInternalRes("sand"));
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer){
        tryFall(world, x, y, layer);
    }

    @Override
    public void onAdded(IWorld world, int x, int y, TileLayer layer){
        tryFall(world, x, y, layer);
    }

    private static void tryFall(IWorld world, int x, int y, TileLayer layer){
        if(!world.isClient() && layer == TileLayer.MAIN){
            if(world.isPosLoaded(x, y-1)){
                TileState below = world.getState(x, y-1);
                if(below.getTile().canReplace(world, x, y-1, layer)){
                    EntitySand sand = new EntitySand(world);
                    sand.setPos(x+0.5, y+0.5);
                    world.addEntity(sand);

                    world.setState(x, y, GameContent.TILE_AIR.getDefState());
                }
            }
        }
    }
}
