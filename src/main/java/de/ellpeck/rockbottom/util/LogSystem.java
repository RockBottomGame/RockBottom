package de.ellpeck.rockbottom.util;

import org.newdawn.slick.util.DefaultLogSystem;

import java.util.Date;

public class LogSystem extends DefaultLogSystem{

    @Override
    public void error(String message, Throwable t){
        this.format("ERROR", message, t);
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
        this.format("WARN", message, t);
    }

    @Override
    public void info(String message){
        this.format("INFO", message, null);
    }

    @Override
    public void debug(String message){
        this.format("DEBUG", message, null);
    }

    private void format(String level, String message, Throwable t){
        out.println("["+new Date()+"] ["+level+"] "+message);

        if(t != null){
            t.printStackTrace(out);
        }
    }
}
