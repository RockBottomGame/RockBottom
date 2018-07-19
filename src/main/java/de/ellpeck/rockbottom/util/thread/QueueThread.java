package de.ellpeck.rockbottom.util.thread;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class QueueThread extends Thread {

    private final List<Runnable> queue = new ArrayList<>();
    private final AbstractGame game;
    private final String name;

    public QueueThread(String name, AbstractGame game) {
        super(name);
        this.game = game;
        this.name = name;
    }

    @Override
    public void run() {
        while (this.game.isRunning()) {
            try {
                while (!this.queue.isEmpty()) {
                    Runnable runnable;
                    synchronized (this.queue) {
                        runnable = this.queue.remove(0);
                    }
                    runnable.run();
                }
            } catch (Exception e) {
                RockBottomAPI.logger().log(Level.WARNING, "There was an exception in the " + this.name + " thread, however it will attempt to keep running", e);
            }

            Util.sleepSafe(1);
        }
    }

    public void add(Runnable runnable) {
        synchronized (this.queue) {
            this.queue.add(runnable);
        }
    }
}
