package de.ellpeck.rockbottom.game.render.item;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.item.DefaultItemRenderer;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ToolItemRenderer extends DefaultItemRenderer{

    public ToolItemRenderer(IResourceName texture){
        super(texture);
    }

    @Override
    public void renderOnMouseCursor(IGameInstance game, IAssetManager manager, Graphics g, Item item, ItemInstance instance, float x, float y, float scale, Color filter){
        this.render(game, manager, g, item, instance, x, y, scale, filter);
    }
}
