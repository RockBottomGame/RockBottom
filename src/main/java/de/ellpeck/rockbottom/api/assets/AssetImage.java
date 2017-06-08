package de.ellpeck.rockbottom.api.assets;

import org.newdawn.slick.Image;

public class AssetImage implements IAsset<Image>{

    private final Image image;

    public AssetImage(Image image){
        this.image = image;
        this.image.setFilter(Image.FILTER_NEAREST);
    }

    @Override
    public Image get(){
        return this.image;
    }
}
