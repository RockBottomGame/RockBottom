package de.ellpeck.rockbottom.render.tile;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public interface ITileRenderer<T extends Tile>{

    void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, T tile, int x, int y, float renderX, float renderY, Color filter);

    void renderItem(RockBottom game, AssetManager manager, Graphics g, T tile, int meta, float x, float y, float scale, Color filter);

    Image getParticleTexture(RockBottom game, AssetManager manager, Graphics g, T tile, int meta);
}
