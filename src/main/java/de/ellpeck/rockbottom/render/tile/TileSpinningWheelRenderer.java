package de.ellpeck.rockbottom.render.tile;


import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.MultiTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileSpinningWheel;

public class TileSpinningWheelRenderer extends MultiTileRenderer<TileSpinningWheel>
{
    public TileSpinningWheelRenderer(ResourceName texture, TileSpinningWheel tile)
    {
        super(texture, tile);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileSpinningWheel tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light)
    {
        Pos2 innerCoord = tile.getInnerCoord(state);
        Pos2 main = tile.getMainPos(x, y, state);
        int stage = world.getState(main.getX(), main.getY()).get(StaticTileProps.SPINNING_STAGE);

        ResourceName tex = this.textures.get(innerCoord);
        manager.getTexture(stage == 0 ? tex : this.texture.addSuffix("." + stage + "." + innerCoord.getX() + "." + innerCoord.getY())).getPositionalVariation(x, y).draw(renderX, renderY, scale, scale, light);
    }

}
