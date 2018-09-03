package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileCorn;

public class TileCornRenderer extends DefaultTileRenderer<TileCorn> {

    private final ResourceName[][] textures;

    public TileCornRenderer(ResourceName name) {
        super(name);

        this.textures = new ResourceName[2][10];
        for (int i = 0; i < 10; i++) {
            this.textures[0][i] = name.addSuffix("." + i + ".bottom");
            this.textures[1][i] = name.addSuffix("." + i + ".top");
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileCorn tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        int top = state.get(StaticTileProps.TOP_HALF) ? 1 : 0;
        int variant = state.get(StaticTileProps.CORN_GROWTH);
        manager.getTexture(this.textures[top][variant]).draw(renderX, renderY, scale, scale, light);
    }
}
