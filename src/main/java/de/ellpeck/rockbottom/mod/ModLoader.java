package de.ellpeck.rockbottom.mod;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.apiimpl.ResourceName;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModLoader implements IModLoader{

    private final List<IMod> loadedMods = new ArrayList<>();

    public ModLoader(){
        this.loadedMods.add(RockBottomAPI.getGame());
    }

    @Override
    public void loadModsFromDir(File dir){
        if(!dir.exists()){
            dir.mkdirs();
            Log.info("Mods folder not found, creating at "+dir);
        }
        else{
            int loadedAmount = 0;

            Log.info("Loading mods from mods folder "+dir);

            for(File file : dir.listFiles()){
                String name = file.getName();
                if(name != null && name.endsWith(".jar")){
                    try{
                        JarFile jar = new JarFile(file);
                        Enumeration<JarEntry> entries = jar.entries();

                        Main.classLoader.addURL(file.toURI().toURL());

                        boolean foundMod = false;
                        while(entries.hasMoreElements()){
                            JarEntry entry = entries.nextElement();
                            String entryName = entry.getName();

                            if(entryName != null && entryName.endsWith(".class") && !entryName.contains("$")){
                                String actualClassName = entryName.substring(0, entryName.length()-6).replace("/", ".");
                                Class aClass = Class.forName(actualClassName, false, Main.classLoader);

                                if(aClass != null && !aClass.isInterface()){
                                    if(IMod.class.isAssignableFrom(aClass)){
                                        IMod instance = (IMod)aClass.newInstance();
                                        String id = instance.getId();

                                        if(id != null && !id.isEmpty() && id.toLowerCase(Locale.ROOT).equals(id) && id.replaceAll(" ", "").equals(id)){
                                            if(this.getMod(id) == null){
                                                this.loadedMods.add(instance);
                                                loadedAmount++;

                                                Log.info("Loaded mod "+instance.getDisplayName()+" with id "+id+" and version "+instance.getVersion());
                                            }
                                            else{
                                                Log.error("Cannot load mod "+instance.getDisplayName()+" with id "+id+" and version "+instance.getVersion()+" because a mod with that id is already present");
                                            }
                                        }
                                        else{
                                            Log.error("Cannot load mod "+instance.getDisplayName()+" with id "+id+" and version "+instance.getVersion()+" because the id is either missing, empty, not all lower case or contains spaces");
                                        }

                                        foundMod = true;
                                        break;
                                    }
                                }
                            }
                        }

                        if(!foundMod){
                            Log.warn("Found jar file "+file+" that doesn't contain a mod");
                        }
                    }
                    catch(Exception e){
                        Log.error("Loading mod from file "+file+" failed", e);
                    }
                }
                else{
                    Log.warn("Found non-jar file "+file+" in mods folder "+dir);
                }
            }

            Log.info("Loaded a total of "+loadedAmount+" mods");
        }
    }

    @Override
    public void sortMods(){
        Log.info("Sorting mods");

        this.loadedMods.sort(Comparator.comparingInt(IMod:: getSortingPriority).reversed());

        Log.info("----- Loaded Mods -----");
        for(IMod mod : this.loadedMods){
            Log.info(mod.getDisplayName()+" @ "+mod.getVersion()+" ("+mod.getId()+")");
        }
        Log.info("-----------------------");
    }

    @Override
    public void preInit(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.loadedMods){
            mod.preInit(game, RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
        }
    }

    @Override
    public void init(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.loadedMods){
            mod.init(game, game.getAssetManager(), RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
        }
    }

    @Override
    public void postInit(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.loadedMods){
            mod.postInit(game, game.getAssetManager(), RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
        }
    }

    @Override
    public void makeAssets(){
        for(IMod mod : this.loadedMods){
            RockBottomAPI.getGame().getAssetManager().addAssetProp(mod, mod.getResourceLocation());
        }
    }

    @Override
    public IMod getMod(String id){
        for(IMod mod : this.loadedMods){
            if(mod.getId().equals(id)){
                return mod;
            }
        }
        return null;
    }

    @Override
    public IResourceName createResourceName(IMod mod, String resource){
        return new ResourceName(mod.getId(), resource);
    }

    @Override
    public IResourceName createResourceName(String combined){
        return new ResourceName(combined);
    }

    @Override
    public List<IMod> getAllMods(){
        return Collections.unmodifiableList(this.loadedMods);
    }
}
