package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.EntityBoomerang;

public class BoomerangEntityRenderer implements IEntityRenderer<EntityBoomerang> {
    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, EntityBoomerang entity, float x, float y, int light) {
        ItemInstance instance = entity.getItem();
        if (instance != null) {
            Item item = instance.getItem();
            IItemRenderer renderer = item.getRenderer();
            if (renderer != null) {
                float rotation = entity.ticksExisted * 15F;
                g.translate(x, y);
                g.rotate(rotation);
                renderer.render(game, manager, g, item, instance, -entity.getWidth() * 0.375F, -entity.getHeight() * 0.375F, 0.75F, light);
                g.rotate(-rotation);
                g.translate(-x, -y);
            }
        }
    }
}
