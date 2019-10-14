package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.MultiTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileCombiner;
import de.ellpeck.rockbottom.world.tile.TileSimpleFurnace;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityCombiner;
import de.ellpeck.rockbottom.world.tile.entity.TileEntitySimpleFurnace;

import java.util.HashMap;
import java.util.Map;

public class TileCombinerRenderer extends MultiTileRenderer<TileCombiner> {

    protected final Map<Pos2, ResourceName> texturesActive = new HashMap<>();

    public TileCombinerRenderer(ResourceName texture, TileCombiner tile) {
        super(texture, tile);

        for (int x = 0; x < tile.getWidth(); x++) {
            for (int y = 0; y < tile.getHeight(); y++) {
                if (tile.isStructurePart(x, y)) {
                    this.texturesActive.put(new Pos2(x, y), this.texture.addSuffix(".active." + x + '.' + y));
                }
            }
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileCombiner tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        Pos2 main = tile.getMainPos(x, y, state);
        TileEntityCombiner tileEntity = world.getTileEntity(layer, main.getX(), main.getY(), TileEntityCombiner.class);

        if (tileEntity != null && tileEntity.isActive()) {
            Pos2 innerCoord = tile.getInnerCoord(state);
            manager.getTexture(this.texturesActive.get(innerCoord)).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
        } else {
            super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);
        }
    }
}
