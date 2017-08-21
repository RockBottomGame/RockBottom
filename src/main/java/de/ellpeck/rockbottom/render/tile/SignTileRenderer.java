package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.gui.GuiSign;
import de.ellpeck.rockbottom.world.tile.TileSign;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySign;
import org.newdawn.slick.Graphics;

public class SignTileRenderer extends DefaultTileRenderer<TileSign>{

    public SignTileRenderer(IResourceName texture){
        super(texture);
    }

    @Override
    public void renderOnMouseOver(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, TileSign tile, TileState state, int x, int y, TileLayer layer, float mouseX, float mouseY){
        TileEntitySign tileEntity = world.getTileEntity(x, y, TileEntitySign.class);
        if(tileEntity != null && tileEntity.text != null){
            GuiSign.renderSignText(manager, tileEntity.text, mouseX+51, mouseY, 0.25F);
        }
    }
}
