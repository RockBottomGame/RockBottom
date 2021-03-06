package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.StaticTileProps;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.gui.MessageBoxGui;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.TileItem;
import de.ellpeck.rockbottom.api.net.chat.component.TranslationChatComponent;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.StardropTileRenderer;

import java.util.ArrayList;
import java.util.List;

public class StardropTile extends BasicTile {

    public StardropTile() {
        super(ResourceName.intern("stardrop"));
        this.addProps(StaticTileProps.STARDROP_GROWTH);
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(FormattingCode.GRAY + manager.localize(ResourceName.intern("info.stardrop")));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new StardropTileRenderer(name);
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }

    @Override
    public void updateRandomly(IWorld world, int x, int y, TileLayer layer) {
        if (Util.RANDOM.nextInt(200) <= 0) {
            TileState state = world.getState(layer, x, y);
            if (state.get(StaticTileProps.STARDROP_GROWTH) < 2) {
                world.setState(layer, x, y, state.cycleProp(StaticTileProps.STARDROP_GROWTH));
            }
        }
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return false;
    }

    @Override
    public boolean canPlace(IWorld world, int x, int y, TileLayer layer, AbstractPlayerEntity player) {
        return false;
    }

    @Override
    public boolean canStay(IWorld world, int x, int y, TileLayer layer, int changedX, int changedY, TileLayer changedLayer) {
        return world.getState(x, y + 1).getTile().isFullTile();
    }

    @Override
    public List<ItemInstance> getDrops(IWorld world, int x, int y, TileLayer layer, Entity destroyer) {
        if (world.getState(layer, x, y).get(StaticTileProps.STARDROP_GROWTH) < 2) {
            return new ArrayList<>();
        } else {
            return super.getDrops(world, x, y, layer, destroyer);
        }
    }

    @Override
    protected TileItem createItemTile() {
        return new TileItem(this.getName()) {
            @Override
            public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player, ItemInstance instance) {
                if (!world.isClient()) {
                    player.setMaxHealth(player.getMaxHealth() + 10);
                    player.getInv().remove(player.getSelectedSlot(), 1);
                }

                player.openGui(new MessageBoxGui(null, 0.25F, 200, 18, new TranslationChatComponent(ResourceName.intern("info.stardrop.consume"))));

                return true;
            }

            @Override
            public double getMaxInteractionDistance(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player, ItemInstance instance) {
                return Double.MAX_VALUE;
            }
        };
    }
}
