package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.render.tile.MetaTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.GrassTuftTile;

public class GrassTuftTileRenderer extends MetaTileRenderer<GrassTuftTile> {

    public GrassTuftTileRenderer(ResourceName texture) {
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, GrassTuftTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        if (layer != TileLayer.MAIN) {
            this.doRender(manager, world, tile, state, x, y, renderX, renderY, scale, light);
        }
    }

    @Override
    public void renderInForeground(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, GrassTuftTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        if (layer == TileLayer.MAIN) {
            this.doRender(manager, world, tile, state, x, y, renderX, renderY, scale, light);
        }
    }

    private void doRender(IAssetManager manager, IWorld world, GrassTuftTile tile, TileState state, int x, int y, float renderX, float renderY, float scale, int[] light) {
        float topX = renderX + (float) Math.sin((world.getTotalTime() + (x * 50)) / 25D % (2 * Math.PI)) * scale / 15F;
        ITexture texture = manager.getTexture(this.getTextureResource(tile, state.get(tile.metaProp))).getPositionalVariation(x, y);
        texture.draw(topX, renderY, renderX, renderY + scale, renderX + scale, renderY + scale, topX + scale, renderY, 0, 0, texture.getRenderWidth(), texture.getRenderHeight(), light, Colors.WHITE);
    }
}
