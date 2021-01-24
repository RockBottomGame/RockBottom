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
import de.ellpeck.rockbottom.world.tile.CornTile;

public class CornTileRenderer extends DefaultTileRenderer<CornTile> {

    private final ResourceName[][] textures;

    public CornTileRenderer(ResourceName texture) {
        super(texture);

        this.textures = new ResourceName[2][10];
        for (int i = 0; i < 10; i++) {
            this.textures[0][i] = this.texture.addSuffix("." + i + ".bottom");
            this.textures[1][i] = this.texture.addSuffix("." + i + ".top");
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, CornTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
    }

    @Override
    public void renderInForeground(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, CornTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        int top = state.get(StaticTileProps.TOP_HALF) ? 1 : 0;
        int variant = state.get(StaticTileProps.PLANT_GROWTH);
        manager.getTexture(this.textures[top][variant]).draw(renderX, renderY, scale, scale, light);
    }
}
