package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.world.World;

import java.util.ArrayList;
import java.util.List;

public class ParticleManager implements IParticleManager{

    private final List<Particle> particles = new ArrayList<>();

    public void update(IGameInstance game){
        for(int i = 0; i < this.particles.size(); i++){
            Particle particle = this.particles.get(i);
            particle.update(game);

            if(particle.isDead()){
                this.particles.remove(i);
                i--;
            }
        }
    }

    public void render(IGameInstance game, IAssetManager manager, IRenderer g, World world, float transX, float transY){
        this.particles.forEach(particle -> {
            IResourceName program = particle.getRenderShader(game, manager, g);
            g.setProgram(program == null ? null : manager.getShaderProgram(program));

            int light = world.getCombinedVisualLight(Util.floor(particle.x), Util.floor(particle.y));
            particle.render(game, manager, g, (float)particle.x-transX, (float)-particle.y-transY+1F, RockBottomAPI.getApiHandler().getColorByLight(light, TileLayer.MAIN));
        });
    }

    @Override
    public void addParticle(Particle particle){
        this.particles.add(particle);
    }

    @Override
    public void addTileParticles(IWorld world, int x, int y, TileState state){
        for(int i = 0; i < Util.RANDOM.nextInt(30)+20; i++){
            double motionX = Util.RANDOM.nextGaussian()*0.1F;
            double motionY = Util.RANDOM.nextGaussian()*0.1F;
            this.addSingleTileParticle(world, x+0.5, y+0.5, motionX, motionY, state);
        }
    }

    @Override
    public void addSingleTileParticle(IWorld world, double x, double y, double motionX, double motionY, TileState state){
        Particle particle = new ParticleTile(world, x, y, motionX, motionY, state);
        this.addParticle(particle);
    }

    @Override
    public void addSmokeParticle(IWorld world, double x, double y, double motionX, double motionY, float scale){
        this.addParticle(new ParticleSmoke(world, x, y, motionX, motionY, scale));
    }

    @Override
    public int getAmount(){
        return this.particles.size();
    }
}
