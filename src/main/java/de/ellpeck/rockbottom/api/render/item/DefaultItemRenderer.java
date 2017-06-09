package de.ellpeck.rockbottom.api.render.item;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class DefaultItemRenderer implements IItemRenderer{

    protected final IResourceName texture;

    public DefaultItemRenderer(IResourceName texture){
        this.texture = texture.addPrefix("items.");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, Item item, ItemInstance instance, float x, float y, float scale, Color filter){
        manager.getImage(this.texture).draw(x, y, 1F*scale, 1F*scale, filter);
    }
}
