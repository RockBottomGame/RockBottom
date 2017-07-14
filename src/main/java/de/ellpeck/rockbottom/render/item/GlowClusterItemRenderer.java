package de.ellpeck.rockbottom.render.item;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.item.ItemInstance;
import de.ellpeck.rockbottom.api.render.item.IItemRenderer;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.item.ItemGlowCluster;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class GlowClusterItemRenderer implements IItemRenderer<ItemGlowCluster>{

    private final IResourceName textureBase;
    private final IResourceName textureGlow;

    public GlowClusterItemRenderer(IResourceName texture){
        IResourceName pref = texture.addPrefix("items.");
        this.textureBase = pref.addSuffix(".base");
        this.textureGlow = pref.addSuffix(".glow");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, ItemGlowCluster item, ItemInstance instance, float x, float y, float scale, Color filter){
        manager.getTexture(this.textureBase).draw(x, y, scale, scale, filter);
        manager.getTexture(this.textureGlow).draw(x, y, scale, scale);
    }
}
