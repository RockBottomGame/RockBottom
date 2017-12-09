package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IGraphics;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.lwjgl.opengl.Display;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DebugRenderer{

    public static void render(RockBottom game, IAssetManager manager, World world, EntityPlayer player, IGraphics g){
        List<String> list = new ArrayList<>();

        list.add("Avg FPS: "+game.getFpsAverage());
        list.add("Avg TPS: "+game.getTpsAverage());
        list.add("Aspect: "+Display.getWidth()+", "+Display.getHeight());
        list.add("Display Ratio: "+g.getDisplayRatio());
        list.add("Gui Scale: "+g.getGuiScale());
        list.add("World Scale: "+g.getWorldScale());
        list.add("");

        String chunks = "Loaded Chunks: "+world.loadedChunks.size();
        if(!RockBottomAPI.getNet().isClient()){
            chunks += ", PlayerChunks: "+player.getChunksInRange().size();
        }
        list.add(chunks);

        list.add("Entities: "+world.getAllEntities().size());
        if(!RockBottomAPI.getNet().isClient()){
            list.add("Players: "+world.players.size());
        }
        list.add("TileEntities: "+world.getAllTileEntities().size());
        list.add("Particles: "+game.getParticleManager().getAmount());
        int scheduledTileAmount = 0;
        for(IChunk chunk : world.loadedChunks){
            scheduledTileAmount += chunk.getScheduledUpdateAmount();
        }
        list.add("Scheduled Tile Updates: "+scheduledTileAmount);
        list.add("Seed: "+world.getWorldInfo().seed);
        list.add("Time: Local "+world.getWorldInfo().currentWorldTime+" / Total "+world.getWorldInfo().totalTimeInWorld);
        list.add("");

        list.add("Player:");
        list.add("Chunk: "+player.chunkX+", "+player.chunkY);
        list.add("Pos: "+String.format(Locale.ROOT, "%.3f, %.3f", player.x, player.y));
        list.add("Motion: "+String.format(Locale.ROOT, "%.3f, %.3f", player.motionX, player.motionY));
        list.add("");

        int x = Util.floor(g.getMousedTileX());
        int y = Util.floor(g.getMousedTileY());
        list.add("Mouse:");
        list.add("ScreenPos: "+game.getInput().getMouseX()+", "+game.getInput().getMouseY());
        list.add("TilePos: "+x+", "+y);
        if(world.isPosLoaded(x, y)){
            IChunk chunk = world.getChunk(x, y);
            list.add("ChunkPos: "+chunk.getGridX()+", "+chunk.getGridY());
            if(!RockBottomAPI.getNet().isClient()){
                list.add("ChunkPlayers: "+chunk.getPlayersInRange().size()+", PlayersCached: "+chunk.getPlayersLeftRange().size());
            }
            list.add("Light: Sky "+world.getSkyLight(x, y)+" / Art "+world.getArtificialLight(x, y)+" -> "+world.getCombinedVisualLight(x, y));
            list.add("Tile: "+world.getState(x, y)+" / "+world.getState(TileLayer.BACKGROUND, x, y));
            list.add("Biome: "+world.getBiome(x, y).getName());
        }

        for(int i = 0; i < list.size(); i++){
            String s = list.get(i);
            if(!s.isEmpty()){
                manager.getFont().drawString(10F, 10F+i*20, s, 0.8F);
            }
        }
    }
}
