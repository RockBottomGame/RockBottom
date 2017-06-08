package de.ellpeck.rockbottom.game.render.tile;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.world.tile.TileSmelter;
import de.ellpeck.rockbottom.game.world.tile.entity.TileEntitySmelter;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SmelterTileRenderer extends MultiTileRenderer<TileSmelter>{

    public SmelterTileRenderer(String texture){
        super(texture);
    }

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, TileSmelter tile, int x, int y, float renderX, float renderY, Color filter){
        if(tile.isMainPos(x, y, world.getMeta(x, y))){
            TileEntitySmelter tileEntity = world.getTileEntity(x, y, TileEntitySmelter.class);
            if(tileEntity != null && tileEntity.isActive()){
                manager.getImage(this.texture+".active").draw(renderX, renderY, 1F, 1F, filter);
                return;
            }
        }

        super.render(game, manager, g, world, tile, x, y, renderX, renderY, filter);
    }
}
