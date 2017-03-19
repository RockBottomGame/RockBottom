package de.ellpeck.game.render.item;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.item.ItemTile;
import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.world.tile.Tile;
import org.newdawn.slick.Graphics;

public class ItemTileRenderer implements IItemRenderer<ItemTile>{

    @Override
    public void render(Game game, AssetManager manager, Graphics g, ItemTile item, float x, float y, float scale){
        Tile tile = item.getTile();
        ITileRenderer renderer = tile.getRenderer();
        if(renderer != null){
            renderer.renderItem(game, manager, g, tile, x, y, scale);
        }
    }
}
