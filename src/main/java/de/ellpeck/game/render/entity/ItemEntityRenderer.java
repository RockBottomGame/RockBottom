package de.ellpeck.game.render.entity;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.item.Item;
import de.ellpeck.game.render.item.IItemRenderer;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.entity.EntityItem;
import org.newdawn.slick.Graphics;

public class ItemEntityRenderer implements IEntityRenderer<EntityItem>{

    @Override
    public void render(Game game, AssetManager manager, Graphics g, IWorld world, EntityItem entity, float x, float y){
        Item item = entity.item.getItem();
        IItemRenderer renderer = item.getRenderer();
        if(renderer != null){
            float bob = (float)Math.sin(game.getContainer().getTime()/500D%(2*Math.PI))*0.1F;
            renderer.render(game, manager, g, item, x, y+bob-0.2F, 0.5F);
        }
    }
}
