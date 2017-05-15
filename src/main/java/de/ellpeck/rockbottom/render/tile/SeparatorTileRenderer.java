package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.util.Pos2;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.tile.TileSeparator;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySeparator;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SeparatorTileRenderer extends MultiTileRenderer<TileSeparator>{

    public SeparatorTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, TileSeparator tile, int x, int y, float renderX, float renderY, Color filter){
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
