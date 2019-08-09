package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.construction.compendium.construction.ConstructionRecipe;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.tile.TileBasic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;

public class TileStone extends TileBasic {

    public TileStone() {
        super(ResourceName.intern("stone"));
    }

    @Override
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop) {
        super.onDestroyed(world, x, y, destroyer, layer, shouldDrop);

        if (!world.isClient() && shouldDrop && destroyer instanceof AbstractEntityPlayer) {
            AbstractEntityPlayer player = (AbstractEntityPlayer) destroyer;
            for (ConstructionRecipe recipe : ConstructionRegistry.STONE_TOOLS) {
                if (recipe != null) {
                    player.getKnowledge().teachRecipe(recipe, true);
                }
            }
            if (ConstructionRegistry.constructionTable != null) {
                player.getKnowledge().teachRecipe(ConstructionRegistry.constructionTable, true);
            }
        }
    }
}
