package de.ellpeck.rockbottom.world.entity;

import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.AbstractEntitySlime;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.render.entity.SlimeEntityRenderer;
import de.ellpeck.rockbottom.world.entity.ai.TaskSlimeJump;

public class EntitySlime extends AbstractEntitySlime{

    private static final int VARIATION_COUNT = 8;
    private final IEntityRenderer renderer = new SlimeEntityRenderer();
    private int variation = Util.RANDOM.nextInt(VARIATION_COUNT);
    public TaskSlimeJump jumpTask = new TaskSlimeJump(0);

    public EntitySlime(IWorld world){
        super(world);
        this.addAiTask(this.jumpTask);
    }

    @Override
    public IEntityRenderer getRenderer(){
        return this.renderer;
    }

    @Override
    public void applyMotion(){
        if(!this.isClimbing){
            this.motionY -= 0.025;
        }

        this.motionX *= 0.9D;
        this.motionY *= this.isClimbing ? 0.5 : 0.98;
    }

    @Override
    public int getMaxHealth(){
        return 20;
    }

    @Override
    public int getRegenRate(){
        return 50;
    }

    @Override
    public int getVariation(){
        return this.variation;
    }

    @Override
    public int getRenderPriority(){
        return 10;
    }

    @Override
    protected int getJumpTimeout(){
        return 40;
    }

    @Override
    public void save(DataSet set){
        super.save(set);
        set.addInt("variation", this.variation);
    }

    @Override
    public void load(DataSet set){
        super.load(set);
        this.variation = set.getInt("variation");
    }

    @Override
    public int getSyncFrequency(){
        return 15;
    }

    @Override
    public float getWidth(){
        return 0.65F;
    }

    @Override
    public float getHeight(){
        return 0.65F;
    }
}
