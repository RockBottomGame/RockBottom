package de.ellpeck.game.gui;

import de.ellpeck.game.Game;
import de.ellpeck.game.world.Chunk;
import de.ellpeck.game.world.World;
import de.ellpeck.game.world.entity.player.EntityPlayer;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.List;

public final class DebugRenderer{

    public static void render(Game game, World world, EntityPlayer player, GameContainer container, Graphics g){
        g.setColor(Color.black);
        g.drawOval((float)container.getWidth()/2F-5F, (float)container.getHeight()/2F-5F, 10, 10);

        List<String> list = new ArrayList<>();

        list.add("Avg FPS: "+game.fpsAverage);
        list.add("Avg TPS: "+game.tpsAverage);
        list.add("");

        list.add("Loaded Chunks: "+world.loadedChunks.size());
        list.add("Entities: "+world.getAllEntities().size());
        list.add("Players: "+world.players.size());
        list.add("TileEntities: "+world.getAllTileEntities().size());
        list.add("Particles: "+game.particleManager.getAmount());
        int amount = 0;
        for(Chunk chunk : world.loadedChunks){
            amount += chunk.randomUpdateTileAmount;
        }
        list.add("Random Update Tiles: "+amount);
        list.add("Seed: "+world.getSeed());
        list.add("");

        list.add("Player:");
        list.add("Chunk: "+player.chunkX+", "+player.chunkY);
        list.add("Pos: "+player.x+", "+player.y);
        list.add("");

        list.add("Mouse:");
        list.add("Pos: "+container.getInput().getMouseX()+", "+container.getInput().getMouseY());
        list.add("Tile: "+game.interactionManager.mousedTileX+", "+game.interactionManager.mousedTileY);
        list.add("Light: "+world.getLight(game.interactionManager.mousedTileX, game.interactionManager.mousedTileY));

        for(int i = 0; i < list.size(); i++){
            String s = list.get(i);
            if(!s.isEmpty()){
                container.getDefaultFont().drawString(10, 10+i*20, s);
            }
        }
    }
}
