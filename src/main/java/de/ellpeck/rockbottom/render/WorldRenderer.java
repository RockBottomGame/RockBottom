package de.ellpeck.rockbottom.render;

import de.ellpeck.rockbottom.api.*;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.ITexture;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.event.impl.WorldRenderEvent;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.particle.ParticleManager;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class WorldRenderer{

    public static final int[] SKY_COLORS = new int[256];
    public static final int[] MAIN_COLORS = new int[Constants.MAX_LIGHT+1];

    public static void init(){
        float step = 1F/(Constants.MAX_LIGHT+1);
        for(int i = 0; i <= Constants.MAX_LIGHT; i++){
            float modifier = i*step;
            MAIN_COLORS[i] = Colors.rgb(modifier, modifier, modifier, 1F);
        }

        int sky = 0x4C8DFF;
        for(int i = 0; i < SKY_COLORS.length; i++){
            float percent = (float)i/(float)SKY_COLORS.length;
            SKY_COLORS[i] = Colors.multiply(sky, percent);
        }
    }

    public void render(IGameInstance game, IAssetManager manager, ParticleManager particles, IGraphics g, World world, EntityPlayer player, InteractionManager input){
        float scale = game.getWorldScale();

        int skyLight = (int)(world.getSkylightModifier()*(SKY_COLORS.length-1));
        int color = SKY_COLORS[game.isLightDebug() ? SKY_COLORS.length-1 : skyLight];
        g.backgroundColor(color);

        double width = game.getWidthInWorld();
        double height = game.getHeightInWorld();
        float transX = (float)(player.x-width/2);
        float transY = (float)(-player.y-height/2);

        int topLeftX = Util.toGridPos(transX);
        int topLeftY = Util.toGridPos(-transY+1);
        int bottomRightX = Util.toGridPos(transX+width);
        int bottomRightY = Util.toGridPos(-transY-height);

        int minX = Math.min(topLeftX, bottomRightX);
        int minY = Math.min(topLeftY, bottomRightY);
        int maxX = Math.max(topLeftX, bottomRightX);
        int maxY = Math.max(topLeftY, bottomRightY);

        List<Entity> entities = new ArrayList<>();
        List<EntityPlayer> players = new ArrayList<>();

        List<TileLayer> layers = new ArrayList<>(TileLayer.getAllLayers());
        layers.sort(Comparator.comparingInt(TileLayer:: getRenderPriority).reversed());

        for(int gridX = minX; gridX <= maxX; gridX++){
            for(int gridY = minY; gridY <= maxY; gridY++){
                if(world.isChunkLoaded(gridX, gridY)){
                    IChunk chunk = world.getChunkFromGridCoords(gridX, gridY);
                    this.renderChunk(game, manager, g, input, world, chunk, transX, transY, scale, layers, false);

                    for(Entity entity : chunk.getAllEntities()){
                        entities.add(entity);

                        if(entity instanceof EntityPlayer){
                            players.add((EntityPlayer)entity);
                        }
                    }
                }
            }
        }

        g.pushMatrix();
        g.scale(game.getWorldScale(), game.getWorldScale());

        entities.stream().sorted(Comparator.comparingInt(Entity:: getRenderPriority)).forEach(entity -> {
            if(entity.shouldRender()){
                IEntityRenderer renderer = entity.getRenderer();
                if(renderer != null){
                    int light = world.getCombinedLight(Util.floor(entity.x), Util.floor(entity.y));
                    renderer.render(game, manager, g, world, entity, (float)entity.x-transX, (float)-entity.y-transY+1F, RockBottomAPI.getApiHandler().getColorByLight(light, TileLayer.MAIN));
                }
            }
        });

        particles.render(game, manager, g, world, transX, transY);

        RockBottomAPI.getEventHandler().fireEvent(new WorldRenderEvent(game, manager, g, world, player, transX, transY));

        players.forEach(entity -> {
            if(entity.shouldRender() && !RockBottomAPI.getNet().isThePlayer(entity)){
                manager.getFont().drawCenteredString((float)entity.x-transX, (float)-entity.y-transY-1.25F, entity.getChatColorFormat()+entity.getName(), 0.015F, false);
            }
        });

        if(game.isChunkBorderDebug()){
            for(int gridX = minX; gridX <= maxX; gridX++){
                for(int gridY = minY; gridY <= maxY; gridY++){
                    if(world.isChunkLoaded(gridX, gridY)){
                        int x = Util.toWorldPos(gridX);
                        int y = Util.toWorldPos(gridY);

                        g.drawRect(x-transX, -y-transY+1F-Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, 1F/scale, Colors.GREEN);
                    }
                }
            }
        }

        g.popMatrix();

        for(int gridX = minX; gridX <= maxX; gridX++){
            for(int gridY = minY; gridY <= maxY; gridY++){
                if(world.isChunkLoaded(gridX, gridY)){
                    IChunk chunk = world.getChunkFromGridCoords(gridX, gridY);
                    this.renderChunk(game, manager, g, input, world, chunk, transX, transY, scale, layers, true);
                }
            }
        }

    }

    private void renderChunk(IGameInstance game, IAssetManager manager, IGraphics g, InteractionManager input, IWorld world, IChunk chunk, float transX, float transY, float scale, List<TileLayer> layers, boolean foreground){
        int chunkX = chunk.getX();
        int chunkY = chunk.getY();

        int startX = Util.clamp(Util.floor(transX), chunkX, chunkX+Constants.CHUNK_SIZE);
        int startY = Util.clamp(Util.floor(-transY-game.getHeightInWorld()+1), chunkY, chunkY+Constants.CHUNK_SIZE);
        int endX = Util.clamp(Util.ceil(transX+game.getWidthInWorld()), chunkX, chunkX+Constants.CHUNK_SIZE);
        int endY = Util.clamp(Util.ceil(-transY+1), chunkY, chunkY+Constants.CHUNK_SIZE);

        for(int x = startX; x < endX; x++){
            for(int y = startY; y < endY; y++){
                int[] light = RockBottomAPI.getApiHandler().interpolateLight(world, x, y);

                int obscuringLayer = -1;

                for(int i = layers.size()-1; i >= 0; i--){
                    TileState state = chunk.getState(layers.get(i), x, y);
                    if(state.getTile().obscuresBackground()){
                        obscuringLayer = i;
                    }
                }

                for(int i = obscuringLayer >= 0 ? obscuringLayer : layers.size()-1; i >= 0; i--){
                    this.renderLayer(game, manager, g, input, world, chunk, layers.get(i), x, y, transX, transY, scale, light, foreground);
                }
            }
        }
    }

    private void renderLayer(IGameInstance game, IAssetManager manager, IGraphics g, InteractionManager input, IWorld world, IChunk chunk, TileLayer layer, int x, int y, float transX, float transY, float scale, int[] light, boolean foreground){
        IApiHandler api = RockBottomAPI.getApiHandler();
        TileState state = chunk.getState(layer, x, y);
        Tile tile = state.getTile();
        ITileRenderer renderer = tile.getRenderer();
        boolean forcesForeground = layer.forceForegroundRender();

        if(renderer != null){
            if(foreground){
                if(forcesForeground){
                    renderer.render(game, manager, g, world, tile, state, x, y, layer, (x-transX)*scale, (-y-transY)*scale, scale, api.interpolateWorldColor(light, layer));

                    if(input.breakingLayer == layer){
                        this.doBreakAnimation(input, manager, x, y, transX, transY, scale);
                    }
                }

                renderer.renderInForeground(game, manager, g, world, tile, state, x, y, layer, (x-transX)*scale, (-y-transY)*scale, scale, api.interpolateWorldColor(light, layer));
            }
            else if(!forcesForeground){
                renderer.render(game, manager, g, world, tile, state, x, y, layer, (x-transX)*scale, (-y-transY)*scale, scale, api.interpolateWorldColor(light, layer));

                if(input.breakingLayer == layer){
                    this.doBreakAnimation(input, manager, x, y, transX, transY, scale);
                }
            }
        }
    }

    private void doBreakAnimation(InteractionManager input, IAssetManager manager, int tileX, int tileY, float transX, float transY, float scale){
        if(input.breakProgress > 0){
            if(tileX == input.breakTileX && tileY == input.breakTileY){
                ITexture brk = manager.getTexture(RockBottomAPI.createInternalRes("break."+Util.ceil(input.breakProgress*8F)));
                brk.draw((tileX-transX)*scale, (-tileY-transY)*scale, scale, scale);
            }
        }
    }
}
