package de.ellpeck.rockbottom.particle;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.item.Item;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.particle.Particle;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IWorld;

public class ParticleItem extends Particle {

    private final ItemInstance instance;
    private int renderPixel = Colors.NO_COLOR;

    public ParticleItem(IWorld world, double x, double y, double motionX, double motionY, ItemInstance instance) {
        super(world, x, y, motionX, motionY, Util.RANDOM.nextInt(30) + 10);
        this.instance = instance;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g, float x, float y, int filter) {
        if (this.renderPixel == Colors.NO_COLOR) {
            Item item = this.instance.getItem();
            IItemRenderer renderer = item.getRenderer();
            if (renderer != null) {
                ITexture texture = renderer.getParticleTexture(game, manager, g, item, this.instance);
                this.renderPixel = ParticleTile.makeRenderPixel(texture);
            }
        }

        if (this.renderPixel != Colors.NO_COLOR) {
            g.addFilledRect(x, y, 0.12F, 0.12F, Colors.multiply(this.renderPixel, filter));
        }
    }
}
