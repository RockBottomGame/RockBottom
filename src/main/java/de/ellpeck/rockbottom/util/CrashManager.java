package de.ellpeck.rockbottom.util;

import com.google.common.base.Charsets;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.content.pack.ContentPack;
import de.ellpeck.rockbottom.api.content.pack.IContentPackLoader;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.content.ContentManager;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.log.Logging;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public final class CrashManager{

    private static List<String> additionalInfo;

    public static void addInfo(String s){
        if(additionalInfo == null){
            additionalInfo = new ArrayList<>();
        }

        additionalInfo.add(s);
    }

    public static void makeCrashReport(Throwable t){
        Main.memReserve = null;
        System.gc();

        File dir = new File(Main.gameDir, "crashes");
        if(!dir.exists()){
            dir.mkdirs();
        }

        log(Level.SEVERE, "The game encountered a fatal exception, creating crash report...", null);

        String date = new SimpleDateFormat("dd.MM.yy_HH.mm.ss").format(new Date());
        File file = new File(dir, date+".txt");

        String name = AbstractGame.NAME.toUpperCase()+" CRASH REPORT";
        String divider = "------------------------------------------------------------";

        String comment;
        try{
            comment = getComment();
        }
        catch(Exception e){
            comment = "Comment unavailable for some reason :(";
        }

        try{
            StringWriter writer = new StringWriter();
            writeInfo(new PrintWriter(writer), divider, name, date, "Find a file with this crash report at "+file, comment, t);
            log(Level.SEVERE, "Crash Report:\n"+writer, null);
        }
        catch(Exception e){
            log(Level.WARNING, "Couldn't generate full crash report", e);
            log(Level.SEVERE, "The game crashed for the following reason", t);
        }

        try{
            PrintWriter writer = new PrintWriter(file);
            writeInfo(writer, divider, name, date, null, comment, t);
            writer.flush();
        }
        catch(Exception e){
            log(Level.WARNING, "Couldn't save crash report to "+file, e);
        }
    }

    private static void writeInfo(PrintWriter writer, String divider, String name, String date, String extraInfo, String comment, Throwable t){
        writer.println(divider);

        writer.println(name);
        writer.println(date);
        if(extraInfo != null){
            writer.println(extraInfo);
        }

        writer.println(divider);
        writer.println("//TODO "+comment);
        writer.println(divider);

        writer.println("Game Version: "+AbstractGame.VERSION);
        writer.println("API Version: "+RockBottomAPI.VERSION);

        writer.println(divider);

        Runtime runtime = Runtime.getRuntime();
        writer.println("Java Version: "+System.getProperty("java.version")+' '+System.getProperty("sun.arch.data.model")+"bit");
        writer.println("Operating System: "+System.getProperty("os.name")+' '+System.getProperty("os.version"));
        writer.println("Processors: "+runtime.availableProcessors());
        long total = runtime.totalMemory();
        writer.println("Used Memory: "+displayByteCount(total-runtime.freeMemory()));
        writer.println("Reserved Memory: "+displayByteCount(total));
        writer.println("Allocated Memory: "+displayByteCount(runtime.maxMemory()));

        writer.println(divider);
        t.printStackTrace(writer);
        writer.println(divider);

        try{
            writer.println("LOADED MODS:");

            IModLoader loader = RockBottomAPI.getModLoader();
            for(IMod mod : loader.getAllTheMods()){
                String s = mod.getDisplayName()+" @ "+mod.getVersion()+" ("+mod.getId()+')';
                if(loader.getModSettings().isDisabled(mod.getId())){
                    s += " [DISABLED]";
                }
                writer.println(s);
            }
        }
        catch(Exception e){
            writer.println("Mod information unavailable");
        }

        writer.println(divider);

        try{
            writer.println("LOADED CONTENT PACKS:");

            IContentPackLoader loader = RockBottomAPI.getContentPackLoader();
            for(ContentPack pack : loader.getAllPacks()){
                String s = pack.getName()+" @ "+pack.getVersion()+" ("+pack.getId()+')';
                if(loader.getPackSettings().isDisabled(pack.getId())){
                    s += " [DISABLED]";
                }
                writer.println(s);
            }
        }
        catch(Exception e){
            writer.println("Content pack information unavailable");
        }

        if(additionalInfo != null && !additionalInfo.isEmpty()){
            writer.println(divider);
            writer.println("ADDITIONAL CRASH INFO:");

            for(String s : additionalInfo){
                writer.println(s);
            }
        }

        writer.print(divider);
    }

    public static String displayByteCount(long bytes){
        if(bytes < 1024){
            return bytes+" B";
        }
        else{
            int exp = (int)(Math.log(bytes)/Math.log(1024));
            char pre = "KMGTPE".charAt(exp-1);
            return String.format("%.1f %sB", bytes/Math.pow(1024, exp), pre);
        }
    }

    private static String getComment() throws Exception{
        List<String> lines = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(ContentManager.getResourceAsStream("assets/rockbottom/text/crash.txt"), Charsets.UTF_8));
        while(true){
            String line = reader.readLine();
            if(line != null){
                lines.add(line);
            }
            else{
                break;
            }
        }
        reader.close();

        return lines.get((int)(Util.getTimeMillis()%lines.size()));
    }

    private static void log(Level level, String text, Throwable t){
        if(Logging.mainLogger != null){
            Logging.mainLogger.log(level, text, t);
        }
    }
}
