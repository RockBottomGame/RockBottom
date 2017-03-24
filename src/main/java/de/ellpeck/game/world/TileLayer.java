package de.ellpeck.game.world;

public enum TileLayer{
    MAIN,
    BACKGROUND;

    public static final TileLayer[] LAYERS = values();

    public TileLayer getOpposite(){
        return this == MAIN ? BACKGROUND : MAIN;
    }
}
