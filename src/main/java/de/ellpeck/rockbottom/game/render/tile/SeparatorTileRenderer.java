package de.ellpeck.rockbottom.game.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.MultiTileRenderer;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.world.tile.TileSeparator;
import de.ellpeck.rockbottom.game.world.tile.entity.TileEntitySeparator;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SeparatorTileRenderer extends MultiTileRenderer<TileSeparator>{

    public SeparatorTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, TileSeparator tile, int x, int y, float renderX, float renderY, Color filter){
        int meta = world.getMeta(x, y);

        Pos2 innerCoord = tile.getInnerCoord(meta);
        String tex = this.texture;

        Pos2 mainPos = tile.getMainPos(x, y, meta);
        TileEntitySeparator tileEntity = world.getTileEntity(mainPos.getX(), mainPos.getY(), TileEntitySeparator.class);
        if(tileEntity != null && tileEntity.isActive()){
            tex += ".active";
        }

        manager.getImage(tex+"."+innerCoord.getX()+"."+innerCoord.getY()).draw(renderX, renderY, 1F, 1F, filter);
    }
}
