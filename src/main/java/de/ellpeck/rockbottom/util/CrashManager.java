package de.ellpeck.rockbottom.util;

import com.google.common.base.Throwables;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.init.AbstractGame;
import de.ellpeck.rockbottom.log.Logging;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                String code = "";
                code += divider+"\n";
                code += name+"\n";
                code += date+"\n";
                code += divider+"\n";
                code += Throwables.getStackTraceAsString(t);
                code += divider;

                pasteLink = paste(code, name+" "+date);
            }
            catch(Exception e){
                log(Level.WARNING, "Couldn't paste crash report online", e);
            }
        }

        try{
            PrintWriter writer = new PrintWriter(file);

            writer.println(divider);
            writer.println(name);
            writer.println(date);
            if(pasteLink != null){
                writer.println("Find an online version of this crash report at "+pasteLink);
            }
            writer.println(divider);
            t.printStackTrace(writer);
            writer.print(divider);

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

    private static String paste(String code, String name) throws Exception{
        HttpClient client = HttpClients.createDefault();

        String url = "https://pastebin.com/api/api_post.php";
        HttpPost post = new HttpPost(url);

        List<NameValuePair> list = new ArrayList();
        list.add(new BasicNameValuePair("api_dev_key", "b4f50994f496fe07c7f87b7ed3698d7f"));
        list.add(new BasicNameValuePair("api_option", "paste"));
        list.add(new BasicNameValuePair("api_paste_code", code));
        list.add(new BasicNameValuePair("api_paste_name", name));
        post.setEntity(new UrlEncodedFormEntity(list));

        HttpResponse res = client.execute(post);
        return EntityUtils.toString(res.getEntity());
    }

    private static void log(Level level, String text, Throwable t){
        if(Logging.mainLogger != null){
            Logging.mainLogger.log(level, text, t);
        }
    }
}
