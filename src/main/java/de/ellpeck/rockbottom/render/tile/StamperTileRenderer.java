package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileStamper;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class StamperTileRenderer extends DefaultTileRenderer<TileStamper>{

    private final IResourceName texDown;

    public StamperTileRenderer(IResourceName texture){
        super(texture);
        this.texDown = this.texture.addSuffix(".down");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, TileStamper tile, int x, int y, TileLayer layer, float renderX, float renderY, float scale, Color[] light){
        if(world.getMeta(x, y) == 1){
            manager.getTexture(this.texDown).drawWithLight(renderX, renderY, scale, scale, light);
        }
        else{
            super.render(game, manager, g, world, tile, x, y, layer, renderX, renderY, scale, light);
        }
    }
}
