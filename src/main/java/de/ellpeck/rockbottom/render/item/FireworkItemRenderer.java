package de.ellpeck.rockbottom.render.item;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.item.ItemFirework;

public class FireworkItemRenderer extends DefaultItemRenderer<ItemFirework>{

    public FireworkItemRenderer(IResourceName texture){
        super(texture);
    }

    @Override
    public void renderOnMouseCursor(IGameInstance game, IAssetManager manager, IGraphics g, ItemFirework item, ItemInstance instance, float x, float y, float scale, int filter, boolean isInPlayerRange){
        if(isInPlayerRange){
            this.render(game, manager, g, item, instance, x, y, scale, filter);
        }
    }
}
