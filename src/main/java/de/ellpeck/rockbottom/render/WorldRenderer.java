package de.ellpeck.rockbottom.render;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.IShaderProgram;
import de.ellpeck.rockbottom.api.assets.texture.ITexture;
import de.ellpeck.rockbottom.api.entity.Entity;
import de.ellpeck.rockbottom.api.entity.EntityLiving;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.event.impl.WorldRenderEvent;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.render.engine.TextureBank;
import de.ellpeck.rockbottom.api.render.entity.IEntityRenderer;
import de.ellpeck.rockbottom.api.render.tile.ITileRenderer;
import de.ellpeck.rockbottom.api.tile.Tile;
import de.ellpeck.rockbottom.api.tile.state.TileState;
import de.ellpeck.rockbottom.api.util.BoundBox;
import de.ellpeck.rockbottom.api.util.Colors;
import de.ellpeck.rockbottom.api.util.Pos2;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.biome.Biome;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.particle.ParticleManager;
import de.ellpeck.rockbottom.world.AbstractWorld;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.world.entity.player.InteractionManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class WorldRenderer {

    public static final int[] SKY_COLORS = new int[256];
    public static final int[] MAIN_COLORS = new int[Constants.MAX_LIGHT + 1];
    private static final ResourceName SUN_RES = ResourceName.intern("sky.sun");
    private static final ResourceName MOON_RES = ResourceName.intern("sky.moon");
    private static final ResourceName[] CLOUD_TEXTURES = new ResourceName[12];
    private final List<Pos2> starMap = new ArrayList<>();
    private final List<Cloud> clouds = new ArrayList<>();
    private final Random random = new Random();

    private float transX;
    private float transY;
    private int minChunkX;
    private int minChunkY;
    private int maxChunkX;
    private int maxChunkY;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;

    public WorldRenderer() {
        float step = 1F / Constants.MAX_LIGHT;
        for (int i = 0; i <= Constants.MAX_LIGHT; i++) {
            float modifier = i * step;
            MAIN_COLORS[i] = Colors.rgb(modifier, modifier, modifier, 1F);
        }

        int sky = 0x4C8DFF;
        for (int i = 0; i < SKY_COLORS.length; i++) {
            float percent = (float) i / (float) SKY_COLORS.length;
            SKY_COLORS[i] = Colors.multiply(sky, percent);
        }

        for (int i = 0; i < CLOUD_TEXTURES.length; i++) {
            CLOUD_TEXTURES[i] = ResourceName.intern("sky.cloud." + i);
        }

        this.addClouds(Util.RANDOM.nextInt(5) + 3, true);
    }

    public void calcCameraValues(IRenderer g) {
        double width = g.getWidthInWorld();
        double height = g.getHeightInWorld();

        this.transX = (float) (g.getCameraX() - width / 2);
        this.transY = (float) (-g.getCameraY() - height / 2);

        int topLeftX = Util.toGridPos(this.transX);
        int topLeftY = Util.toGridPos(-this.transY + 1);
        int bottomRightX = Util.toGridPos(this.transX + width);
        int bottomRightY = Util.toGridPos(-this.transY - height);

        this.minChunkX = Math.min(topLeftX, bottomRightX);
        this.minChunkY = Math.min(topLeftY, bottomRightY);
        this.maxChunkX = Math.max(topLeftX, bottomRightX);
        this.maxChunkY = Math.max(topLeftY, bottomRightY);

        int offset = g.isCullingDebug() ? 2 : 0;
        this.minX = Util.floor(this.transX) + offset;
        this.minY = Util.floor(-this.transY - g.getHeightInWorld() + 1) + offset;
        this.maxX = Util.ceil(this.transX + g.getWidthInWorld()) - offset;
        this.maxY = Util.ceil(-this.transY + 1) - offset;
    }

    public void render(IGameInstance game, IAssetManager manager, ParticleManager particles, IRenderer g, AbstractWorld world, EntityPlayer player, InteractionManager input) {
        float scale = g.getWorldScale();

        this.renderSky(game, manager, g, world, player, g.getWidthInWorld(), g.getHeightInWorld());

        List<Entity> entities = new ArrayList<>();
        List<EntityPlayer> players = new ArrayList<>();

        for (int gridY = this.minChunkY; gridY <= this.maxChunkY; gridY++) {
            for (int gridX = this.minChunkX; gridX <= this.maxChunkX; gridX++) {
                if (world.isChunkLoaded(gridX, gridY)) {
                    IChunk chunk = world.getChunkFromGridCoords(gridX, gridY);
                    this.renderChunk(game, manager, g, input, world, chunk, scale, chunk.getLoadedLayers(), false);

                    for (Entity entity : chunk.getAllEntities()) {
                        entities.add(entity);

                        if (entity instanceof EntityPlayer) {
                            players.add((EntityPlayer) entity);
                        }
                    }
                }
            }
        }
        g.setProgram(null);

        g.setScale(scale, scale);

        entities.stream().sorted(Comparator.comparingInt(Entity::getRenderPriority)).forEach(entity -> {
            if (entity.shouldRender()) {
                IEntityRenderer renderer = entity.getRenderer();
                if (renderer != null) {
                    double x = entity.getLerpedX();
                    double y = entity.getLerpedY();

                    if (game.getGuiManager().getGui() != null && game.getGuiManager().getGui().doesPauseGame()) {
                        x = entity.getX();
                        y = entity.getY();
                    }

                    if (x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY) {
                        ResourceName program = renderer.getRenderShader(game, manager, g, world, entity);
                        g.setProgram(program == null ? null : manager.getShaderProgram(program));

                        int light = world.getCombinedVisualLight(Util.floor(x), Util.floor(y));
                        int color = RockBottomAPI.getApiHandler().getColorByLight(light, TileLayer.MAIN);

                        if (entity instanceof EntityLiving) {
                            EntityLiving living = (EntityLiving) entity;

                            float damagePercentage;
                            float fadePercentage;

                            if (living.isDead() || living.getHealth() <= 0) {
                                damagePercentage = 1F;
                                fadePercentage = 1F - living.deathTimer / (float) living.getDeathLingerTime();
                            } else {
                                damagePercentage = 1F - (world.getTotalTime() - living.lastDamageTime) / 20F;
                                fadePercentage = 1F;
                            }

                            if (damagePercentage > 0F) {
                                color = Colors.lerp(color, Colors.RED, damagePercentage);
                            }

                            if (fadePercentage < 1F) {
                                color = Colors.multiplyA(color, fadePercentage);
                            }
                        }

                        renderer.render(game, manager, g, world, entity, (float) x - this.transX, (float) -y - this.transY + 1F, color);

                        if (g.isBoundBoxDebug()) {
                            g.addFilledRect((float) x - this.transX - 0.1F, (float) -y - this.transY + 0.9F, 0.2F, 0.2F, Colors.GREEN);

                            BoundBox box = entity.currentBounds;
                            g.addEmptyRect((float) box.getMinX() - this.transX, (float) -box.getMaxY() - this.transY + 1F, (float) box.getWidth(), (float) box.getHeight(), 0.1F, Colors.RED);

                            BoundBox boxMotion = box.copy().add(entity.motionX, entity.motionY);
                            g.addEmptyRect((float) boxMotion.getMinX() - this.transX, (float) -boxMotion.getMaxY() - this.transY + 1F, (float) boxMotion.getWidth(), (float) boxMotion.getHeight(), 0.05F, Colors.YELLOW);
                        }
                    }
                }
            }
        });
        g.setProgram(null);

        particles.render(game, manager, g, world, this.transX, this.transY);
        g.setProgram(null);

        RockBottomAPI.getEventHandler().fireEvent(new WorldRenderEvent(game, manager, g, world, player, this.transX, this.transY));

        players.forEach(entity -> {
            if (entity.shouldRender() && !entity.isLocalPlayer()) {
                manager.getFont().drawCenteredString((float) entity.getLerpedX() - this.transX, (float) -entity.getLerpedY() - this.transY - 0.75F, entity.getChatColorFormat() + entity.getName(), 0.015F, false);
            }
        });

        g.setScale(1F, 1F);

        for (int gridY = this.minChunkY; gridY <= this.maxChunkY; gridY++) {
            for (int gridX = this.minChunkX; gridX <= this.maxChunkX; gridX++) {
                if (world.isChunkLoaded(gridX, gridY)) {
                    IChunk chunk = world.getChunkFromGridCoords(gridX, gridY);
                    this.renderChunk(game, manager, g, input, world, chunk, scale, chunk.getLoadedLayers(), true);
                }
            }
        }
        g.setProgram(null);

        Biome biome = world.getBiome(player.chunkX, player.chunkY);
        biome.renderForeground(game, manager, g, world, player, scale);

        boolean chunkDebug = g.isChunkBorderDebug();
        boolean heightDebug = g.isHeightDebug();
        boolean biomeDebug = g.isBiomeDebug();

        if (chunkDebug || heightDebug || biomeDebug) {
            g.setScale(scale, scale);

            for (int gridX = this.minChunkX; gridX <= this.maxChunkX; gridX++) {
                for (int gridY = this.minChunkY; gridY <= this.maxChunkY; gridY++) {
                    if (world.isChunkLoaded(gridX, gridY)) {
                        int worldX = Util.toWorldPos(gridX);
                        int worldY = Util.toWorldPos(gridY);

                        if (chunkDebug) {
                            g.addEmptyRect(worldX - this.transX, -worldY - this.transY + 1F - Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, Constants.CHUNK_SIZE, 0.1F, Colors.GREEN);
                        }

                        if (heightDebug || biomeDebug) {
                            IChunk chunk = world.getChunkFromGridCoords(gridX, gridY);
                            for (int x = 0; x < Constants.CHUNK_SIZE; x++) {
                                if (heightDebug) {
                                    for (TileLayer layer : TileLayer.getLayersByRenderPrio()) {
                                        this.random.setSeed(layer.getName().hashCode());
                                        g.addFilledRect(worldX - this.transX + x, -worldY - this.transY + 1F - chunk.getHeightInner(layer, x), 1F, 0.1F, Colors.random(this.random));
                                    }
                                }

                                if (biomeDebug) {
                                    for (int y = 0; y < Constants.CHUNK_SIZE; y++) {
                                        if (!world.isClient()) {
                                            this.random.setSeed(chunk.getExpectedBiomeLevel(worldX + x, worldY + y).getName().hashCode());
                                            g.addFilledRect(worldX - this.transX + x + 0.35F, -worldY - this.transY - y + 0.35F, 0.3F, 0.3F, Colors.random(this.random));
                                        }
                                        this.random.setSeed(chunk.getBiomeInner(x, y).getName().hashCode());
                                        g.addEmptyRect(worldX - this.transX + x + 0.25F, -worldY - this.transY - y + 0.25F, 0.5F, 0.5F, 0.1F, Colors.random(this.random));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            g.setScale(1F, 1F);
        }
    }

    private void renderChunk(IGameInstance game, IAssetManager manager, IRenderer g, InteractionManager input, IWorld world, IChunk chunk, float scale, List<TileLayer> layers, boolean foreground) {
        int chunkX = chunk.getX();
        int chunkY = chunk.getY();

        int startX = Math.max(this.minX, chunkX);
        int startY = Math.max(this.minY, chunkY);
        int endX = Math.min(this.maxX, chunkX + Constants.CHUNK_SIZE);
        int endY = Math.min(this.maxY, chunkY + Constants.CHUNK_SIZE);

        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                int[] light = RockBottomAPI.getApiHandler().interpolateLight(world, x, y);

                int obscuringLayer = -1;

                for (int i = layers.size() - 1; i >= 0; i--) {
                    TileLayer layer = layers.get(i);
                    TileState state = chunk.getState(layer, x, y);
                    if (state.getTile().obscuresBackground(world, x, y, layer)) {
                        obscuringLayer = i;
                    }
                }

                for (int i = obscuringLayer >= 0 ? obscuringLayer : layers.size() - 1; i >= 0; i--) {
                    TileLayer layer = layers.get(i);
                    int[] color = RockBottomAPI.getApiHandler().interpolateWorldColor(light, layer);

                    float fade = chunk.getFadePercentage();
                    if (fade < 1F) {
                        for (int c = 0; c < color.length; c++) {
                            color[c] = Colors.multiplyA(color[c], fade);
                        }
                    }

                    this.renderLayer(game, manager, g, input, world, chunk, layer, x, y, scale, color, foreground);
                }
            }
        }
    }

    private void renderLayer(IGameInstance game, IAssetManager manager, IRenderer g, InteractionManager input, IWorld world, IChunk chunk, TileLayer layer, int x, int y, float scale, int[] color, boolean foreground) {
        if (layer.isVisible(game, game.getPlayer(), chunk, x, y, foreground)) {
            TileState state = chunk.getState(layer, x, y);
            Tile tile = state.getTile();
            ITileRenderer renderer = tile.getRenderer();
            boolean forcesForeground = layer.forceForegroundRender();

            if (renderer != null) {
                if (foreground) {
                    this.renderTile(game, manager, g, input, world, layer, state, tile, renderer, x, y, scale, color, forcesForeground, true);
                } else if (!forcesForeground) {
                    this.renderTile(game, manager, g, input, world, layer, state, tile, renderer, x, y, scale, color, true, false);
                }
            }
        }
    }

    private void renderTile(IGameInstance game, IAssetManager manager, IRenderer g, InteractionManager input, IWorld world, TileLayer layer, TileState state, Tile tile, ITileRenderer renderer, int x, int y, float scale, int[] color, boolean renderNormal, boolean renderForeground) {
        boolean isBreakTile = input.breakingLayer == layer && input.breakProgress > 0 && x == input.breakTileX && y == input.breakTileY;

        if (isBreakTile) {
            IShaderProgram program = manager.getShaderProgram(IShaderProgram.BREAK_SHADER);
            g.setProgram(program);
        } else {
            ResourceName program = renderer.getRenderShader(game, manager, g, world, tile, state, x, y, layer);
            g.setProgram(program == null ? null : manager.getShaderProgram(program));
        }

        if (renderNormal) {
            renderer.render(game, manager, g, world, tile, state, x, y, layer, (x - this.transX) * scale, (-y - this.transY) * scale, scale, color);
        }

        if (renderForeground) {
            renderer.renderInForeground(game, manager, g, world, tile, state, x, y, layer, (x - this.transX) * scale, (-y - this.transY) * scale, scale, color);
        }

        if (isBreakTile) {
            ITexture tex = manager.getTexture(ResourceName.intern("break." + Util.ceil(input.breakProgress * 8F)));
            tex.bind(TextureBank.BANK_2, true);
        }
    }

    private void renderSky(IGameInstance game, IAssetManager manager, IRenderer g, AbstractWorld world, AbstractEntityPlayer player, double width, double height) {
        if (!world.renderSky(game, manager, g, world, player, width, height)) {
            return;
        }

        Biome biome = world.getBiome(player.chunkX, player.chunkY);

        if (!biome.renderBackground(game, manager, g, world, player, width, height)) {
            return;
        }

        // Sky Color
        float skylightMod = world.getSkylightModifier(false);

        int skyLight = (int) (skylightMod * (SKY_COLORS.length - 1));
        int skyColor = biome.getSkyColor(SKY_COLORS[skyLight]);
        g.backgroundColor(skyColor);

        // Scale
        float scale = g.getWorldScale();
        g.setScale(scale, scale);

        int time = world.getCurrentTime();
        float worldScale = game.getSettings().renderScale;

        // Stars
        float starAlpha = 1F - Math.min(1F, skylightMod + 0.5F);
        if (starAlpha <= 0F) {
            if (!this.starMap.isEmpty()) {
                this.starMap.clear();
            }
        } else {
            if (this.starMap.isEmpty()) {
                for (int i = 0; i < Util.RANDOM.nextInt(50) + 30; i++) {
                    this.starMap.add(new Pos2(Util.RANDOM.nextInt(101), Util.RANDOM.nextInt(101)));
                }
            }

            int starColor = Colors.multiplyA(Colors.WHITE, starAlpha);
            for (Pos2 pos : this.starMap) {
                this.random.setSeed(Util.scrambleSeed(pos.getX(), pos.getY()));
                float mod = ((float) Math.sin((world.getTotalTime() + this.random.nextFloat() * 500) / 80D % (2 * Math.PI)) + 1F) / 2F;

                g.addFilledRect((float) ((pos.getX() / 100D) * width), (float) ((pos.getY() / 100D) * height), 0.1F, 0.1F, Colors.multiplyA(starColor, mod));
            }
        }

        double radiusX = 10D / worldScale;
        double radiusY = 7D / worldScale;

        // Sun
        double sunAngle = (time / (double) Constants.TIME_PER_DAY) * 360D + 180D;
        double sunRads = Math.toRadians(sunAngle);
        float sunX = (float) (width / 2D + Math.cos(sunRads) * radiusX);
        float sunY = (float) (height + Math.sin(sunRads) * radiusY);
        manager.getTexture(SUN_RES).draw(sunX - 2F, sunY - 2F, 4F, 4F);

        // Moon
        double moonAngle = (time / (double) Constants.TIME_PER_DAY) * 360D;
        double moonRads = Math.toRadians(moonAngle);
        float moonX = (float) (width / 2D + Math.cos(moonRads) * radiusX);
        float moonY = (float) (height + Math.sin(moonRads) * radiusY);
        manager.getTexture(MOON_RES).draw(moonX - 2F, moonY - 2F, 4F, 4F);

        // Clouds
        float yOff = (float) player.getY() * 0.025F;
        for (Cloud cloud : this.clouds) {
            cloud.render(manager, width, height, skylightMod, yOff);
        }

        g.setScale(1F, 1F);
    }

    public void update(IWorld world, IParticleManager manager) {
        int possibleAddAmount = 0;

        for (int i = this.clouds.size() - 1; i >= 0; i--) {
            Cloud cloud = this.clouds.get(i);
            cloud.x += cloud.speed;

            if (cloud.x >= 1.5D) {
                this.clouds.remove(i);
                possibleAddAmount += 2;
            }
        }

        if (this.clouds.size() < 15) {
            if (this.clouds.size() <= 0) {
                possibleAddAmount += 5;
            } else if (possibleAddAmount <= 0 && Util.RANDOM.nextFloat() >= 0.9F) {
                possibleAddAmount = Util.RANDOM.nextInt(5);
            }

            if (possibleAddAmount > 0) {
                this.addClouds(Util.RANDOM.nextInt(possibleAddAmount) + 1, false);
            }
        }

        this.doRandomTileRenderUpdates(world, manager);
    }

    private void addClouds(int amount, boolean randomX) {
        for (int i = 0; i < amount; i++) {
            this.clouds.add(new Cloud(Util.RANDOM.nextDouble() * 0.001D, randomX ? Util.RANDOM.nextDouble() : -0.5D, Util.RANDOM.nextDouble() * 0.3D));
        }
    }

    private void doRandomTileRenderUpdates(IWorld world, IParticleManager manager) {
        int tileAmount = (this.maxX - this.minX) * (this.maxY - this.minY);
        int toUpdate = Util.floor(tileAmount * Constants.RANDOM_TILE_RENDER_UPDATES);

        List<TileLayer> layers = TileLayer.getAllLayers();
        for (int i = 0; i < toUpdate * layers.size(); i++) {
            TileLayer layer = layers.get(Util.RANDOM.nextInt(layers.size()));
            int x = Util.RANDOM.nextInt(this.maxX - this.minX) + this.minX;
            int y = Util.RANDOM.nextInt(this.maxY - this.minY) + this.minY;

            TileState state = world.getState(layer, x, y);
            state.getTile().updateRandomlyInPlayerView(world, x, y, layer, state, manager);
        }
    }

    private static class Cloud {

        private final double speed;
        private final double y;
        private final int[] cloudParts;
        private final Pos2[] cloudOffsets;
        protected double x;

        private Cloud(double speed, double x, double y) {
            this.speed = speed;
            this.x = x;
            this.y = y;

            this.cloudParts = new int[Util.RANDOM.nextInt(4) + 3];
            this.cloudOffsets = new Pos2[this.cloudParts.length];

            for (int i = 0; i < this.cloudParts.length; i++) {
                this.cloudParts[i] = Util.RANDOM.nextInt(12);
                this.cloudOffsets[i] = new Pos2(Util.RANDOM.nextInt(17) - 8, Util.RANDOM.nextInt(5) - 2);
            }
        }

        private void render(IAssetManager manager, double width, double height, float lightModifier, float yOffset) {
            for (int i = 0; i < this.cloudParts.length; i++) {
                int part = this.cloudParts[i];
                Pos2 offset = this.cloudOffsets[i];

                manager.getTexture(CLOUD_TEXTURES[part]).draw((float) (this.x * width) + offset.getX() * 0.1F, (float) (this.y * height) + offset.getY() * 0.1F + yOffset, 1F, 1F, Colors.multiplyA(Colors.multiply(Colors.WHITE, lightModifier), 0.75F));
            }
        }
    }
}
