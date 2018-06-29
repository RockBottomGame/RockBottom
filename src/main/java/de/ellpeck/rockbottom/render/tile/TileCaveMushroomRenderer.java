package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.TileMetaRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileCaveMushroom;

public class TileCaveMushroomRenderer extends TileMetaRenderer<TileCaveMushroom> {

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileCaveMushroom tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        for (int i = 0; i < light.length; i++) {
            light[i] = Math.max(light[i], Colors.multiply(Colors.WHITE, 0.2F));
        }
        this.getTexture(manager, tile, state.get(tile.metaProp)).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
    }
}
