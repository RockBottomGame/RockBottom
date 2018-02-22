package de.ellpeck.rockbottom.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.log.Logging;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

public final class CrashManager{

    public static void makeCrashReport(Throwable t){
        File dir = new File(Main.gameDir, "crashes");
        if(!dir.exists()){
            dir.mkdirs();
        }

        String date = new SimpleDateFormat("dd.MM.yy_HH.mm.ss").format(new Date());
        File file = new File(dir, date+".txt");

        log(Level.SEVERE, "The game encountered a fatal exception", t);

        String name = AbstractGame.NAME.toUpperCase()+" CRASH REPORT";
        String divider = "====================================================================================================";

        String pasteLink = null;

        if(!Main.suppressCrashPaste){
            try{
                StringWriter writer = new StringWriter();
                writeInfo(new PrintWriter(writer), divider, name, date, pasteLink, t);

                JsonObject object = paste(writer.toString(), name+" "+date);
                pasteLink = object.get("link").getAsString();
            }
            catch(Exception e){
                log(Level.WARNING, "Couldn't paste crash report online", e);
            }
        }

        try{
            PrintWriter writer = new PrintWriter(file);
            writeInfo(writer, divider, name, date, pasteLink, t);
            writer.flush();
        }
        catch(Exception e){
            log(Level.WARNING, "Couldn't save crash report to "+file, e);
        }

        log(Level.INFO, "======================================================================", null);
        if(pasteLink != null){
            log(Level.INFO, "Uploaded crash report to "+pasteLink, null);
        }
        log(Level.INFO, "Saved crash report to "+file, null);
        log(Level.INFO, "======================================================================", null);

        if(!Main.isDedicatedServer && pasteLink != null){
            try{
                Desktop.getDesktop().browse(new URI(pasteLink));
            }
            catch(Exception e){
                log(Level.WARNING, "Couldn't open paste link", e);
            }
        }
    }

    private static void writeInfo(PrintWriter writer, String divider, String name, String date, String pasteLink, Throwable t){
        writer.println(divider);

        writer.println(name);
        writer.println(date);
        if(pasteLink != null){
            writer.println("Find an online version of this crash report at "+pasteLink);
        }

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

        writer.print(divider);
    }

    private static JsonObject paste(String code, String name) throws Exception{
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

    private static void log(Level level, String text, Throwable t){
        if(Logging.mainLogger != null){
            Logging.mainLogger.log(level, text, t);
        }
    }
}
