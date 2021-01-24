package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.SnowTile;

public class SnowTileRenderer extends DefaultTileRenderer<SnowTile> {

    public SnowTileRenderer(ResourceName texture) {
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, SnowTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        if (layer != TileLayer.MAIN) {
            super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
    }

    @Override
    public void renderInForeground(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, SnowTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        if (layer == TileLayer.MAIN) {
            super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
    }
}
