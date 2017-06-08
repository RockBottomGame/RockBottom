package de.ellpeck.rockbottom.game;

import de.ellpeck.rockbottom.game.assets.AssetManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class Container extends AppGameContainer{

    protected final RockBottom game;

    public Container(RockBottom game) throws SlickException{
        super(game, 1280, 720, false);
        this.game = game;
    }

    @Override
    protected void setup() throws SlickException{
        Display.setTitle(this.game.getTitle());
        Display.setResizable(true);

        Log.info("LWJGL version: "+Sys.getVersion());
        Log.info("Display: "+this.originalDisplayMode);
        Log.info("Target: "+this.targetDisplayMode);

        AccessController.doPrivileged((PrivilegedAction)() -> {
            try{
                PixelFormat format = new PixelFormat(8, 8, 0);
                Display.create(format);
            }
            catch(LWJGLException e){
                Log.error("Couldn't create pixel format", e);

                try{
                    Display.create();
                }
                catch(LWJGLException e2){
                    throw new RuntimeException("Failed to initialize LWJGL display", e2);
                }
            }
            return null;
        });

        Log.info("Initializing system");

        this.initSystem();
        this.enterOrtho();

        try{
            this.drawLoadingInfo();
        }
        catch(SlickException e){
            Log.warn("Couldn't render loading screen image", e);
        }

        try{
            Log.info("Initializing controllers");
            this.getInput().initControllers();
        }
        catch(SlickException e){
            Log.warn("Failed to initialize controllers", e);
        }

        Log.info("Finished initializing system");

        try{
            this.game.init(this);
        }
        catch(SlickException e){
            Log.error("Failed to initialize game", e);
            this.running = false;
        }
    }

    @Override
    protected void gameLoop() throws SlickException{
        super.gameLoop();

        if(!this.isFullscreen() && Display.wasResized()){
            this.width = Display.getWidth();
            this.height = Display.getHeight();

            this.initGL();
            this.enterOrtho();

            this.game.getGuiManager().setReInit();
        }
    }

    private void drawLoadingInfo() throws SlickException{
        Image image = new Image(AssetManager.getResource("/assets/loading.png"), "loading", false);
        image.setFilter(Image.FILTER_NEAREST);

        image.draw(0, 0, this.getWidth(), this.getHeight());
        Display.update();
    }
}
