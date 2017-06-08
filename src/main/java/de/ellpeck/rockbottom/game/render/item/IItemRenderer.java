package de.ellpeck.rockbottom.game.render.item;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.game.item.ItemInstance;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IItemRenderer<T extends Item>{

    void render(RockBottom game, AssetManager manager, Graphics g, T item, ItemInstance instance, float x, float y, float scale, Color filter);

    default void renderOnMouseCursor(RockBottom game, AssetManager manager, Graphics g, T item, ItemInstance instance, float x, float y, float scale, Color filter){

    }
}
