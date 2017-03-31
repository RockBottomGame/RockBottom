package de.ellpeck.game.render.tile;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public interface ITileRenderer<T extends Tile>{

    void render(Game game, AssetManager manager, Graphics g, IWorld world, T tile, int x, int y, float renderX, float renderY, Color filter);

    void renderItem(Game game, AssetManager manager, Graphics g, T tile, float x, float y, float scale, Color filter);

    Image getParticleTexture(Game game, AssetManager manager, Graphics g, T tile, byte meta);
}
