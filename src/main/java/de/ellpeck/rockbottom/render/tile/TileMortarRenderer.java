package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.inventory.IInventory;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.tile.TileMortar;
import de.ellpeck.rockbottom.world.tile.entity.TileEntityMortar;

public class TileMortarRenderer extends DefaultTileRenderer<TileMortar> {

    public TileMortarRenderer(ResourceName texture) {
        super(texture);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileMortar tile, TileState state, int x, int y, TileLayer layer, float renderX, float renderY, float scale, int[] light) {
        super.render(game, manager, g, world, tile, state, x, y, layer, renderX, renderY, scale, light);

        TileEntityMortar tileEntity = world.getTileEntity(layer, x, y, TileEntityMortar.class);
        if (tileEntity != null) {
            IInventory inventory = tileEntity.getTileInventory();

            float xOff = scale / 6;
            for (ItemInstance content : inventory) {
                if (content != null) {
                    Item item = content.getItem();
                    IItemRenderer renderer = item.getRenderer();
                    if (renderer != null) {
                        renderer.render(game, manager, g, item, content, renderX + xOff, renderY + scale / 2.5F, scale / 3, light[0]);
                    }
                }
                xOff += scale / 6;
            }
        }
    }

    @Override
    public void renderOnMouseOver(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, TileMortar tile, TileState state, int x, int y, TileLayer layer, float mouseX, float mouseY) {
        TileEntityMortar tileEntity = world.getTileEntity(layer, x, y, TileEntityMortar.class);
        if (tileEntity != null) {
            float progress = tileEntity.getProgress();
            if (progress >= 0F) {
                g.addFilledRect(mouseX + 5, mouseY - 5 - progress * 15, 5, progress * 15, Colors.GREEN);
                g.addEmptyRect(mouseX + 5, mouseY - 20, 5, 15, Colors.BLACK);
            }
        }
    }
}
