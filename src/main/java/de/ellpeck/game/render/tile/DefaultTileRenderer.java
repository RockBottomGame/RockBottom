package de.ellpeck.game.render.tile;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public class DefaultTileRenderer implements ITileRenderer{

    protected final String texture;

    public DefaultTileRenderer(String texture){
        this.texture = "tiles."+texture;
    }

    @Override
    public void render(Game game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        manager.getImage(this.texture).draw(renderX, renderY, 1F, 1F, filter);
    }

    @Override
    public void renderItem(Game game, AssetManager manager, Graphics g, Tile tile, float x, float y, float scale, Color filter){
        manager.getImage(this.texture).draw(x-0.25F, y-0.25F, scale, scale, filter);
    }

    @Override
    public Image getParticleTexture(Game game, AssetManager manager, Graphics g, Tile tile, int meta){
        return manager.getImage(this.texture);
    }
}
