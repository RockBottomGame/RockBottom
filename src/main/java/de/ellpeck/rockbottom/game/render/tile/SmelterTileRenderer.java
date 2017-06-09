package de.ellpeck.rockbottom.game.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.MultiTileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.world.tile.TileSmelter;
import de.ellpeck.rockbottom.game.world.tile.entity.TileEntitySmelter;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SmelterTileRenderer extends MultiTileRenderer<TileSmelter>{

    private final IResourceName texActive;

    public SmelterTileRenderer(IResourceName texture, MultiTile tile){
        super(texture, tile);
        this.texActive = this.texture.addSuffix(".active");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, TileSmelter tile, int x, int y, float renderX, float renderY, Color filter){
        if(tile.isMainPos(x, y, world.getMeta(x, y))){
            TileEntitySmelter tileEntity = world.getTileEntity(x, y, TileEntitySmelter.class);
            if(tileEntity != null && tileEntity.isActive()){
                manager.getImage(this.texActive).draw(renderX, renderY, 1F, 1F, filter);
                return;
            }
        }

        super.render(game, manager, g, world, tile, x, y, renderX, renderY, filter);
    }
}
