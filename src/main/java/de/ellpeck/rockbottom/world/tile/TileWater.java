package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.tile.TileLiquid;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.List;

public class TileWater extends TileLiquid{

    public TileWater(){
        super(RockBottomAPI.createInternalRes("water"));
    }

    @Override
    public int getLevels(){
        return 12;
    }

    @Override
    public boolean doesFlow(){
        return true;
    }

    @Override
    public int getFlowSpeed(){
        return 5;
    }

    @Override
    public void onIntersectWithEntity(IWorld world, int x, int y, TileLayer layer, TileState state, BoundBox entityBox, BoundBox entityBoxMotion, List<BoundBox> tileBoxes, Entity entity){
        entity.motionX *= 0.95;
    }
}
