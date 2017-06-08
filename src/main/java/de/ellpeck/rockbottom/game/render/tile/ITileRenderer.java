package de.ellpeck.rockbottom.game.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public interface ITileRenderer<T extends Tile>{

    void render(IGameInstance game, AssetManager manager, Graphics g, IWorld world, T tile, int x, int y, float renderX, float renderY, Color filter);

    void renderItem(IGameInstance game, AssetManager manager, Graphics g, T tile, int meta, float x, float y, float scale, Color filter);

    Image getParticleTexture(IGameInstance game, AssetManager manager, Graphics g, T tile, int meta);
}
