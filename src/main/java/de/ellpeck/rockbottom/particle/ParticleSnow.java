package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;

public class ParticleSnow extends Particle {

    private final int color;

    public ParticleSnow(IWorld world, double x, double y, double motionX, double motionY, int maxLife) {
        super(world, x, y, motionX, motionY, maxLife);
        this.color = Colors.multiply(Colors.WHITE, 0.7F + Util.RANDOM.nextFloat() * 0.3F);
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, float x, float y, int filter) {
        g.addFilledRect(x, y, 0.12F, 0.12F, Colors.multiply(this.color, filter));
    }
}
