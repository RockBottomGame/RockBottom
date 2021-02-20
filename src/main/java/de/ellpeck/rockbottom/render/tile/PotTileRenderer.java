package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.TileItem;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.IPotPlantable;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.TileMeta;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.PotTile;
import de.ellpeck.rockbottom.world.tile.entity.PotTileEntity;

public class PotTileRenderer extends DefaultTileRenderer<PotTile> {

    public PotTileRenderer(ResourceName texture) {
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, PotTile tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        PotTileEntity pot = world.getTileEntity(x, y, PotTileEntity.class);
        if (pot != null) {
            ItemInstance inst = pot.getFlower();
            if (inst != null) {
                Item item = inst.getItem();
                IPotPlantable plant = IPotPlantable.getPlantable(inst);
                if (plant != null) {
                    float plantScale = plant.getRenderScale() * scale;
                    float plantScaleOffset = scale - plantScale;
                    float xOffset = plant.getRenderXOffset(world, state, x, y, inst) * scale;
                    float yOffset = plant.getRenderYOffset(world, state, x, y, inst) * scale;
                    int visualLight = world.getCombinedVisualLight(Util.floor(x), Util.floor(y));
                    int color = RockBottomAPI.getApiHandler().getColorByLight(visualLight, layer);
                    this.renderPlant(game, manager, renderer, world, x, y, layer, renderX + plantScaleOffset / 2 + xOffset, renderY + plantScaleOffset + yOffset, plantScale, light, color, inst);
                }
            }
        }

        super.render(game, manager, renderer, world, tile, state, x, y, layer, renderX, renderY, scale, light);
    }

    private void renderPlant(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light, int color, ItemInstance plant) {
        if (plant.getItem() instanceof TileItem) {
            Tile tile = ((TileItem) plant.getItem()).getTile();
            TileState renderState = tile.getDefState();
            if (tile instanceof TileMeta) {
                renderState = renderState.prop(((TileMeta) tile).metaProp, plant.getMeta());
            }
            tile.getRenderer().render(game, manager, renderer, world, tile, renderState, x, y, layer, renderX, renderY, scale, light);
        } else {
            plant.getItem().getRenderer().render(game, manager, renderer, plant.getItem(), plant, renderX, renderY, scale, color);
        }
    }
}
