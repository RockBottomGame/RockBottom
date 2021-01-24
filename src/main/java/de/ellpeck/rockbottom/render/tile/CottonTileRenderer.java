package de.ellpeck.rockbottom.render.tile;


import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

public class CottonTileRenderer extends DefaultTileRenderer {
    private final ResourceName[][] alive = new ResourceName[2][10];
    private final ResourceName[][] dead = new ResourceName[2][5];

    public CottonTileRenderer(ResourceName name) {
        super(name);

        for (int i = 0; i < 10; ++i) {
            this.alive[0][i] = this.texture.addSuffix("." + i + ".bottom");
            this.alive[1][i] = this.texture.addSuffix("." + i + ".top");

            if (i < 5) {
                this.dead[0][i] = this.texture.addSuffix(".dead." + i + ".bottom");
                this.dead[1][i] = this.texture.addSuffix(".dead." + i + ".top");
            }
        }

    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, Tile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {

    }

    @Override
    public void renderInForeground(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, Tile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        boolean top = state.get(StaticTileProps.TOP_HALF);
        int growth = state.get(StaticTileProps.PLANT_GROWTH);

        if (!state.get(StaticTileProps.ALIVE)) {
            manager.getTexture(this.dead[top ? 1 : 0][Math.min(growth, 4)]).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
        } else {
            manager.getTexture(this.alive[top ? 1 : 0][growth]).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
        }

    }
}
