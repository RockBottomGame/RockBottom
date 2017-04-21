package de.ellpeck.rockbottom.render.entity;

import de.ellpeck.rockbottom.RockBottom;
import de.ellpeck.rockbottom.assets.AssetManager;
import de.ellpeck.rockbottom.world.IWorld;
import de.ellpeck.rockbottom.world.entity.Entity;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IEntityRenderer<T extends Entity>{

    void render(RockBottom game, AssetManager manager, Graphics g, IWorld world, T entity, float x, float y, Color filter);

}
