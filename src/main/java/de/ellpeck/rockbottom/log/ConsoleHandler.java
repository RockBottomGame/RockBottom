package de.ellpeck.rockbottom.log;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class ConsoleHandler extends StreamHandler{

    public ConsoleHandler(OutputStream out, Formatter formatter){
        super(out, formatter);
    }

    @Override
    public synchronized void publish(LogRecord record){
        super.publish(record);
        this.flush();
    }
}
