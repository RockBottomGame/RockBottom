package de.ellpeck.rockbottom.game.render.entity;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.game.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.world.entity.EntityItem;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class ItemEntityRenderer implements IEntityRenderer<EntityItem>{

    @Override
    public void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, EntityItem entity, float x, float y, Color filter){
        Item item = entity.item.getItem();
        IItemRenderer renderer = item.getRenderer();
        if(renderer != null){
            float bob = (float)Math.sin(entity.ticksExisted/20D%(2*Math.PI))*0.1F;
            renderer.render(game, manager, g, item, entity.item, x-0.25F, y+bob-0.45F, 0.5F, filter);
        }
    }
}
