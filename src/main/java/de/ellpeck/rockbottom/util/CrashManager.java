package de.ellpeck.rockbottom.util;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public final class CrashManager{

    public static void makeCrashReport(Throwable t){
        Main.memReserve = null;
        System.gc();

        t = t.fillInStackTrace();

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

        String pasteLink = null;

        try{
            StringWriter writer = new StringWriter();
            writeInfo(new PrintWriter(writer), divider, name, date, "Find a file with this crash report at "+file, comment, t);
            log(Level.SEVERE, "Crash Report:\n"+writer.toString(), null);
        }
        catch(Exception e){
            log(Level.WARNING, "Couldn't generate full crash report", e);
            log(Level.SEVERE, "The game crashed for the following reason", t);
        }

        if(!Main.suppressCrashPaste){
            try{
                StringWriter writer = new StringWriter();
                writeInfo(new PrintWriter(writer), divider, name, date, null, comment, t);

                JsonObject object = paste(writer.toString(), name+" "+date);
                pasteLink = object.get("link").getAsString();

                writer.close();
            }
            catch(Exception e){
                log(Level.WARNING, "Couldn't paste crash report online", e);
            }
        }

        try{
            PrintWriter writer = new PrintWriter(file);
            writeInfo(writer, divider, name, date, "Find an online version of this crash report at "+pasteLink, comment, t);
            writer.flush();
        }
        catch(Exception e){
            log(Level.WARNING, "Couldn't save crash report to "+file, e);
        }

        log(Level.INFO, divider, null);
        if(pasteLink != null){
            log(Level.INFO, "Uploaded crash report to "+pasteLink, null);
        }
        log(Level.INFO, "Saved crash report to "+file, null);
        log(Level.INFO, divider, null);

        if(!Main.isDedicatedServer && pasteLink != null){
            try{
                Desktop.getDesktop().browse(new URI(pasteLink));
            }
            catch(Exception e){
                log(Level.WARNING, "Couldn't open paste link", e);
            }
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
        writer.println("Java Version: "+System.getProperty("java.version"));
        writer.println("Operating System: "+System.getProperty("os.name")+" "+System.getProperty("os.version"));
        writer.println("Processors: "+runtime.availableProcessors());
        long free = runtime.freeMemory();
        writer.println("Free Memory: "+free+" bytes ("+free/1024+" megabytes)");
        long max = runtime.maxMemory();
        writer.println("Max Memory: "+(max >= Long.MAX_VALUE ? "None" : max+" bytes ("+max/1024+" megabytes"));

        writer.println(divider);
        t.printStackTrace(writer);
        writer.println(divider);

        try{
            writer.println("LOADED MODS:");

            IModLoader loader = RockBottomAPI.getModLoader();
            for(IMod mod : loader.getAllTheMods()){
                String s = mod.getDisplayName()+" @ "+mod.getVersion()+" ("+mod.getId()+")";
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
                String s = pack.getName()+" @ "+pack.getVersion()+" ("+pack.getId()+")";
                if(loader.getPackSettings().isDisabled(pack.getId())){
                    s += " [DISABLED]";
                }
                writer.println(s);
            }
        }
        catch(Exception e){
            writer.println("Content pack information unavailable");
        }

        writer.print(divider);
    }

    private static JsonObject paste(String code, String name) throws
            Exception{
        HttpClient client = HttpClients.createDefault();

        String url = "https://api.paste.ee/v1/pastes";
        HttpPost post = new HttpPost(url);

        JsonObject json = new JsonObject();
        JsonArray array = new JsonArray();

        JsonObject section = new JsonObject();
        section.addProperty("name", name);
        section.addProperty("contents", code);

        array.add(section);
        json.add("sections", array);

        StringEntity entity = new StringEntity(json.toString());
        entity.setContentType("application/json");
        post.setEntity(entity);

        post.addHeader("X-Auth-Token", "uf9VO0VLbW4F0iyvqLRjfBQMJJB8GuCAg06gOCENS");

        HttpResponse response = client.execute(post);
        String resString = EntityUtils.toString(response.getEntity());

        return Util.JSON_PARSER.parse(resString).getAsJsonObject();
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
