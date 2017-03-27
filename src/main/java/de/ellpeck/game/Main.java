package de.ellpeck.game;

import de.ellpeck.game.util.LogSystem;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

public final class Main{

    public static void main(String[] args){
        Log.setLogSystem(new LogSystem());

        Game game = new Game();

        try{
            AppGameContainer container = new AppGameContainer(game, 1280, 720, false);

            container.setAlwaysRender(true);
            container.setShowFPS(false);
            container.setTargetFrameRate(Constants.TARGET_FPS);

            int interval = 1000/Constants.TARGET_TPS;
            container.setMinimumLogicUpdateInterval(interval);
            container.setMaximumLogicUpdateInterval(interval);

            container.start();
        }
        catch(SlickException e){
            Log.error("Exception initializing game! ", e);
        }
    }
}
