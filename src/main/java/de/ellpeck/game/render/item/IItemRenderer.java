package de.ellpeck.game.render.item;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.gui.Gui;
import de.ellpeck.game.item.Item;
import de.ellpeck.game.item.ItemInstance;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IItemRenderer<T extends Item>{

    void render(Game game, AssetManager manager, Graphics g, T item, float x, float y, float scale, Color filter);

    static void renderSlotInGui(Game game, AssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale){
        Gui.drawScaledImage(g, manager.getImage("gui.slot"), x, y, scale, Gui.GUI_COLOR);

        if(slot != null){
            IItemRenderer.renderItemInGui(game, manager, g, slot, x+3F*scale, y+3F*scale, scale);
        }
    }

    static void renderItemInGui(Game game, AssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale){
        Item item = slot.getItem();
        IItemRenderer renderer = item.getRenderer();
        if(renderer != null){
            renderer.render(game, manager, g, item, x, y, 12F*scale, Color.white);
        }

        manager.getFont().drawStringFromRight(x+15F*scale, y+9F*scale, String.valueOf(slot.getAmount()), 0.25F*scale);
    }
}
