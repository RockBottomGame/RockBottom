package de.ellpeck.game.render.entity;

import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.world.IWorld;
import de.ellpeck.game.world.entity.Entity;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public interface IEntityRenderer<T extends Entity>{

    void render(Game game, AssetManager manager, Graphics g, IWorld world, T entity, float x, float y);

}
