package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.item.ToolProperty;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.BasicTile;
import de.ellpeck.rockbottom.api.tile.entity.IFilteredInventory;
import de.ellpeck.rockbottom.api.tile.entity.TileEntity;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.render.tile.MortarTileRenderer;
import de.ellpeck.rockbottom.world.tile.entity.MortarTileEntity;

public class MortarTile extends BasicTile {

    public MortarTile() {
        super(ResourceName.intern("mortar"));
    }

    @Override
    protected ITileRenderer createRenderer(ResourceName name) {
        return new MortarTileRenderer(name);
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player) {
        ItemInstance instance = player.getInv().get(player.getSelectedSlot());
        MortarTileEntity tile = world.getTileEntity(layer, x, y, MortarTileEntity.class);
        if (tile != null) {
            IFilteredInventory inv = tile.getTileInventory();
            if (instance != null) {
                if (instance.getItem().getToolProperties(instance).containsKey(ToolProperty.PESTLE)) {
                    if (!world.isClient()) {
                        instance.getItem().takeDamage(instance, player, 1);
                        tile.doPestleProgress(player);
                    }
                    return true;
                } else {
                    ItemInstance toAdd = instance.copy().setAmount(1);
                    for (int i = 0; i < inv.getSlotAmount(); i++) {
                        if (inv.get(i) == null) {
                            if (!world.isClient()) {
                                inv.set(i, toAdd);
                                player.getInv().remove(player.getSelectedSlot(), 1);
                            }
                            return true;
                        }
                    }
                }
            } else {
                for (int i = inv.getSlotAmount() - 1; i >= 0; i--) {
                    ItemInstance slot = inv.get(i);
                    if (slot != null) {
                        if (!world.isClient()) {
                            ItemInstance remain = player.getInv().addExistingFirst(slot, false);
                            inv.set(i, remain);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public TileEntity provideTileEntity(IWorld world, int x, int y, TileLayer layer) {
        return new MortarTileEntity(world, x, y, layer);
    }

    @Override
    public boolean canProvideTileEntity() {
        return true;
    }

    @Override
    public boolean canPlaceInLayer(TileLayer layer) {
        return layer == TileLayer.MAIN;
    }

    @Override
    public boolean isFullTile() {
        return false;
    }

    @Override
    public BoundingBox getBoundBox(IWorld world, TileState state, int x, int y, TileLayer layer) {
        return null;
    }
}
