package de.ellpeck.rockbottom.gui;

import de.ellpeck.rockbottom.init.RockBottom;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.world.IChunk;
import de.ellpeck.rockbottom.api.world.TileLayer;
import de.ellpeck.rockbottom.world.World;
import de.ellpeck.rockbottom.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public final class DebugRenderer{

    public static void render(RockBottom game, IAssetManager manager, World world, EntityPlayer player, GameContainer container, Graphics g){
        g.setColor(Color.black);
        g.drawOval((float)container.getWidth()/2F-5F, (float)container.getHeight()/2F-5F, 10, 10);

        List<String> list = new ArrayList<>();

        list.add("Avg FPS: "+game.getFpsAverage());
        list.add("Avg TPS: "+game.getTpsAverage());
        list.add("Aspect: "+container.getWidth()+", "+container.getHeight());
        list.add("Screen: "+container.getScreenWidth()+", "+container.getScreenHeight());
        list.add("World: "+game.getWidthInWorld()+", "+game.getHeightInWorld());
        list.add("Gui: "+game.getWidthInGui()+", "+game.getHeightInGui());
        list.add("Vsync: "+container.isVSyncRequested()+", Fullscreen: "+container.isFullscreen());
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
        list.add("Pos: "+player.x+", "+player.y);
        list.add("Motion: "+player.motionX+", "+player.motionY);
        list.add("");

        int x = game.getInteractionManager().mousedTileX;
        int y = game.getInteractionManager().mousedTileY;
        list.add("Mouse:");
        list.add("ScreenPos: "+container.getInput().getMouseX()+", "+container.getInput().getMouseY());
        list.add("TilePos: "+x+", "+y);
        if(world.isPosLoaded(x, y)){
            IChunk chunk = world.getChunk(x, y);
            list.add("ChunkPos: "+chunk.getGridX()+", "+chunk.getGridY());
            if(!RockBottomAPI.getNet().isClient()){
                list.add("ChunkPlayers: "+chunk.getPlayersInRange().size()+", PlayersCached: "+chunk.getPlayersLeftRange().size());
            }
            list.add("Light: Sky "+world.getSkyLight(x, y)+" / Art "+world.getArtificialLight(x, y)+" -> "+world.getCombinedLight(x, y));
            list.add("Tile: "+world.getTile(x, y).getName()+" / "+world.getTile(TileLayer.BACKGROUND, x, y).getName());
            list.add("Meta: "+world.getMeta(x, y)+" / "+world.getMeta(TileLayer.BACKGROUND, x, y));
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
