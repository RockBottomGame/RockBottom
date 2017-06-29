package de.ellpeck.rockbottom.init;

import org.newdawn.slick.SlickException;

public class ContainerServer extends Container{

    public ContainerServer(RockBottomServer game) throws SlickException{
        super(game);
    }

    @Override
    protected void setup() throws SlickException{
        this.doSetup();
    }

    @Override
    protected void gameLoop() throws SlickException{
        this.doGameLoop();
    }

    @Override
    protected void updateAndRender(int delta) throws SlickException{
        this.doUpdate(delta);
    }

    @Override
    public void setDisplayMode(int width, int height, boolean fullscreen) throws SlickException{

    }
}
