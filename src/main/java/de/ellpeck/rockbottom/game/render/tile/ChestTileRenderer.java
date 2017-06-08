package de.ellpeck.rockbottom.game.render.tile;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.world.tile.entity.TileEntityChest;
import de.ellpeck.rockbottom.api.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ChestTileRenderer extends DefaultTileRenderer{

    public ChestTileRenderer(){
        super("chest");
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        String tex = this.texture;

        TileEntityChest chest = world.getTileEntity(x, y, TileEntityChest.class);
        if(chest != null && chest.openCount > 0){
            tex += ".open";
        }

        manager.getImage(tex).draw(renderX, renderY, 1F, 1F, filter);
    }
}
