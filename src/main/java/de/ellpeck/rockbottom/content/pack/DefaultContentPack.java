package de.ellpeck.rockbottom.content.pack;

import de.ellpeck.rockbottom.api.content.pack.ContentPack;

public class DefaultContentPack extends ContentPack{

    public DefaultContentPack(){
        super(DEFAULT_PACK_ID, "Default", "~", new String[0], "The default content of the game and all installed mods");
    }
}
