package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;

public class ParticleFirework extends Particle{

    private static final IResourceName TEXTURE = RockBottomAPI.createInternalRes("particle.firework");
    private final BoundBox boundingBox = new BoundBox(-0.075, -0.075, 0.075, 0.075);
    private final int particleColor;

    public ParticleFirework(IWorld world, double x, double y, double motionX, double motionY, int maxLife, int color){
        super(world, x, y, motionX, motionY, maxLife);
        this.particleColor = Colors.multiply(color, 0.75F+Util.RANDOM.nextFloat()*0.25F);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, float x, float y, int filter){
        float lifePercentage = 1F-(float)this.life/(float)this.maxLife;
        manager.getTexture(TEXTURE).draw(x-0.1F, y-0.1F, 0.2F, 0.2F, Colors.multiplyA(this.particleColor, lifePercentage));
    }

    @Override
    public BoundBox getBoundingBox(){
        return this.boundingBox;
    }

    @Override
    protected void applyMotion(){

    }
}
