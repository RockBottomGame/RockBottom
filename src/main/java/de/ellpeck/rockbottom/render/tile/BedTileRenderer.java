package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.MultiTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.BedTile;

import java.util.HashMap;
import java.util.Map;

public class BedTileRenderer extends MultiTileRenderer<BedTile> {

    private final Map<Pos2, ResourceName> coverTextures = new HashMap<>();
    private final ResourceName leftPillow;
    private final ResourceName rightPillow;

    public BedTileRenderer(ResourceName texture, BedTile tile) {
        super(texture, tile);

        for (int x = 0; x < tile.getWidth(); x++) {
            for (int y = 0; y < tile.getHeight(); y++) {
                if (tile.isStructurePart(x, y)) {
                    this.coverTextures.put(new Pos2(x, y), this.texture.addSuffix(".cover." + x + '.' + y));
                }
            }
        }

        this.leftPillow = this.texture.addSuffix(".left_pillow");
        this.rightPillow = this.texture.addSuffix(".right_pillow");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, BedTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        // Draw Frame
        super.render(game, manager, renderer, world, tile, state, x, y, layer, renderX, renderY, scale, light);

        // Draw Cover
        Pos2 innerCoord = tile.getInnerCoord(state);
        manager.getTexture(this.coverTextures.get(innerCoord)).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light, state.get(StaticTileProps.COVER_COLOR).color);

        // Draw Pillow
        boolean isFacingRight = state.get(StaticTileProps.FACING_RIGHT);
        if (tile.isPillowPos(world, x, y)) {
            manager.getTexture(isFacingRight ? this.rightPillow : this.leftPillow).draw(renderX, renderY, scale, scale, light, state.get(StaticTileProps.PILLOW_COLOR).color);
        }
    }
}
