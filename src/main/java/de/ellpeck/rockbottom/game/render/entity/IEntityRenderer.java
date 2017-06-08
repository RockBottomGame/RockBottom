package de.ellpeck.rockbottom.game.render.entity;

import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.world.entity.Entity;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IEntityRenderer<T extends Entity>{

    void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, T entity, float x, float y, Color filter);

}
