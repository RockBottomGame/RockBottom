package de.ellpeck.rockbottom.api.render.tile;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.tile.MultiTile;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.api.world.IWorld;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.HashMap;
import java.util.Map;

public class MultiTileRenderer<T extends MultiTile> extends DefaultTileRenderer<T>{

    protected final IResourceName texItem;
    protected final Map<Pos2, IResourceName> textures = new HashMap<>();

    public MultiTileRenderer(IResourceName texture, MultiTile tile){
        super(texture);
        this.texItem = this.texture.addSuffix(".item");

        for(int x = 0; x < tile.getWidth(); x++){
            for(int y = 0; y < tile.getHeight(); y++){
                if(tile.isStructurePart(x, y)){
                    this.textures.put(new Pos2(x, y), this.texture.addSuffix("."+x+"."+y));
                }
            }
        }
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, Graphics g, IWorld world, T tile, int x, int y, float renderX, float renderY, Color filter){
        Pos2 innerCoord = tile.getInnerCoord(world.getMeta(x, y));
        manager.getImage(this.textures.get(innerCoord)).draw(renderX, renderY, 1F, 1F, filter);
    }

    @Override
    public Image getParticleTexture(IGameInstance game, IAssetManager manager, Graphics g, T tile, int meta){
        Pos2 innerCoord = tile.getInnerCoord(meta);
        return manager.getImage(this.textures.get(innerCoord));
    }

    @Override
    public void renderItem(IGameInstance game, IAssetManager manager, Graphics g, T tile, int meta, float x, float y, float scale, Color filter){
        manager.getImage(this.texItem).draw(x, y, scale, scale, filter);
    }
}
