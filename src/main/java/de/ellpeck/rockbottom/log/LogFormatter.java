package de.ellpeck.rockbottom.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter{

    private final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
    private final Date date = new Date();

    @Override
    public String format(LogRecord record){
        this.date.setTime(record.getMillis());

        String format = "["+this.format.format(this.date)+"] ["+record.getLoggerName()+"] ["+record.getLevel()+"] "+record.getMessage()+System.lineSeparator();

        Throwable t = record.getThrown();
        if(t != null){
            StringWriter stringWriter = new StringWriter();

            PrintWriter printWriter = new PrintWriter(stringWriter);
            t.printStackTrace(printWriter);
            printWriter.close();

            format += stringWriter.toString();
        }

        return format;
    }
}
