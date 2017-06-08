package de.ellpeck.rockbottom.api.render.entity;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.world.IWorld;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public interface IEntityRenderer<T extends Entity>{

    void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, T entity, float x, float y, Color filter);

}
