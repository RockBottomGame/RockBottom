package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.world.entity.EntityFirework;

public class FireworkEntityRenderer implements IEntityRenderer<EntityFirework> {

    private final ResourceName texture = ResourceName.intern("items.firework");

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, IWorld world, EntityFirework entity, float x, float y, int light) {
        manager.getTexture(this.texture).draw(x - 0.25F, y - 0.25F, 0.5F, 0.5F, light);
    }
}
