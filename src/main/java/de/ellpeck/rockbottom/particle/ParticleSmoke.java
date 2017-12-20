package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;

public class ParticleSmoke extends Particle{

    private static final IResourceName SMOKE_NAME = RockBottomAPI.createInternalRes("particle.smoke");
    private final float scale;

    public ParticleSmoke(IWorld world, double x, double y, double motionX, double motionY, float scale){
        super(world, x, y, motionX, motionY, Util.RANDOM.nextInt(50)+20);
        this.scale = scale;
    }

    @Override
    protected void applyMotion(){
        this.motionY += 0.003;

        this.motionX *= 0.99;
        this.motionY *= 0.99;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IGraphics g, float x, float y, int filter){
        float size = this.scale*(1F-(float)this.life/(float)this.maxLife);

        ITexture image = manager.getTexture(SMOKE_NAME);
        image.draw(x-size/2, y-size/2, size, size);
    }
}
