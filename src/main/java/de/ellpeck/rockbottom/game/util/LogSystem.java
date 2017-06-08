package de.ellpeck.rockbottom.game.util;

import org.newdawn.slick.util.DefaultLogSystem;

import java.util.Date;

public class LogSystem extends DefaultLogSystem{

    private final LogLevel level;

    public LogSystem(LogLevel level){
        this.level = level;
    }

    @Override
    public void error(String message, Throwable t){
        this.format(LogLevel.ERROR, message, t);
    }

    @Override
    public void error(Throwable t){
        this.error("", t);
    }

    @Override
    public void error(String message){
        this.error(message, null);
    }

    @Override
    public void warn(String message){
        this.warn(message, null);
    }

    @Override
    public void warn(String message, Throwable t){
        this.format(LogLevel.WARN, message, t);
    }

    @Override
    public void info(String message){
        this.format(LogLevel.INFO, message, null);
    }

    @Override
    public void debug(String message){
        this.format(LogLevel.DEBUG, message, null);
    }

    private void format(LogLevel level, String message, Throwable t){
        if(this.level == level || this.level.ordinal() >= level.ordinal()){
            out.println("["+new Date()+"] ["+level+"] "+message);

            if(t != null){
                t.printStackTrace(out);
            }
        }
    }

    public enum LogLevel{
        NONE,
        ERROR,
        WARN,
        INFO,
        DEBUG
    }
}
