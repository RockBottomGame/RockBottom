package de.ellpeck.rockbottom.assets.loader;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetLoader;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.Locale;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.assets.AssetManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class LocaleLoader implements IAssetLoader<Locale>{

    @Override
    public IResourceName getAssetIdentifier(){
        return RockBottomAPI.createInternalRes("loc");
    }

    @Override
    public Locale loadAsset(IAssetManager manager, IResourceName resourceName, String path, JsonElement element, String elementName, IMod loadingMod) throws Exception{
        String resPath = path+element.getAsString();
        Locale locale = this.fromStream(AssetManager.getResource(resPath), elementName);

        for(Locale asset : manager.getAllOfType(Locale.class).values()){
            Locale mergedLocale = this.merge(asset, locale);
            if(mergedLocale != null){
                return mergedLocale;
            }
        }

        RockBottomAPI.logger().config("Loaded locale "+resourceName+" for mod "+loadingMod.getDisplayName());
        return locale;
    }

    private Locale fromStream(InputStream stream, String name) throws Exception{
        JsonElement main = Util.JSON_PARSER.parse(new InputStreamReader(stream, Charsets.UTF_8));

        Map<IResourceName, String> locale = new HashMap<>();
        for(Map.Entry<String, JsonElement> entry : main.getAsJsonObject().entrySet()){
            this.recurseLoad(locale, name, entry.getKey(), "", entry.getValue());
        }

        return new Locale(name, locale);
    }

    private void recurseLoad(Map<IResourceName, String> locale, String localeName, String domain, String name, JsonElement element){
        if(element.isJsonPrimitive()){
            String key = domain+Constants.RESOURCE_SEPARATOR+name;
            String value = element.getAsJsonPrimitive().getAsString();

            locale.put(RockBottomAPI.createRes(key), value);
            RockBottomAPI.logger().config("Added localization "+key+" -> "+value+" to locale with name "+localeName);
        }
        else{
            for(Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()){
                String key = entry.getKey();

                String newName;
                if(name.isEmpty()){
                    newName = key;
                }
                else{
                    if("*".equals(key)){
                        newName = name.substring(0, name.length()-1);
                    }
                    else{
                        //TODO Deprecated check, the stuff in the if should be the "actual" code
                        if(name.endsWith(".")){
                            newName = name+key;
                        }
                        else{
                            newName = name+"."+key;

                            RockBottomAPI.logger().warning("Locale with name "+localeName+" is still using dotless notation in entry "+newName+"! It should be changed to dotted notation where every category ends with a . while every actual entry doesn't!");
                        }
                    }
                }

                this.recurseLoad(locale, localeName, domain, newName, entry.getValue());
            }
        }
    }

    private Locale merge(Locale locale, Locale otherLocale){
        String name = locale.getName();
        if(name.equals(otherLocale.getName())){
            Map<IResourceName, String> other = otherLocale.getLocalization();

            Map<IResourceName, String> newLocale = new HashMap<>();
            newLocale.putAll(locale.getLocalization());
            newLocale.putAll(other);

            RockBottomAPI.logger().config("Merged locale "+name+" with "+other.size()+" bits of additional localization information");
            return new Locale(name, newLocale);
        }
        else{
            return null;
        }
    }
}
