package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.MultiTileRenderer;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileSmelter;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySmelter;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class SmelterTileRenderer extends MultiTileRenderer<TileSmelter>{

    private final IResourceName texActive;

    public SmelterTileRenderer(IResourceName texture, MultiTile tile){
        super(texture, tile);
        this.texActive = this.texture.addSuffix(".active");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, TileSmelter tile, int x, int y, TileLayer layer, float renderX, float renderY, float scale, Color[] light){
        if(tile.isMainPos(x, y, world.getMeta(x, y))){
            TileEntitySmelter tileEntity = world.getTileEntity(x, y, TileEntitySmelter.class);
            if(tileEntity != null && tileEntity.isActive()){
                manager.getTexture(this.texActive).drawWithLight(renderX, renderY, scale, scale, light);
                return;
            }
        }

        super.render(game, manager, g, world, tile, x, y, layer, renderX, renderY, scale, light);
    }
}
