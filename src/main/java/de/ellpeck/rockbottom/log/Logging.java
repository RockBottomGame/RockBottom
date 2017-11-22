package de.ellpeck.rockbottom.log;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.init.AbstractGame;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Logging{

    public static Logger mainLogger;

    public static void createMain(String levelName){
        Level level = getLogLevel(levelName);
        mainLogger = Logger.getLogger(AbstractGame.NAME);
        mainLogger.setLevel(level);
        mainLogger.setUseParentHandlers(false);

        LogFormatter formatter = new LogFormatter();
        Filter filter = record -> record.getLevel().intValue() < Level.WARNING.intValue();

        ConsoleHandler infoHandler = new ConsoleHandler(System.out, formatter);
        infoHandler.setFilter(filter);
        infoHandler.setLevel(level);
        mainLogger.addHandler(infoHandler);

        ConsoleHandler errorHandler = new ConsoleHandler(System.err, formatter);
        errorHandler.setFilter(record -> !filter.isLoggable(record));
        errorHandler.setLevel(level);
        mainLogger.addHandler(errorHandler);

        try{
            File file = new File(Main.gameDir, "log");
            if(!file.exists()){
                file.mkdirs();

                mainLogger.info("Creating logs folder at "+file);
            }

            FileHandler fileHandler = new FileHandler(new File(file, "latest.log").getPath());
            fileHandler.setFilter(null);
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(level);
            mainLogger.addHandler(fileHandler);
        }
        catch(Exception e){
            mainLogger.log(Level.SEVERE, "Could not initialize logger file saving", e);
        }

        Log.setLogSystem(new SlickLogWrapper());
    }

    private static Level getLogLevel(String level){
        switch(level.toLowerCase(Locale.ROOT)){
            case "all":
                return Level.ALL;
            case "finest":
                return Level.FINEST;
            case "finer":
                return Level.FINER;
            case "fine":
                return Level.FINE;
            case "config":
                return Level.CONFIG;
            case "info":
                return Level.INFO;
            case "warning":
                return Level.WARNING;
            case "severe":
                return Level.SEVERE;
            case "off":
                return Level.OFF;
        }
        throw new IllegalArgumentException("Specified log level "+level+" is invalid!");
    }

    public static Logger createLogger(String name){
        Logger logger = Logger.getLogger(name);
        logger.setParent(mainLogger);
        return logger;
    }
}
