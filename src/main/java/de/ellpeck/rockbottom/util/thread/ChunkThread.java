package de.ellpeck.rockbottom.util.thread;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ChunkThread extends Thread{

    private final List<Runnable> queue = new ArrayList<>();
    private final AbstractGame game;

    public ChunkThread(AbstractGame game){
        super(ThreadHandler.CHUNK_GEN);
        this.game = game;
    }

    @Override
    public void run(){
        while(this.game.isRunning){
            try{
                synchronized(this.queue){
                    while(!this.queue.isEmpty()){
                        this.queue.remove(0).run();
                    }
                }
            }
            catch(Exception e){
                RockBottomAPI.logger().log(Level.WARNING, "There was an exception in the chunk gen thread, however it will attempt to keep running", e);
            }

            Util.sleepSafe(1);
        }
    }

    public void add(Runnable runnable){
        synchronized(this.queue){
            this.queue.add(runnable);
        }
    }
}
