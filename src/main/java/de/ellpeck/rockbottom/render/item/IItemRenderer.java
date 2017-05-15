package de.ellpeck.rockbottom.render.item;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemInstance;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IItemRenderer<T extends Item>{

    void render(RockBottom game, AssetManager manager, Graphics g, T item, ItemInstance instance, float x, float y, float scale, Color filter);

    default void renderOnMouseCursor(RockBottom game, AssetManager manager, Graphics g, T item, ItemInstance instance, float x, float y, float scale, Color filter){

    }
}
