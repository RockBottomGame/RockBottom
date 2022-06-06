package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.RopeTile;

public class RopeTileRenderer extends DefaultTileRenderer<RopeTile> {


    public RopeTileRenderer(ResourceName texture, boolean hasCustomItemTexture) {
        super(texture, hasCustomItemTexture);
    }

    @Override
    public ResourceName getTextureForState(IWorld world, TileLayer layer, int x, int y, TileState state) {
        return state.get(StaticTileProps.IS_ENDING) ? this.texture.addSuffix(".ending") : super.getTextureForState(world, layer, x, y, state);
    }
}
