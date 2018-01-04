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
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
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

    private static final IResourceName SUN_RES = RockBottomAPI.createInternalRes("sky.sun");
    private static final IResourceName MOON_RES = RockBottomAPI.createInternalRes("sky.moon");
    private static final IResourceName[] CLOUD_TEXTURES = new IResourceName[12];
    public static final int[] SKY_COLORS = new int[256];
    public static final int[] MAIN_COLORS = new int[Constants.MAX_LIGHT+1];
    private final List<Pos2> starMap = new ArrayList<>();
    private final List<Cloud> clouds = new ArrayList<>();

    public WorldRenderer(){
        float step = 1F/Constants.MAX_LIGHT;
        for(int i = 0; i <= Constants.MAX_LIGHT; i++){
            float modifier = i*step;
            MAIN_COLORS[i] = Colors.rgb(modifier, modifier, modifier, 1F);
        }

        int sky = 0x4C8DFF;
        for(int i = 0; i < SKY_COLORS.length; i++){
            float percent = (float)i/(float)SKY_COLORS.length;
            SKY_COLORS[i] = Colors.multiply(sky, percent);
        }

        for(int i = 0; i < CLOUD_TEXTURES.length; i++){
            CLOUD_TEXTURES[i] = RockBottomAPI.createInternalRes("sky.cloud."+i);
        }

        this.addClouds(Util.RANDOM.nextInt(5)+3, true);
    }

    public void render(IGameInstance game, IAssetManager manager, ParticleManager particles, IGraphics g, World world, EntityPlayer player, InteractionManager input){
        float scale = g.getWorldScale();
        float skylightMod = world.getSkylightModifier(false);

        int skyLight = (int)(skylightMod*(SKY_COLORS.length-1));
        int color = SKY_COLORS[skyLight];
        g.backgroundColor(color);

        double width = g.getWidthInWorld();
        double height = g.getHeightInWorld();
        float transX = (float)(player.x-width/2);
        float transY = (float)(-player.y-height/2);

        this.renderSky(game, manager, g, world, skylightMod, width, height);

        int topLeftX = Util.toGridPos(transX);
        int topLeftY = Util.toGridPos(-transY+1);
        int bottomRightX = Util.toGridPos(transX+width);
        int bottomRightY = Util.toGridPos(-transY-height);

        int minX = Math.min(topLeftX, bottomRightX);
        int minY = Math.min(topLeftY, bottomRightY);
        int maxX = Math.max(topLeftX, bottomRightX);
        int maxY = Math.max(topLeftY, bottomRightY);

        List<TileLayer> layers = TileLayer.getLayersByRenderPrio();
        List<Entity> entities = new ArrayList<>();
        List<EntityPlayer> players = new ArrayList<>();

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
        g.scale(g.getWorldScale(), g.getWorldScale());

        entities.stream().sorted(Comparator.comparingInt(Entity:: getRenderPriority)).forEach(entity -> {
            if(entity.shouldRender()){
                IEntityRenderer renderer = entity.getRenderer();
                if(renderer != null){
                    int light = world.getCombinedVisualLight(Util.floor(entity.x), Util.floor(entity.y));
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

        if(g.isChunkBorderDebug()){
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

        int startX = Math.max(Util.floor(transX), chunkX);
        int startY = Math.max(Util.floor(-transY-g.getHeightInWorld()+1), chunkY);
        int endX = Math.min(Util.ceil(transX+g.getWidthInWorld()), chunkX+Constants.CHUNK_SIZE);
        int endY = Math.min(Util.ceil(-transY+1), chunkY+Constants.CHUNK_SIZE);

        for(int x = startX; x < endX; x++){
            for(int y = startY; y < endY; y++){
                int[] light = RockBottomAPI.getApiHandler().interpolateLight(world, x, y);

                int obscuringLayer = -1;

                for(int i = layers.size()-1; i >= 0; i--){
                    TileLayer layer = layers.get(i);
                    TileState state = chunk.getState(layer, x, y);
                    if(state.getTile().obscuresBackground(world, x, y, layer)){
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
        if(layer.isVisible(game, game.getPlayer(), chunk, x, y, foreground)){
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
                            this.doBreakAnimation(input, manager, world, x, y, state, layer, transX, transY, scale);
                        }
                    }

                    renderer.renderInForeground(game, manager, g, world, tile, state, x, y, layer, (x-transX)*scale, (-y-transY)*scale, scale, api.interpolateWorldColor(light, layer));
                }
                else if(!forcesForeground){
                    renderer.render(game, manager, g, world, tile, state, x, y, layer, (x-transX)*scale, (-y-transY)*scale, scale, api.interpolateWorldColor(light, layer));

                    if(input.breakingLayer == layer){
                        this.doBreakAnimation(input, manager, world, x, y, state, layer, transX, transY, scale);
                    }
                }
            }
        }
    }

    private void doBreakAnimation(InteractionManager input, IAssetManager manager, IWorld world, int tileX, int tileY, TileState state, TileLayer layer, float transX, float transY, float scale){
        if(input.breakProgress > 0){
            if(tileX == input.breakTileX && tileY == input.breakTileY){
                if(state.getTile().shouldShowBreakAnimation(world, tileX, tileY, layer)){
                    ITexture brk = manager.getTexture(RockBottomAPI.createInternalRes("break."+Util.ceil(input.breakProgress*8F)));
                    brk.draw((tileX-transX)*scale, (-tileY-transY)*scale, scale, scale);
                }
            }
        }
    }

    private void renderSky(IGameInstance game, IAssetManager manager, IGraphics g, IWorld world, float skylightMod, double width, double height){
        g.pushMatrix();
        g.scale(g.getWorldScale(), g.getWorldScale());

        int time = world.getWorldInfo().currentWorldTime;
        float worldScale = game.getSettings().renderScale;

        float starAlpha = 1F-Math.min(1F, skylightMod+0.5F);
        if(starAlpha <= 0F){
            if(!this.starMap.isEmpty()){
                this.starMap.clear();
            }
        }
        else{
            if(this.starMap.isEmpty()){
                for(int i = 0; i < Util.RANDOM.nextInt(50)+30; i++){
                    this.starMap.add(new Pos2(Util.RANDOM.nextInt(101), Util.RANDOM.nextInt(101)));
                }
            }

            int starColor = Colors.multiplyA(Colors.WHITE, starAlpha);
            for(Pos2 pos : this.starMap){
                g.fillRect((float)((pos.getX()/100D)*width), (float)((pos.getY()/100D)*height), 0.1F, 0.1F, starColor);
            }
        }

        double radiusX = 10D/worldScale;
        double radiusY = 7D/worldScale;

        double sunAngle = (time/(double)Constants.TIME_PER_DAY)*360D+180D;
        double sunRads = Math.toRadians(sunAngle);
        float sunX = (float)(width/2D+Math.cos(sunRads)*radiusX);
        float sunY = (float)(height+Math.sin(sunRads)*radiusY);
        manager.getTexture(SUN_RES).draw(sunX-2F, sunY-2F, 4F, 4F);

        double moonAngle = (time/(double)Constants.TIME_PER_DAY)*360D;
        double moonRads = Math.toRadians(moonAngle);
        float moonX = (float)(width/2D+Math.cos(moonRads)*radiusX);
        float moonY = (float)(height+Math.sin(moonRads)*radiusY);
        manager.getTexture(MOON_RES).draw(moonX-2F, moonY-2F, 4F, 4F);

        for(Cloud cloud : this.clouds){
            cloud.render(manager, width, height, skylightMod);
        }

        g.popMatrix();
    }

    public void update(){
        int possibleAddAmount = 0;

        for(int i = 0; i < this.clouds.size(); i++){
            Cloud cloud = this.clouds.get(i);
            cloud.x += cloud.speed;

            if(cloud.x >= 1.5D){
                this.clouds.remove(i);
                i--;

                possibleAddAmount += 2;
            }
        }

        if(this.clouds.size() < 15){
            if(this.clouds.size() <= 0){
                possibleAddAmount += 5;
            }
            else if(possibleAddAmount <= 0 && Util.RANDOM.nextFloat() >= 0.9F){
                possibleAddAmount = Util.RANDOM.nextInt(5);
            }

            if(possibleAddAmount > 0){
                this.addClouds(Util.RANDOM.nextInt(possibleAddAmount)+1, false);
            }
        }
    }

    private void addClouds(int amount, boolean randomX){
        for(int i = 0; i < amount; i++){
            this.clouds.add(new Cloud(Util.RANDOM.nextDouble()*0.001D, randomX ? Util.RANDOM.nextDouble() : -0.5D, Util.RANDOM.nextDouble()*0.3D));
        }
    }

    private static class Cloud{

        private final double speed;
        private final double y;
        private double x;

        private final int[] cloudParts;
        private final Pos2[] cloudOffsets;

        private Cloud(double speed, double x, double y){
            this.speed = speed;
            this.x = x;
            this.y = y;

            this.cloudParts = new int[Util.RANDOM.nextInt(4)+3];
            this.cloudOffsets = new Pos2[this.cloudParts.length];

            for(int i = 0; i < this.cloudParts.length; i++){
                this.cloudParts[i] = Util.RANDOM.nextInt(12);
                this.cloudOffsets[i] = new Pos2(Util.RANDOM.nextInt(17)-8, Util.RANDOM.nextInt(5)-2);
            }
        }

        private void render(IAssetManager manager, double width, double height, float lightModifier){
            for(int i = 0; i < this.cloudParts.length; i++){
                int part = this.cloudParts[i];
                Pos2 offset = this.cloudOffsets[i];

                manager.getTexture(CLOUD_TEXTURES[part]).draw((float)(this.x*width)+offset.getX()*0.1F, (float)(this.y*height)+offset.getY()*0.1F, 1F, 1F, Colors.multiplyA(Colors.multiply(Colors.WHITE, lightModifier), 0.75F));
            }
        }
    }
}
