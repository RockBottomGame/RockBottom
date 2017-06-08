package de.ellpeck.rockbottom.game.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.util.Util;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ParticleSmoke extends Particle{

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

        Image image = manager.getImage("particle.smoke");
        image.draw(x-(image.getWidth()*size/2), y-(image.getHeight()*size/2), size);
    }
}
