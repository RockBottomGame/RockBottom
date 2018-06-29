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
import de.ellpeck.rockbottom.world.tile.TileStardrop;

public class TileStardropRenderer extends DefaultTileRenderer<TileStardrop> {

    private final ResourceName[] stages = new ResourceName[StaticTileProps.STARDROP_GROWTH.getVariants()];

    public TileStardropRenderer(ResourceName texture) {
        super(texture);

        for (int i = 0; i < this.stages.length; i++) {
            this.stages[i] = this.texture.addSuffix(".stage_" + i);
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileStardrop tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        manager.getTexture(this.stages[state.get(StaticTileProps.STARDROP_GROWTH)]).draw(renderX, renderY, scale, scale, light);
    }
}
