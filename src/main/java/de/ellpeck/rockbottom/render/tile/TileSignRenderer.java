package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.GuiSign;
import de.ellpeck.rockbottom.world.tile.TileSign;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySign;

public class TileSignRenderer extends DefaultTileRenderer<TileSign>{

    public TileSignRenderer(IResourceName texture){
        super(texture);
    }

    @Override
    public void renderOnMouseOver(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileSign tile, TileState state, int x, int y, TileLayer layer, float mouseX, float mouseY){
        AbstractEntityPlayer player = game.getPlayer();
        if(player.isInRange(g.getMousedTileX(), g.getMousedTileY(), AbstractEntityPlayer.RANGE)){
            TileEntitySign tileEntity = world.getTileEntity(x, y, TileEntitySign.class);
            if(tileEntity != null){
                g.scale(0.5F, 0.5F);
                GuiSign.drawSign(manager, tileEntity.text, true, (mouseX+3)*2F, (mouseY+3)*2F);
                g.scale(2F, 2F);
            }
        }
    }
}
