package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TilePlatform;

public class TilePlatformRenderer extends DefaultTileRenderer<TilePlatform> {

    public TilePlatformRenderer(ResourceName texture) {
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TilePlatform tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {

        if (state.get(StaticTileProps.HAS_LADDER)) {
            GameContent.TILE_LADDER.getRenderer().render(game, manager, g, world, GameContent.TILE_LADDER, GameContent.TILE_LADDER.getDefState(), x, y, layer, renderX, renderY, scale, light);
        }
        ITexture texture = manager.getTexture(this.texture).getPositionalVariation(x, y);

        Tile left = world.getState(x - 1, y).getTile();
        Tile right = world.getState(x + 1, y).getTile();

        boolean leftFullTile = left.isFullTile();
        boolean rightFullTile = right.isFullTile();
        boolean leftPlatform = left instanceof TilePlatform;
        boolean rightPlatform = right instanceof TilePlatform;

        // The row and column to draw from the texture and whether to flip it in X.
        int row = 0;
        int col = 1;
        boolean flip = false;

        // I did nice visualisation of this.
        // |                is a full tile
        // .- OR - OR -.    is any  platform (left side and right side attached to full tile respectively, middle no full tile attachment)
        // .= OR = OR =.    is this platform (left side and right side attached to full tile respectively, middle no full tile attachment)

        if (leftFullTile && rightFullTile) { //  |.=.|
            col = 2;
        } else if (leftFullTile || rightFullTile) { //  |.=--  OR  --=.|
            col = 0;
            if (leftFullTile) {
                row = rightPlatform ? 1 : 0; //  |.=-- else |.=
            } else {
                row = leftPlatform ? 1 : 0;  //  --=.| else =.|
                flip = true;
            }
        } else if (!(leftPlatform && rightPlatform)) { //  |.--=  OR  =--.|  OR  =  NOT  |.---=---.|
            if (!leftPlatform && !rightPlatform) {  //  =
                row = 1;
                col = 2;
            } else {    //  |.--=  OR  =--.|
                row = 1;
                flip = rightPlatform;   //  rightPlatform ? =--.| else |.--=
            }
        }

        texture.draw(renderX + (flip ? scale : 0), renderY, renderX + (flip ? 0 : scale), renderY + scale, col * 12, row * 12, col * 12 + 12, row * 12 + 12, light);
    }

    @Override
    public void renderItem(IGameInstance game, IAssetManager manager, IRenderer g, TilePlatform tile, ItemInstance instance, float x, float y, float scale, int filter) {
        manager.getTexture(this.texture).draw(x, y + scale/2, x + scale, y + scale + scale/2, 24, 0, 36, 12, filter);
    }
}
