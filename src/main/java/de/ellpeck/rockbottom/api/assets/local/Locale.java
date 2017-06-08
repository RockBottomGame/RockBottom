package de.ellpeck.rockbottom.api.assets.local;

import org.newdawn.slick.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Locale{

    private final String name;
    private final Map<String, String> localization = new HashMap<>();

    public Locale(String name){
        this.name = name;
    }

    public static Locale fromStream(InputStream stream, String name) throws IOException{
        Locale locale = new Locale(name);

        Properties props = new Properties();
        props.load(stream);

        for(String key : props.stringPropertyNames()){
            String value = props.getProperty(key);

            locale.localization.put(key, value);
        }

        return locale;
    }

    public String localize(String unloc, Object... format){
        String loc = this.localization.get(unloc);

        if(loc == null){
            this.localization.put(unloc, unloc);
            loc = unloc;

            Log.warn("Localization with name "+unloc+" is missing from locale with name "+this.name+"!");
        }

        if(format == null || format.length <= 0){
            return loc;
        }
        else{
            return String.format(loc, format);
        }
    }
}
