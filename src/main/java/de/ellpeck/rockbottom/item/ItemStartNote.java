package de.ellpeck.rockbottom.item;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.item.ItemBasic;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.gui.GuiStartNote;

import java.util.List;

public class ItemStartNote extends ItemBasic{

    public static final int TEXT_VARIATIONS = 3;

    public ItemStartNote(){
        super(RockBottomAPI.createInternalRes("start_note"));
    }

    @Override
    public boolean onInteractWith(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance){
        return player.openGui(new GuiStartNote(instance.getMeta()));
    }

    @Override
    public int getInteractionPriority(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player, ItemInstance instance){
        return -5;
    }

    @Override
    public double getMaxInteractionDistance(IWorld world, int x, int y, TileLayer layer, double mouseX, double mouseY, AbstractEntityPlayer player){
        return Double.MAX_VALUE;
    }

    @Override
    public int getHighestPossibleMeta(){
        return TEXT_VARIATIONS-1;
    }

    @Override
    public void describeItem(IAssetManager manager, ItemInstance instance, List<String> desc, boolean isAdvanced){
        super.describeItem(manager, instance, desc, isAdvanced);
        desc.add(manager.localize(RockBottomAPI.createInternalRes("info.start_note")));
    }
}
