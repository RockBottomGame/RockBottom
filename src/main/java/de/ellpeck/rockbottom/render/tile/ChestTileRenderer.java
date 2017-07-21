package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityChest;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ChestTileRenderer extends DefaultTileRenderer{

    private final IResourceName texOpen;

    public ChestTileRenderer(IResourceName name){
        super(name);
        this.texOpen = this.texture.addSuffix(".open");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, Tile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, Color[] light){
        IResourceName tex;

        TileEntityChest chest = world.getTileEntity(x, y, TileEntityChest.class);
        if(chest != null && chest.openCount > 0){
            tex = this.texOpen;
        }
        else{
            tex = this.texture;
        }

        manager.getTexture(tex).drawWithLight(renderX, renderY, scale, scale, light);
    }
}
