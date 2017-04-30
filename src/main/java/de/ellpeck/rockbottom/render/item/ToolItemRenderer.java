package de.ellpeck.rockbottom.render.item;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemInstance;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ToolItemRenderer extends DefaultItemRenderer{

    public ToolItemRenderer(String texture){
        super(texture);
    }

    @Override
    public void renderOnMouseCursor(RockBottom game, AssetManager manager, Graphics g, Item item, ItemInstance instance, float x, float y, float scale, Color filter){
        this.render(game, manager, g, item, instance, x, y, scale, filter);
    }
}
