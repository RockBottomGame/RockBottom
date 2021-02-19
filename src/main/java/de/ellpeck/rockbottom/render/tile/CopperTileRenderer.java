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
import de.ellpeck.rockbottom.world.tile.CopperTile;

public class CopperTileRenderer extends DefaultTileRenderer<CopperTile> {

    private final ResourceName canister;

    public CopperTileRenderer(ResourceName texture) {
        super(texture);
        this.canister = this.texture.addSuffix(".canister");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, CopperTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        if (state.get(StaticTileProps.HAS_CANISTER)) {
            manager.getTexture(this.canister).draw(renderX, renderY, scale, scale, light);
        } else {
            super.render(game, manager, renderer, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
    }
}
