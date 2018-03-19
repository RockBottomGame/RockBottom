package de.ellpeck.rockbottom.util.thread;

import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChunkThread extends Thread{

    private final List<Runnable> queue = new ArrayList<>();
    private final AbstractGame game;

    public ChunkThread(AbstractGame game){
        super("ChunkGen");
        this.game = game;
    }

    @Override
    public void run(){
        while(this.game.isRunning){
            synchronized(this.queue){
                if(!this.queue.isEmpty()){
                    Iterator<Runnable> iterator = this.queue.iterator();
                    while(iterator.hasNext()){
                        iterator.next().run();
                        iterator.remove();
                    }
                }
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
