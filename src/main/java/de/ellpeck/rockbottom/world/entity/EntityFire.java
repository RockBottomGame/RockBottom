package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.entity.AbstractEntityFire;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.entity.FireEntityRenderer;

public class EntityFire extends AbstractEntityFire {

    private final int lifespan;
    private int life;
    private float size;

    public EntityFire(IWorld world) {
        super(world);
        this.lifespan = Util.seconds(5) + Util.seconds(Util.RANDOM.nextInt(5));
        this.life = 0;
        this.size = 0.5f;
    }

    @Override
    public void update(IGameInstance game) {
        super.update(game);
        this.life++;
        if (this.life >= this.lifespan)
            this.setDead(true);
    }

    @Override
    public float getWidth() {
        return this.size;
    }

    @Override
    public float getHeight() {
        return this.size;
    }

    @Override
    public IEntityRenderer getRenderer() {
        return new FireEntityRenderer();
    }

    @Override
    public int getLifespan() {
        return this.lifespan;
    }

    @Override
    public int getLife() {
        return this.life;
    }

    @Override
    public float getSize() {
        return this.size;
    }
}
