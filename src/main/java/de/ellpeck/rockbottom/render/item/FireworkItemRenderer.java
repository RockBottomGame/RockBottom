package de.ellpeck.rockbottom.render.item;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.item.FireworkItem;

public class FireworkItemRenderer extends DefaultItemRenderer<FireworkItem> {

    public FireworkItemRenderer(ResourceName texture) {
        super(texture);
    }

    @Override
    public void renderOnMouseCursor(IGameInstance game, IAssetManager manager, IRenderer renderer, FireworkItem item, ItemInstance instance, float x, float y, float scale, int filter, boolean isInPlayerRange) {
        if (isInPlayerRange) {
            this.render(game, manager, renderer, item, instance, x, y, scale, filter);
        }
    }
}
