package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.EntityFire;

public class FireEntityRenderer implements IEntityRenderer<EntityFire> {

    private final ResourceName animation = ResourceName.intern("entity.fire");

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, EntityFire entity, float x, float y, int light) {
        float width = entity.getWidth();
        float height = entity.getHeight();
        manager.getAnimation(this.animation).drawRow(0, x-width/2, y-height/2, width, height, light);
    }
}
