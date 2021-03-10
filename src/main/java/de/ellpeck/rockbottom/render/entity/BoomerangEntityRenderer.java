package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.BoomerangEntity;

public class BoomerangEntityRenderer implements IEntityRenderer<BoomerangEntity> {
    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer renderer, IWorld world, BoomerangEntity entity, float x, float y, int light) {
        ItemInstance instance = entity.getItem();
        if (instance != null) {
            Item item = instance.getItem();
            IItemRenderer itemRenderer = item.getRenderer();
            if (itemRenderer != null) {
                renderer.pushMatrix();
                float rotation = entity.ticksExisted * 15F;
                renderer.translate(x, y);
                renderer.rotate(rotation);
                itemRenderer.render(game, manager, renderer, item, instance, -entity.getWidth() * 0.375F, -entity.getHeight() * 0.375F, 0.75F, light, false);
                renderer.popMatrix();
            }
        }
    }
}
