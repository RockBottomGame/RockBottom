package de.ellpeck.rockbottom.api.render.item;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ItemTile;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ItemTileRenderer implements IItemRenderer<ItemTile>{

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, ItemTile item, ItemInstance instance, float x, float y, float scale, Color filter){
        Tile tile = item.getTile();
        if(tile != null){
            ITileRenderer renderer = tile.getRenderer();
            if(renderer != null){
                renderer.renderItem(game, manager, g, tile, instance.getMeta(), x, y, scale, filter);
            }
        }
    }

    @Override
    public void renderOnMouseCursor(IGameInstance game, IAssetManager manager, Graphics g, ItemTile item, ItemInstance instance, float x, float y, float scale, Color filter){
        this.render(game, manager, g, item, instance, x+0.1F*scale, y, scale*0.75F, filter);
    }
}
