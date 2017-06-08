package de.ellpeck.rockbottom.game;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.game.util.LogSystem;
import de.ellpeck.rockbottom.game.util.LogSystem.LogLevel;
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
            Log.error("Exception initializing game", e);
        }
        finally{
            RockBottomAPI.getNet().shutdown();
        }

        Log.info("Game shutting down");
        System.exit(0);
    }
}
