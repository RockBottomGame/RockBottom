package de.ellpeck.game.assets.local;

import de.ellpeck.game.assets.IAsset;

public class AssetLocale implements IAsset<Locale>{

    private final Locale locale;

    public AssetLocale(Locale locale){
        this.locale = locale;
    }

    @Override
    public Locale get(){
        return this.locale;
    }
}
