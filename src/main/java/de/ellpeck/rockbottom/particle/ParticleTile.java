package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;

public class ParticleTile extends Particle{

    private final TileState state;
    private int renderPixel = -1;

    public ParticleTile(IWorld world, double x, double y, double motionX, double motionY, TileState state){
        super(world, x, y, motionX, motionY, Util.RANDOM.nextInt(30)+10);
        this.state = state;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, float x, float y, int filter){
        if(this.renderPixel == -1){
            ITileRenderer renderer = this.state.getTile().getRenderer();
            if(renderer != null){
                ITexture texture = renderer.getParticleTexture(game, manager, g, this.state.getTile(), this.state);
                if(texture != null){
                    int width = texture.getRenderWidth();
                    int height = texture.getRenderHeight();

                    int pixelX = Util.RANDOM.nextInt(width);
                    int pixelY = Util.RANDOM.nextInt(height);
                    this.renderPixel = texture.getTextureColor(pixelX, pixelY);
                }
            }
        }

        if(this.renderPixel != -1){
            g.addFilledRect(x, y, 0.12F, 0.12F, Colors.multiply(this.renderPixel, filter));
        }
    }
}
