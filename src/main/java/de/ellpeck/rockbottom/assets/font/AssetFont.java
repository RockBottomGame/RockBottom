package de.ellpeck.rockbottom.assets.font;

import de.ellpeck.rockbottom.assets.IAsset;

public class AssetFont implements IAsset<Font>{

    private final Font font;

    public AssetFont(Font font){
        this.font = font;
    }

    @Override
    public Font get(){
        return this.font;
    }
}