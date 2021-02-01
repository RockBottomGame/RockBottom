package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.entity.FallingEntity;

public class FallingTile extends BasicTile {

    public FallingTile(ResourceName name) {
        super(name);
    }

    private static void tryFall(IWorld world, int x, int y, TileLayer layer) {
        if (!world.isClient() && layer == TileLayer.MAIN) {
            if (world.isPosLoaded(x, y - 1)) {
                TileState below = world.getState(x, y - 1);
                if (below.getTile().canReplace(world, x, y - 1, layer)) {
                    FallingEntity falling = new FallingEntity(world, world.getState(layer, x, y));
                    falling.setPos(x + 0.5, y + 0.5);
                    world.addEntity(falling);

                    world.setState(x, y, GameContent.Tiles.AIR.getDefState());
                }
            }
        }
    }

    @Override
    public void onChangeAround(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        tryFall(world, x, y, layer);
    }

    @Override
    public void onAdded(IWorld world, int x, int y, TileLayer layer) {
        tryFall(world, x, y, layer);
    }
}
