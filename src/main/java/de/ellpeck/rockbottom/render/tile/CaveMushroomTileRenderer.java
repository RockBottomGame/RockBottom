package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.MetaTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.CaveMushroomTile;

public class CaveMushroomTileRenderer extends MetaTileRenderer<CaveMushroomTile> {

    public CaveMushroomTileRenderer(ResourceName texture) {
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, CaveMushroomTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        for (int i = 0; i < light.length; i++) {
            light[i] = Math.max(light[i], Colors.multiply(Colors.WHITE, 0.2F));
        }
        manager.getTexture(this.getTextureResource(tile, state.get(tile.metaProp))).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
    }

    @Override
    public void renderItem(IGameInstance game, IAssetManager manager, IRenderer renderer, CaveMushroomTile tile, ItemInstance instance, float x, float y, float scale, int filter, boolean mirrored) {
        manager.getTexture(tile.subResourceNames.get(instance.getMeta()).addSuffix(".item")).draw(x, y, scale, scale, filter, mirrored, false);
    }
}
