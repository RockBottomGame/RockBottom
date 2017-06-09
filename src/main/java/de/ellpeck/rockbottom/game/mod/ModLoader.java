package de.ellpeck.rockbottom.game.mod;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.game.apiimpl.ResourceName;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModLoader implements IModLoader{

    private final CustomClassLoader classLoader;
    private final List<IMod> loadedMods = new ArrayList<>();

    public ModLoader(){
        ClassLoader loader = this.getClass().getClassLoader();
        if(loader instanceof URLClassLoader){
            URLClassLoader original = (URLClassLoader)loader;

            this.classLoader = new CustomClassLoader(original.getURLs(), original);
            Thread.currentThread().setContextClassLoader(this.classLoader);
        }
        else{
            throw new RuntimeException("Class loader "+loader+" is not an URL class loader! This shouldn't happen!");
        }
    }

    @Override
    public void loadModsFromDir(File dir){
        if(!dir.exists()){
            dir.mkdirs();
            Log.info("Mods folder not found, creating at "+dir);
        }
        else{
            Log.info("Loading mods from mods folder "+dir);

            for(File file : dir.listFiles()){
                String name = file.getName();
                if(name != null && name.endsWith(".jar")){
                    try{
                        JarFile jar = new JarFile(file);
                        Enumeration<JarEntry> entries = jar.entries();

                        this.classLoader.addURL(file.toURI().toURL());

                        boolean foundMod = false;
                        while(entries.hasMoreElements()){
                            JarEntry entry = entries.nextElement();
                            String entryName = entry.getName();

                            if(entryName != null && entryName.endsWith(".class") && !entryName.contains("$")){
                                String actualClassName = entryName.substring(0, entryName.length()-6).replace("/", ".");
                                Class aClass = Class.forName(actualClassName, false, this.classLoader);

                                if(aClass != null && !aClass.isInterface()){
                                    if(IMod.class.isAssignableFrom(aClass)){
                                        IMod instance = (IMod)aClass.newInstance();

                                        this.loadedMods.add(instance);

                                        Log.info("Loaded mod "+instance.getDisplayName()+" with id "+instance.getId()+" and version "+instance.getVersion());

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

            Log.info("Loaded a total of "+this.loadedMods.size()+" mods");
        }
    }

    @Override
    public void preInit(){
        IGameInstance game = RockBottomAPI.getGame();
        IAssetManager assets = game.getAssetManager();

        for(IMod mod : this.loadedMods){
            assets.addAssetProp(mod, mod.getResourceLocation());

            mod.preInit(game, assets, RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
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
    public IResourceName createResourceName(IMod mod, String resource){
        return new ResourceName(mod == null ? null : mod.getId(), resource);
    }

    @Override
    public IResourceName createResourceName(String combined){
        return new ResourceName(combined);
    }

    @Override
    public URLClassLoader getClassLoader(){
        return this.classLoader;
    }

    private static class CustomClassLoader extends URLClassLoader{

        public CustomClassLoader(URL[] urls, ClassLoader parent){
            super(urls, parent);
        }

        @Override
        public void addURL(URL url){
            super.addURL(url);
        }
    }
}
