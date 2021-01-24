package de.ellpeck.rockbottom.world.tile;


import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.TileTallPlant;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.CottonTileRenderer;

public class CottonTile extends TileTallPlant {
    public CottonTile() {
        super(ResourceName.intern("cotton"));
        this.addProps(StaticTileProps.ALIVE);
    }

    @Override
    protected final ITileRenderer createRenderer(ResourceName name) {
        return new CottonTileRenderer(name);
    }

    @Override
    public final void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        TileState state = world.getState(layer, x, y);
        if (!state.get(StaticTileProps.TOP_HALF)) {
            int growth = state.get(StaticTileProps.PLANT_GROWTH);

            boolean left = true;
            TileState liquid = world.getState(TileLayer.LIQUIDS, x - 1, y - 1);
            if (liquid.getTile() != GameContent.TILE_WATER) {
                left = false;
                liquid = world.getState(TileLayer.LIQUIDS, x + 1, y - 1);
            }

            boolean wasAlive = state.get(StaticTileProps.ALIVE);
            boolean alive = liquid.getTile() == GameContent.TILE_WATER && world.getCombinedLight(x, y) > 50;
            if (wasAlive != alive) {
                if (alive) {
                    world.setState(layer, x, y, state.prop(StaticTileProps.ALIVE, true));
                    if (growth >= 4) {
                        TileState up = world.getState(layer, x, y + 1);
                        if (up.getTile() == this) {
                            world.setState(layer, x, y + 1, up.prop(StaticTileProps.ALIVE, true));
                        }
                    }
                } else {
                    world.setState(layer, x, y, state.prop(StaticTileProps.ALIVE, false));
                    if (growth >= 4) {
                        TileState up = world.getState(layer, x, y + 1);
                        if (up.getTile() == this) {
                            world.setState(layer, x, y + 1, up.prop(StaticTileProps.ALIVE, false));
                        }
                    }
                }
            }

            if (Util.RANDOM.nextFloat() >= 0.95F && growth < 9) {
                if (alive) {
                    if (growth >= 3) {
                        TileState topHalf;
                        if ((topHalf = world.getState(layer, x, y + 1)).getTile() == this) {
                            world.setState(layer, x, y + 1, topHalf.prop(StaticTileProps.PLANT_GROWTH, growth + 1).prop(StaticTileProps.ALIVE, true));
                        } else {
                            if (!world.getState(layer, x, y + 1).getTile().canReplace(world, x, y + 1, layer)) {
                                return;
                            }

                            world.setState(layer, x, y + 1, this.getDefState().prop(StaticTileProps.TOP_HALF, Boolean.TRUE).prop(StaticTileProps.PLANT_GROWTH, growth + 1).prop(StaticTileProps.ALIVE, true));
                        }
                    }
                    if (Util.RANDOM.nextFloat() >= 0.6f) {
                        int level = liquid.get(GameContent.TILE_WATER.level);
                        if (level > 0) {
                            world.setState(TileLayer.LIQUIDS, x + (left ? -1 : 1), y - 1, liquid.prop(GameContent.TILE_WATER.level, level - 1));
                        } else {
                            world.setState(TileLayer.LIQUIDS, x + (left ? -1 : 1), y - 1, GameContent.TILE_AIR.getDefState());
                        }
                    }
                    world.setState(layer, x, y, state.prop(StaticTileProps.PLANT_GROWTH, growth + 1).prop(StaticTileProps.ALIVE, true));
                } else {
                    world.setState(layer, x, y, state.prop(StaticTileProps.ALIVE, false));
                    if (growth >= 3) {
                        world.setState(layer, x, y + 1, this.getDefState().prop(StaticTileProps.TOP_HALF, Boolean.TRUE).prop(StaticTileProps.PLANT_GROWTH, growth).prop(StaticTileProps.ALIVE, false));
                    }
                }
            }
        }

    }
}
