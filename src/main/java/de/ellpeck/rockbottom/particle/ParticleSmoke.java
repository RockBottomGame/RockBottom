package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ParticleSmoke extends Particle{

    private static final IResourceName SMOKE_NAME = AbstractGame.internalRes("particle.smoke");
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
    public void render(IGameInstance game, IAssetManager manager, Graphics g, float x, float y, Color filter){
        float size = this.scale*(1F-(float)this.life/(float)this.maxLife);

        Image image = manager.getTexture(SMOKE_NAME);
        image.draw(x-(image.getWidth()*size/2), y-(image.getHeight()*size/2), size);
    }
}
