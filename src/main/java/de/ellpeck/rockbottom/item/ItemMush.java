package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.Registries;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.set.ModBasedDataSet;
import de.ellpeck.rockbottom.api.effect.ActiveEffect;
import de.ellpeck.rockbottom.api.effect.IEffect;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;

import java.util.List;

public class ItemMush extends ItemBasic {
    public ItemMush() {
        super(ResourceName.intern("mush"));
    }

    public static ActiveEffect getEffect(ItemInstance instance) {
        ModBasedDataSet set = instance.getAdditionalData();
        if (set != null) {
            DataSet data = set.getDataSet(ResourceName.intern("effect"));
            if (!data.isEmpty()) {
                IEffect effect = Registries.EFFECT_REGISTRY.get(new ResourceName(data.getString("name")));
                int amount = data.getInt("time");
                int level = data.hasKey("level") ? data.getInt("level") : 1;
                return new ActiveEffect(effect, amount, level);
            }
        }
        return null;
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced) {
        super.describeItem(manager, instance, desc, isAdvanced);

        ActiveEffect effect = getEffect(instance);
        if (effect != null) {
            desc.add(FormattingCode.GREEN + effect.getDisplayName(manager, RockBottomAPI.getGame().getPlayer()) + " (" + effect.getDisplayTime() + ')');
        } else {
            desc.add(FormattingCode.RED + "No Effect");
        }
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance) {
        ActiveEffect effect = getEffect(instance);
        if (effect != null) {
            if (!world.isClient()) {
                player.addEffect(effect);
                player.getInv().remove(player.getSelectedSlot(), 1);
            }
            return true;
        }
        return false;
    }

    @Override
    public double getMaxInteractionDistance(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance) {
        return Double.MAX_VALUE;
    }
}
