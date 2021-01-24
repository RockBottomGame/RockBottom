package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.SignGui;
import de.ellpeck.rockbottom.world.tile.SignTile;
import de.ellpeck.rockbottom.world.tile.entity.SignTileEntity;

public class SignTileRenderer extends DefaultTileRenderer<SignTile> {

    public SignTileRenderer(ResourceName texture) {
        super(texture);
    }

    @Override
    public void renderOnMouseOver(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, SignTile tile, TileState state, int x, int y, TileLayer layer, float mouseX, float mouseY) {
        AbstractPlayerEntity player = game.getPlayer();
        if (player.isInRange(g.getMousedTileX(), g.getMousedTileY(), player.getRange())) {
            SignTileEntity tileEntity = world.getTileEntity(x, y, SignTileEntity.class);
            if (tileEntity != null) {
                g.scale(0.5F, 0.5F);
                SignGui.drawSign(manager, tileEntity.text, true, (mouseX + 3) * 2F, (mouseY + 3) * 2F);
                g.scale(2F, 2F);
            }
        }
    }
}
