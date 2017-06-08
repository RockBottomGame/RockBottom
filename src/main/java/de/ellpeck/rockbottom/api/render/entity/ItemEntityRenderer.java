package de.ellpeck.rockbottom.api.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.EntityItem;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ItemEntityRenderer implements IEntityRenderer<EntityItem>{

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, EntityItem entity, float x, float y, Color filter){
        Item item = entity.item.getItem();
        IItemRenderer renderer = item.getRenderer();
        if(renderer != null){
            float bob = (float)Math.sin(entity.ticksExisted/20D%(2*Math.PI))*0.1F;
            renderer.render(game, manager, g, item, entity.item, x-0.25F, y+bob-0.45F, 0.5F, filter);
        }
    }
}
