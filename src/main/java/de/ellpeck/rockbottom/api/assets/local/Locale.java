package de.ellpeck.rockbottom.api.assets.local;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import org.newdawn.slick.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Locale{

    private final String name;
    private final Map<IResourceName, String> localization = new HashMap<>();

    public Locale(String name){
        this.name = name;
    }

    public static Locale fromStream(InputStream stream, String name) throws IOException{
        Locale locale = new Locale(name);

        Properties props = new Properties();
        props.load(stream);

        for(String key : props.stringPropertyNames()){
            String value = props.getProperty(key);

            try{
                locale.localization.put(RockBottomAPI.createRes(key), value);
                Log.debug("Added localization "+key+" -> "+value+" to locale "+name);
            }
            catch(IllegalArgumentException e){
                Log.error("Cannot add "+value+" to locale "+name+" because key "+key+" cannot be parsed", e);
            }
        }

        return locale;
    }

    public boolean merge(Locale otherLocale){
        if(this.name.equals(otherLocale.name)){
            this.localization.putAll(otherLocale.localization);

            Log.info("Merged locale "+this.name+" with "+otherLocale.localization.size()+" bits of additional localization information");
            return true;
        }
        else{
            return false;
        }
    }

    public String localize(IResourceName unloc, Object... format){
        String loc = this.localization.get(unloc);

        if(loc == null){
            loc = unloc.toString();
            this.localization.put(unloc, loc);

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
