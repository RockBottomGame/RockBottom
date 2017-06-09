package de.ellpeck.rockbottom.game.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.render.tile.DefaultTileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.game.RockBottom;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

public class TorchTileRenderer extends DefaultTileRenderer{

    private final IResourceName texLeft;
    private final IResourceName texRight;
    private final IResourceName texBack;

    public TorchTileRenderer(){
        super(RockBottom.internalRes("torch"));
        this.texLeft = this.texture.addSuffix(".left");
        this.texRight = this.texture.addSuffix(".right");
        this.texBack = this.texture.addSuffix(".back");
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, Tile tile, int x, int y, float renderX, float renderY, Color filter){
        int meta = world.getMeta(x, y);

        IResourceName tex;
        if(meta == 1){
            tex = this.texLeft;
        }
        else if(meta == 2){
            tex = this.texRight;
        }
        else if(meta == 3){
            tex = this.texBack;
        }
        else{
            tex = this.texture;
        }

        manager.getImage(tex).draw(renderX, renderY, 1F, 1F, filter);
    }
}
