package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.net.NetHandler;
import de.ellpeck.rockbottom.util.LogSystem;
import de.ellpeck.rockbottom.util.LogSystem.LogLevel;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public final class Main{

    public static void main(String[] args){
        Log.setLogSystem(new LogSystem(LogLevel.DEBUG));

        RockBottom game = new RockBottom();

        try{
            Container container = new Container(game);
            container.setForceExit(false);
            container.setUpdateOnlyWhenVisible(false);
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

        Log.info("Game shutting down");
        System.exit(0);
    }
}
