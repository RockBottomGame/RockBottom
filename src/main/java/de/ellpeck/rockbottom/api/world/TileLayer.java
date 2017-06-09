package de.ellpeck.rockbottom.api.world;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;

public enum TileLayer{
    MAIN(RockBottomAPI.createRes(null, "layer.main")),
    BACKGROUND(RockBottomAPI.createRes(null, "layer.background"));

    public static final TileLayer[] LAYERS = values();

    public final IResourceName name;

    TileLayer(IResourceName name){
        this.name = name;
    }

    public TileLayer getOpposite(){
        return this == MAIN ? BACKGROUND : MAIN;
    }
}
