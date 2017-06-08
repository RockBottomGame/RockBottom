package de.ellpeck.rockbottom.api.world;

public enum TileLayer{
    MAIN("layer.main"),
    BACKGROUND("layer.background");

    public static final TileLayer[] LAYERS = values();

    public final String name;

    TileLayer(String name){
        this.name = name;
    }

    public TileLayer getOpposite(){
        return this == MAIN ? BACKGROUND : MAIN;
    }
}
