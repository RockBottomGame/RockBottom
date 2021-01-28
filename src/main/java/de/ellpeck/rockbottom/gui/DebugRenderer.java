package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.gen.biome.level.BiomeLevel;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.assets.sound.SoundHandler;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.util.CrashManager;
import de.ellpeck.rockbottom.world.AbstractWorld;
import de.ellpeck.rockbottom.world.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public final class DebugRenderer {

    public static void render(RockBottom game, IAssetManager manager, IWorld world, PlayerEntity player, IRenderer g) {
        List<String> list = getInfo(game, world, player, g);

        int y = 0;
        boolean right = false;

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (!s.isEmpty()) {
                if (right) {
                    manager.getFont().drawStringFromRight(game.getWidth() - 10F, 10F + y, s, 0.8F);
                } else {
                    manager.getFont().drawString(10F, 10F + y, s, 0.8F);
                }
            }

            y += 20;
            if (y >= game.getHeight() - 20) {
                y = 0;
                right = true;
            }
        }
    }

    public static List<String> getInfo(RockBottom game, IWorld world, PlayerEntity player, IRenderer g) {
        List<String> list = new ArrayList<>();

        Runtime runtime = Runtime.getRuntime();
        long total = runtime.totalMemory();
        list.add("Used Memory: " + CrashManager.displayByteCount(total - runtime.freeMemory()));
        list.add("Reserved Memory: " + CrashManager.displayByteCount(total));
        list.add("Allocated Memory: " + CrashManager.displayByteCount(runtime.maxMemory()));

        list.add("");

        list.add("Avg FPS: " + game.getFpsAverage());
        list.add("Avg TPS: " + game.getTpsAverage());
        list.add("Aspect: " + game.getWidth() + ", " + game.getHeight());
        list.add("Display Ratio: " + g.getDisplayRatio());
        list.add("Gui Scale: " + g.getGuiScale());
        list.add("World Scale: " + g.getWorldScale());
        list.add("");
        list.add("Free Sound Sources: " + SoundHandler.getFreeSources());
        list.add("Playing Sounds: " + SoundHandler.getPlayingSoundAmount());
        list.add("Streaming Sounds: " + SoundHandler.getStreamingSoundAmount());
        list.add("");

        list.add("Texture Binds: " + Texture.binds);
        list.add("Renderer Flushes: " + g.getFlushes());

        if (world != null && player != null) {
            list.add("");
            list.add("Current world: " + world.getName());
            if (!world.isClient()) {
                list.add("Sub world amount: " + world.getSubWorlds().size());
            }
            list.add("");

            String chunks = "Loaded Chunks: " + ((AbstractWorld) world).loadedChunks.size();
            if (!RockBottomAPI.getNet().isClient()) {
                chunks += ", PlayerChunks: " + player.getChunksInRange().size();
            }
            list.add(chunks);

            list.add("Entities: " + world.getAllEntities().size());
            list.add("Players: " + world.getAllPlayers().size());
            list.add("TileEntities: " + world.getAllTileEntities().size() + ", Ticking: " + world.getAllTickingTileEntities().size());
            list.add("Particles: " + game.getParticleManager().getAmount());
            int scheduledTileAmount = 0;
            for (IChunk chunk : ((AbstractWorld) world).loadedChunks) {
                scheduledTileAmount += chunk.getScheduledUpdateAmount();
            }
            list.add("Scheduled Tile Updates: " + scheduledTileAmount);
            list.add("Seed: " + world.getSeed());
            list.add("Time: Local " + world.getCurrentTime() + " / Total " + world.getTotalTime());
            list.add("");

            list.add("Player:");
            list.add("Chunk: " + player.chunkX + ", " + player.chunkY);
            list.add("Pos: " + String.format(Locale.ROOT, "%.3f, %.3f", player.getX(), player.getY()));
            list.add("Motion: " + String.format(Locale.ROOT, "%.3f, %.3f", player.motionX, player.motionY));
            list.add("");

            int x = Util.floor(g.getMousedTileX());
            int y = Util.floor(g.getMousedTileY());
            list.add("Mouse:");
            list.add("ScreenPos: " + game.getInput().getMouseX() + ", " + game.getInput().getMouseY());
            list.add("TilePos: " + x + ", " + y);
            if (world.isPosLoaded(x, y)) {
                IChunk chunk = world.getChunk(x, y);
                list.add("ChunkPos: " + chunk.getGridX() + ", " + chunk.getGridY() + ", Persistent: " + chunk.isConstantlyPersistent());
                if (!RockBottomAPI.getNet().isClient()) {
                    list.add("ChunkPlayers: " + chunk.getPlayersInRange().size() + ", PlayersCached: " + chunk.getPlayersLeftRange().size());
                }
                list.add("Light: Sky " + world.getSkyLight(x, y) + " / Art " + world.getArtificialLight(x, y) + " -> " + world.getCombinedVisualLight(x, y));
                String s = "Biome: " + world.getBiome(x, y).getName();
                if (!world.isClient()) {
                    s += ", Levels: " + world.getExpectedBiomeLevels(x, y).stream().map(BiomeLevel::getName).collect(Collectors.toList());
                }
                list.add(s);
                list.add("Most Prominent Biome: " + chunk.getMostProminentBiome().getName());

                list.add("");
                for (TileLayer layer : TileLayer.getLayersByInteractionPrio()) {
                    list.add(layer.getName() + ": " + world.getState(layer, x, y));
                    list.add("Avg Height: " + chunk.getAverageHeight(layer) + ", Height: " + chunk.getHeight(layer, x));
                    list.add("Flatness: " + chunk.getFlatness(layer));
                    list.add("");
                }
            }
        }

        return list;
    }
}
