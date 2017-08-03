package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileStamper;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class StamperTileRenderer extends DefaultTileRenderer<TileStamper>{

    private final IResourceName texItem;
    private final IResourceName texDown;

    public StamperTileRenderer(IResourceName texture){
        super(texture);
        this.texItem = this.texture.addSuffix(".item");
        this.texDown = this.texture.addSuffix(".down");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, TileStamper tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, Color[] light){
        if(state.get(TileStamper.DOWN_PROP)){
            manager.getTexture(this.texDown).drawWithLight(renderX, renderY, scale, scale, light);
        }
        else{
            super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
    }

    @Override
    public void renderItem(IGameInstance game, IAssetManager manager, Graphics g, TileStamper tile, ItemInstance instance, float x, float y, float scale, Color filter){
        manager.getTexture(this.texItem).draw(x, y, scale, scale, filter);
    }
}
