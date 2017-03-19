package de.ellpeck.game.render.item;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.item.Item;
import org.newdawn.slick.Graphics;

public class DefaultItemRenderer implements IItemRenderer{

    private final String texture;

    public DefaultItemRenderer(String texture){
        this.texture = "items."+texture;
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g, Item item, float x, float y, float scale){
        manager.getImage(this.texture).draw(x, y, 1F*scale, 1F*scale);
    }
}
