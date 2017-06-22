package de.ellpeck.rockbottom.util;

import org.newdawn.slick.util.DefaultLogSystem;
import org.newdawn.slick.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogSystem extends DefaultLogSystem{

    private static final DateFormat FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
    private final LogLevel level;

    private LogSystem(LogLevel level){
        this.level = level;
    }

    public static void init(LogLevel level){
        Log.setLogSystem(new LogSystem(level));
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
            out.println("["+FORMAT.format(new Date())+"] ["+level+"] "+message);

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
