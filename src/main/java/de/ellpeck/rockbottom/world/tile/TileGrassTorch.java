package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.RockBottomAPI;

public class TileGrassTorch extends TileTorch{

    public TileGrassTorch(){
        super(RockBottomAPI.createInternalRes("torch_grass"));
    }

    @Override
    public double getTurnOffChance(){
        return 0.8;
    }

    @Override
    public int getMaxLight(){
        return 20;
    }
}
