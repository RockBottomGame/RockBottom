package de.ellpeck.rockbottom.log;

import org.newdawn.slick.util.LogSystem;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SlickLogWrapper implements LogSystem{

    private static final Logger SLICK_LOGGER = Logging.createLogger("Slick-Util");

    @Override
    public void error(String message, Throwable e){
        SLICK_LOGGER.log(Level.SEVERE, message, e);
    }

    @Override
    public void error(Throwable e){
        SLICK_LOGGER.log(Level.SEVERE, "An error occured", e);
    }

    @Override
    public void error(String message){
        SLICK_LOGGER.log(Level.SEVERE, message);
    }

    @Override
    public void warn(String message){
        SLICK_LOGGER.log(Level.WARNING, message);
    }

    @Override
    public void info(String message){
        SLICK_LOGGER.log(Level.INFO, message);
    }

    @Override
    public void debug(String message){
        SLICK_LOGGER.log(Level.CONFIG, message);
    }
}
