package de.ellpeck.game.render;

import de.ellpeck.game.Constants;
import de.ellpeck.game.Game;
import de.ellpeck.game.assets.AssetManager;
import de.ellpeck.game.particle.ParticleManager;
import de.ellpeck.game.render.entity.IEntityRenderer;
import de.ellpeck.game.render.tile.ITileRenderer;
import de.ellpeck.game.util.MathUtil;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.TileLayer;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.Entity;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import de.ellpeck.game.world.entity.player.InteractionManager;
import de.ellpeck.game.world.tile.Tile;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldRenderer{

    private static final Color SKY_COLOR = new Color(0x4C8DFF);
    public static final Color[] BACKGROUND_COLORS = new Color[Constants.MAX_LIGHT+1];
    public static final Color[] MAIN_COLORS = new Color[Constants.MAX_LIGHT+1];

    static{
        float step = 1F/(Constants.MAX_LIGHT+1);
        for(int i = 0; i <= Constants.MAX_LIGHT; i++){
            float modifier = step+i*step;

            MAIN_COLORS[i] = new Color(modifier, modifier, modifier, 1F);
            BACKGROUND_COLORS[i] = new Color(modifier*0.5F, modifier*0.5F, modifier*0.5F, 1F);
        }
    }

    public void render(Game game, AssetManager manager, ParticleManager particles, Graphics g, World world, EntityPlayer player, InteractionManager input){
        g.scale(Constants.RENDER_SCALE, Constants.RENDER_SCALE);
        g.setBackground(SKY_COLOR);

        double width = game.getWidthInWorld();
        double height = game.getHeightInWorld();
        double worldAtScreenX = player.x-width/2;
        double worldAtScreenY = -player.y-height/2;

        g.translate((float)-worldAtScreenX, (float)-worldAtScreenY);

        List<Entity> entities = new ArrayList<>();

        for(int gridX = -Constants.CHUNK_RENDER_DISTANCE; gridX <= Constants.CHUNK_RENDER_DISTANCE; gridX++){
            for(int gridY = -Constants.CHUNK_RENDER_DISTANCE; gridY <= Constants.CHUNK_RENDER_DISTANCE; gridY++){
                Chunk chunk = world.getChunkFromGridCoords(player.chunkX+gridX, player.chunkY+gridY);

                for(int x = 0; x < Constants.CHUNK_SIZE; x++){
                    for(int y = 0; y < Constants.CHUNK_SIZE; y++){
                        int tileX = chunk.x+x;
                        int tileY = chunk.y+y;

                        if(tileX >= worldAtScreenX-1 && -tileY >= worldAtScreenY-1 && tileX < worldAtScreenX+width && -tileY < worldAtScreenY+height){
                            Tile tile = chunk.getTileInner(x, y);
                            byte light = chunk.getCombinedLightInner(x, y);

                            if(!tile.isFullTile()){
                                Tile tileBack = chunk.getTileInner(TileLayer.BACKGROUND, x, y);
                                ITileRenderer rendererBack = tileBack.getRenderer();
                                if(rendererBack != null){
                                    rendererBack.render(game, manager, g, world, tileBack, tileX, tileY, tileX, -tileY, BACKGROUND_COLORS[light]);

                                    if(input.breakingLayer == TileLayer.BACKGROUND){
                                        this.doBreakAnimation(input, manager, tileX, tileY);
                                    }
                                }
                            }

                            ITileRenderer renderer = tile.getRenderer();
                            if(renderer != null){
                                renderer.render(game, manager, g, world, tile, tileX, tileY, tileX, -tileY, MAIN_COLORS[light]);

                                if(input.breakingLayer == TileLayer.MAIN){
                                    this.doBreakAnimation(input, manager, tileX, tileY);
                                }
                            }
                        }
                    }
                }

                entities.addAll(chunk.getAllEntities());
            }
        }

        entities.stream().sorted(Comparator.comparingInt(Entity:: getRenderPriority)).forEach(entity -> {
            IEntityRenderer renderer = entity.getRenderer();
            if(renderer != null){
                int light = world.getCombinedLight(MathUtil.floor(entity.x), MathUtil.floor(entity.y));
                renderer.render(game, manager, g, world, entity, (float)entity.x, (float)-entity.y+1F, MAIN_COLORS[light]);
            }
        });

        particles.render(game, manager, g, world);

        g.resetTransform();
    }

    private void doBreakAnimation(InteractionManager input, AssetManager manager, int tileX, int tileY){
        if(input.breakProgress > 0){
            if(tileX == input.breakTileX && tileY == input.breakTileY){
                Image brk = manager.getImage("break."+MathUtil.ceil(input.breakProgress*8F));
                brk.draw(tileX, -tileY, 1F, 1F);
            }
        }
    }
}
