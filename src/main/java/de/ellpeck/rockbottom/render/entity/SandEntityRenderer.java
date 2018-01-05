package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.GameContent;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.EntitySand;

public class SandEntityRenderer implements IEntityRenderer<EntitySand>{

    private final ItemInstance instance = new ItemInstance(GameContent.TILE_SAND);

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, EntitySand entity, float x, float y, int light){
        Item item = this.instance.getItem();
        IItemRenderer renderer = item.getRenderer();
        if(renderer != null){
            renderer.render(game, manager, g, item, this.instance, x-0.5F, y-0.5F, 1F, light);
        }
    }
}
