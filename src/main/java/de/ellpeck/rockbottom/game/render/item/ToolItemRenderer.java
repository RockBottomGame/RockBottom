package de.ellpeck.rockbottom.game.render.item;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ToolItemRenderer extends DefaultItemRenderer{

    public ToolItemRenderer(String texture){
        super(texture);
    }

    @Override
    public void renderOnMouseCursor(IGameInstance game, AssetManager manager, Graphics g, Item item, ItemInstance instance, float x, float y, float scale, Color filter){
        this.render(game, manager, g, item, instance, x, y, scale, filter);
    }
}
