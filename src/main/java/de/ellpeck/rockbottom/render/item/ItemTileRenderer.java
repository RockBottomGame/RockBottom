package de.ellpeck.rockbottom.render.item;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.item.ItemInstance;
import de.ellpeck.rockbottom.item.ItemTile;
import de.ellpeck.rockbottom.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ItemTileRenderer implements IItemRenderer<ItemTile>{

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, ItemTile item, ItemInstance instance, float x, float y, float scale, Color filter){
        Tile tile = item.getTile();
        if(tile != null){
            ITileRenderer renderer = tile.getRenderer();
            if(renderer != null){
                renderer.renderItem(game, manager, g, tile, instance.getMeta(), x, y, scale, filter);
            }
        }
    }

    @Override
    public void renderOnMouseCursor(RockBottom game, AssetManager manager, Graphics g, ItemTile item, ItemInstance instance, float x, float y, float scale, Color filter){
        this.render(game, manager, g, item, instance, x+0.1F*scale, y, scale*0.75F, filter);
    }
}
