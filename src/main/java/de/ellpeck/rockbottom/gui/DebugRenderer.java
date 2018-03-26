package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.layer.TileLayer;
import de.ellpeck.rockbottom.assets.sound.SoundHandler;
import de.ellpeck.rockbottom.assets.tex.Texture;
import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class DebugRenderer{

    public static void render(RockBottom game, IAssetManager manager, World world, EntityPlayer player, IRenderer g){
        List<String> list = new ArrayList<>();

        list.add("Avg FPS: "+game.getFpsAverage());
        list.add("Avg TPS: "+game.getTpsAverage());
        list.add("Aspect: "+game.getWidth()+", "+game.getHeight());
        list.add("Display Ratio: "+g.getDisplayRatio());
        list.add("Gui Scale: "+g.getGuiScale());
        list.add("World Scale: "+g.getWorldScale());
        list.add("");
        list.add("Free Sound Sources: "+SoundHandler.getFreeSources());
        list.add("Playing Sounds: "+SoundHandler.getPlayingSoundAmount());
        list.add("Streaming Sounds: "+SoundHandler.getStreamingSoundAmount());
        list.add("");

        list.add("Texture Binds: "+Texture.binds);
        list.add("Renderer Flushes: "+g.getFlushes());
        list.add("");

        if(world != null && player != null){
            String chunks = "Loaded Chunks: "+world.loadedChunks.size();
            if(!RockBottomAPI.getNet().isClient()){
                chunks += ", PlayerChunks: "+player.getChunksInRange().size();
            }
            list.add(chunks);

            list.add("Entities: "+world.getAllEntities().size());
            if(!RockBottomAPI.getNet().isClient()){
                list.add("Players: "+world.players.size());
            }
            list.add("TileEntities: "+world.getAllTileEntities().size()+", Ticking: "+world.getAllTickingTileEntities().size());
            list.add("Particles: "+game.getParticleManager().getAmount());
            int scheduledTileAmount = 0;
            for(IChunk chunk : world.loadedChunks){
                scheduledTileAmount += chunk.getScheduledUpdateAmount();
            }
            list.add("Scheduled Tile Updates: "+scheduledTileAmount);
            list.add("Seed: "+world.getSeed());
            list.add("Time: Local "+world.getCurrentTime()+" / Total "+world.getTotalTime());
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
                list.add("ChunkPos: "+chunk.getGridX()+", "+chunk.getGridY()+", Persistent: "+chunk.isPersistent());
                if(!RockBottomAPI.getNet().isClient()){
                    list.add("ChunkPlayers: "+chunk.getPlayersInRange().size()+", PlayersCached: "+chunk.getPlayersLeftRange().size());
                }
                list.add("Light: Sky "+world.getSkyLight(x, y)+" / Art "+world.getArtificialLight(x, y)+" -> "+world.getCombinedVisualLight(x, y));
                list.add("Biome: "+world.getBiome(x, y).getName());
                list.add("Most Prominent Biome: "+chunk.getMostProminentBiome().getName());

                list.add("");
                for(TileLayer layer : TileLayer.getLayersByInteractionPrio()){
                    list.add(layer.getName()+": "+world.getState(layer, x, y).toString()+", Avg Height: "+chunk.getAverageHeight(layer));
                }
                list.add("");

            }
        }

        for(int i = 0; i < list.size(); i++){
            String s = list.get(i);
            if(!s.isEmpty()){
                manager.getFont().drawString(10F, 10F+i*20, s, 0.8F);
            }
        }
    }
}
