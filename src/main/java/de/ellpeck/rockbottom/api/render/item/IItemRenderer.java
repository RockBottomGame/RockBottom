package de.ellpeck.rockbottom.api.render.item;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IItemRenderer<T extends Item>{

    void render(IGameInstance game, IAssetManager manager, Graphics g, T item, ItemInstance instance, float x, float y, float scale, Color filter);

    default void renderOnMouseCursor(IGameInstance game, IAssetManager manager, Graphics g, T item, ItemInstance instance, float x, float y, float scale, Color filter){

    }
}
