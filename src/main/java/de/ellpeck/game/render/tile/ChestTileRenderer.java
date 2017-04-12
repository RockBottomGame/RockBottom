package de.ellpeck.game.render.tile;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.tile.Tile;
import de.ellpeck.game.world.tile.entity.TileEntity;
import de.ellpeck.game.world.tile.entity.TileEntityChest;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ChestTileRenderer extends DefaultTileRenderer{

    public ChestTileRenderer(){
        super("chest");
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        String tex = this.texture;

        TileEntity tileEntity = world.getTileEntity(x, y);
        if(tileEntity instanceof TileEntityChest && ((TileEntityChest)tileEntity).openCount > 0){
            tex += ".open";
        }

        manager.getImage(tex).draw(renderX, renderY, 1F, 1F, filter);
    }
}
