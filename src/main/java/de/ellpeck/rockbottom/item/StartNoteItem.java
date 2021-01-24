package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractPlayerEntity;
import de.ellpeck.rockbottom.api.item.BasicItem;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.StartNoteGui;

import java.util.List;

public class StartNoteItem extends BasicItem {

    public static final int TEXT_VARIATIONS = 3;

    public StartNoteItem() {
        super(ResourceName.intern("start_note"));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player, ItemInstance instance) {
        return player.openGui(new StartNoteGui(instance.getMeta()));
    }

    @Override
    public int getInteractionPriority(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player, ItemInstance instance) {
        return -5;
    }

    @Override
    public double getMaxInteractionDistance(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractPlayerEntity player, ItemInstance instance) {
        return Double.MAX_VALUE;
    }

    @Override
    public int getHighestPossibleMeta() {
        return TEXT_VARIATIONS - 1;
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced, boolean isRealItem) {
        super.describeItem(manager, instance, desc, isAdvanced, isRealItem);
        desc.add(manager.localize(ResourceName.intern("info.start_note")));
    }
}
