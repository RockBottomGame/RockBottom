package de.ellpeck.rockbottom.assets;

import org.newdawn.slick.Sound;

public class AssetSound implements IAsset<Sound>{

    private final Sound sound;

    public AssetSound(Sound sound){
        this.sound = sound;
    }

    @Override
    public Sound get(){
        return this.sound;
    }
}
