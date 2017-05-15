package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.util.Util;
import de.ellpeck.rockbottom.world.World;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class ParticleSmoke extends Particle{

    private final float scale;

    public ParticleSmoke(World world, double x, double y, double motionX, double motionY, float scale){
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
    public void render(RockBottom game, AssetManager manager, Graphics g, float x, float y, Color filter){
        float size = this.scale*(1F-(float)this.life/(float)this.maxLife);

        Image image = manager.getImage("particle.smoke");
        image.draw(x-(image.getWidth()*size/2), y-(image.getHeight()*size/2), size);
    }
}
