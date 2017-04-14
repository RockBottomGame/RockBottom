package de.ellpeck.game;

import de.ellpeck.game.net.NetHandler;
import de.ellpeck.game.util.LogSystem;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public final class Main{

    public static void main(String[] args){
        Log.setLogSystem(new LogSystem());

        Game game = new Game();

        try{
            Container container = new Container(game);
            container.setForceExit(false);

            container.setAlwaysRender(true);
            container.setShowFPS(false);

            int interval = 1000/Constants.TARGET_TPS;
            container.setMinimumLogicUpdateInterval(interval);
            container.setMaximumLogicUpdateInterval(interval);

            container.start();
        }
        catch(SlickException e){
            Log.error("Exception initializing game! ", e);
        }
        finally{
            NetHandler.shutdown();
        }
    }
}
