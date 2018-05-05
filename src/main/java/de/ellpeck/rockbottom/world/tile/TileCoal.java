package de.ellpeck.rockbottom.world.tile;

import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.construction.ConstructionRegistry;

public class TileCoal extends TileOreMaterial{

    public TileCoal(){
        super(ResourceName.intern("coal"));
    }

    @Override
    public void onDestroyed(IWorld world, int x, int y, Entity destroyer, TileLayer layer, boolean shouldDrop){
        super.onDestroyed(world, x, y, destroyer, layer, shouldDrop);

        if(ConstructionRegistry.torch != null){
            if(!world.isClient() && shouldDrop && destroyer instanceof AbstractEntityPlayer){
                ((AbstractEntityPlayer)destroyer).getKnowledge().teachRecipe(ConstructionRegistry.torch, true);
            }
        }
    }
}
