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
import de.ellpeck.rockbottom.world.tile.LampTile;

public class LampTileRenderer extends DefaultTileRenderer<LampTile> {

    private final ResourceName texLeft;
    private final ResourceName texRight;
    private final ResourceName texBack;

    public LampTileRenderer(ResourceName texture) {
        super(texture);

        this.texLeft = this.texture.addSuffix(".left");
        this.texRight = this.texture.addSuffix(".right");
        this.texBack = this.texture.addSuffix(".back");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, LampTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        int meta = state.get(StaticTileProps.TORCH_FACING);

        ResourceName tex;
        if (meta == 0) {
            tex = this.texture;
        } else if (meta == 1) {
            tex = this.texRight;
        } else if (meta == 2) {
            tex = this.texLeft;
        } else {
            tex = this.texBack;
        }

        manager.getTexture(tex).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
    }
}
