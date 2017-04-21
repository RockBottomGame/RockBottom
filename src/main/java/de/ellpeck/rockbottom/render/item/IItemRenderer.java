package de.ellpeck.rockbottom.render.item;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.gui.Gui;
import de.ellpeck.rockbottom.item.Item;
import de.ellpeck.rockbottom.item.ItemInstance;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IItemRenderer<T extends Item>{

    void render(RockBottom game, AssetManager manager, Graphics g, T item, float x, float y, float scale, Color filter);

    static void renderSlotInGui(RockBottom game, AssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale){
        Gui.drawScaledImage(g, manager.getImage("gui.slot"), x, y, scale, Gui.GUI_COLOR);

        if(slot != null){
            renderItemInGui(game, manager, g, slot, x+3F*scale, y+3F*scale, scale);
        }
    }

    static void renderItemInGui(RockBottom game, AssetManager manager, Graphics g, ItemInstance slot, float x, float y, float scale){
        Item item = slot.getItem();
        IItemRenderer renderer = item.getRenderer();
        if(renderer != null){
            renderer.render(game, manager, g, item, x, y, 12F*scale, Color.white);
        }

        manager.getFont().drawStringFromRight(x+15F*scale, y+9F*scale, String.valueOf(slot.getAmount()), 0.25F*scale);
    }
}
