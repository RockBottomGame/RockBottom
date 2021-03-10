package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.FallingEntity;

public class FallingEntityRenderer implements IEntityRenderer<FallingEntity> {

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, FallingEntity entity, float x, float y, int light) {
        if (entity.state != null) {
            Tile tile = entity.state.getTile();
            ITileRenderer renderer = tile.getRenderer();
            if (renderer != null) {
                renderer.renderItem(game, manager, g, tile, entity.stateInstance, x - 0.5F, y - 0.5F, 1F, light, false);
            }
        }
    }
}
