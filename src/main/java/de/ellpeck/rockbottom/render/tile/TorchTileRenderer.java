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
import de.ellpeck.rockbottom.world.tile.TorchTile;

public class TorchTileRenderer extends DefaultTileRenderer<TorchTile> {

    private final ResourceName[] texNormal = new ResourceName[2];
    private final ResourceName[] texLeft = new ResourceName[2];
    private final ResourceName[] texRight = new ResourceName[2];
    private final ResourceName[] texBack = new ResourceName[2];

    public TorchTileRenderer(ResourceName texture) {
        super(texture.addSuffix(".off"));

        ResourceName tileTexture = texture.addPrefix("tiles.");
        for (int i = 0; i < 2; i++) {
            String suffix = i == 0 ? ".on" : ".off";
            this.texNormal[i] = tileTexture.addSuffix(suffix);
            this.texLeft[i] = tileTexture.addSuffix(".left" + suffix);
            this.texRight[i] = tileTexture.addSuffix(".right" + suffix);
            this.texBack[i] = tileTexture.addSuffix(".back" + suffix);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, TorchTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        int meta = state.get(StaticTileProps.TORCH_FACING);

        ResourceName[] tex;
        if (meta == 0) {
            tex = this.texNormal;
        } else if (meta == 1) {
            tex = this.texRight;
        } else if (meta == 2) {
            tex = this.texLeft;
        } else {
            tex = this.texBack;
        }

        ResourceName name = tex[state.get(StaticTileProps.TORCH_TIMER) < 9 ? 0 : 1];
        manager.getTexture(name).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
    }
}
