package de.ellpeck.rockbottom.game.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.game.RockBottom;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class TorchTileRenderer extends DefaultTileRenderer{

    public TorchTileRenderer(){
        super("torch");
    }

    @Override
    public void render(IGameInstance game, AssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        int meta = world.getMeta(x, y);

        String tex = this.texture;
        if(meta == 1){
            tex += ".left";
        }
        else if(meta == 2){
            tex += ".right";
        }
        else if(meta == 3){
            tex += ".back";
        }

        manager.getImage(tex).draw(renderX, renderY, 1F, 1F, filter);
    }
}
