package de.ellpeck.rockbottom.api.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.world.IWorld;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

public interface ITileRenderer<T extends Tile>{

    void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, T tile, int x, int y, float renderX, float renderY, Color filter);

    void renderItem(IGameInstance game, IAssetManager manager, Graphics g, T tile, int meta, float x, float y, float scale, Color filter);

    Image getParticleTexture(IGameInstance game, IAssetManager manager, Graphics g, T tile, int meta);
}
