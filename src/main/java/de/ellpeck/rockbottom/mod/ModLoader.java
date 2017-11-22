package de.ellpeck.rockbottom.mod;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.settings.ModSettings;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.mod.IModLoader;
import de.ellpeck.rockbottom.api.util.Counter;
import de.ellpeck.rockbottom.api.util.reg.IResourceName;
import de.ellpeck.rockbottom.apiimpl.ResourceName;

import java.io.File;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

public class ModLoader implements IModLoader{

    private final List<IMod> allMods = new ArrayList<>();
    private final List<IMod> activeMods = new ArrayList<>();
    private final List<IMod> disabledMods = new ArrayList<>();

    private final ModSettings modSettings = new ModSettings();

    public ModLoader(){
        IGameInstance game = RockBottomAPI.getGame();
        this.allMods.add(game);
        this.activeMods.add(game);
    }

    @Override
    public void loadJarMods(File dir){
        RockBottomAPI.getGame().getDataManager().loadPropSettings(this.modSettings);

        if(!dir.exists()){
            dir.mkdirs();
            RockBottomAPI.logger().info("Mods folder not found, creating at "+dir);
        }
        else{
            int amount = 0;

            RockBottomAPI.logger().info("Loading jar mods from mods folder "+dir);

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

                            if(this.findMod(entryName)){
                                amount++;

                                foundMod = true;
                                break;
                            }
                        }

                        if(!foundMod){
                            RockBottomAPI.logger().warning("Jar file "+file+" doesn't contain a valid mod");
                        }
                    }
                    catch(Exception e){
                        RockBottomAPI.logger().log(Level.WARNING, "Loading jar mod from file "+file+" failed", e);
                    }
                }
                else{
                    RockBottomAPI.logger().warning("Found non-jar file "+file+" in mods folder "+dir);
                }
            }

            RockBottomAPI.logger().info("Loaded a total of "+amount+" jar mods");
        }
    }

    @Override
    public void loadUnpackedMods(File dir){
        if(dir.exists()){
            RockBottomAPI.logger().info("Loading unpacked mods from folder "+dir);

            Counter amount = new Counter(0);
            this.recursiveLoad(dir, dir.listFiles(), amount);

            RockBottomAPI.logger().info("Loaded a total of "+amount.get()+" unpacked mods");
        }
        else{
            RockBottomAPI.logger().info("Not loading unpacked mods from folder "+dir+" as it doesn't exist");
        }
    }

    private void recursiveLoad(File original, File[] files, Counter amount){
        for(File file : files){
            if(file.isDirectory()){
                this.recursiveLoad(original, file.listFiles(), amount);
            }
            else{
                String name = file.getAbsolutePath();
                if(name != null && name.endsWith(".class")){
                    try{
                        Main.classLoader.addURL(file.toURI().toURL());

                        if(this.findMod(name.replace(original.getAbsolutePath(), "").replace(File.separator, ".").replaceFirst(".", ""))){
                            amount.add(1);
                        }
                    }
                    catch(Exception e){
                        RockBottomAPI.logger().log(Level.WARNING, "Loading unpacked mod from file "+file+" failed", e);
                    }
                }
                else{
                    RockBottomAPI.logger().warning("Found non-class file "+file+" in unpacked mods folder "+original);
                }
            }
        }
    }

    private boolean findMod(String className) throws Exception{
        if(className != null && className.endsWith(".class") && !className.contains("$")){
            String actualClassName = className.substring(0, className.length()-6).replace("/", ".");
            Class aClass = Class.forName(actualClassName, false, Main.classLoader);

            if(aClass != null && !aClass.isInterface()){
                if(IMod.class.isAssignableFrom(aClass)){
                    IMod instance = (IMod)aClass.newInstance();
                    String id = instance.getId();

                    if(id != null && !id.isEmpty() && id.toLowerCase(Locale.ROOT).equals(id) && id.replaceAll(" ", "").equals(id)){
                        if(this.getMod(id) == null){
                            if(this.modSettings.isDisabled(id)){
                                this.disabledMods.add(instance);
                                RockBottomAPI.logger().info("Mod "+instance.getDisplayName()+" with id "+id+" and version "+instance.getVersion()+" is marked as disabled in the mod settings");
                            }
                            else{
                                this.activeMods.add(instance);
                                RockBottomAPI.logger().info("Loaded mod "+instance.getDisplayName()+" with id "+id+" and version "+instance.getVersion());
                            }

                            this.allMods.add(instance);
                            return true;
                        }
                        else{
                            RockBottomAPI.logger().warning("Cannot load mod "+instance.getDisplayName()+" with id "+id+" and version "+instance.getVersion()+" because a mod with that id is already present");
                        }
                    }
                    else{
                        RockBottomAPI.logger().warning("Cannot load mod "+instance.getDisplayName()+" with id "+id+" and version "+instance.getVersion()+" because the id is either missing, empty, not all lower case or contains spaces");
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void sortMods(){
        RockBottomAPI.logger().info("Sorting mods");

        Comparator comp = Comparator.comparingInt(IMod:: getSortingPriority).reversed();
        this.allMods.sort(comp);
        this.activeMods.sort(comp);
        this.disabledMods.sort(comp);

        RockBottomAPI.logger().info("----- Loaded Mods -----");
        for(IMod mod : this.allMods){
            String s = mod.getDisplayName()+" @ "+mod.getVersion()+" ("+mod.getId()+")";
            if(this.modSettings.isDisabled(mod.getId())){
                s += " [DISABLED]";
            }
            RockBottomAPI.logger().info(s);
        }
        RockBottomAPI.logger().info("-----------------------");
    }

    @Override
    public void prePreInit(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.activeMods){
            mod.prePreInit(game, RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
        }
    }

    @Override
    public void preInit(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.activeMods){
            mod.preInit(game, RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
        }
    }

    @Override
    public void init(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.activeMods){
            mod.init(game, RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
        }
    }

    @Override
    public void initAssets(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.activeMods){
            mod.initAssets(game, game.getAssetManager(), RockBottomAPI.getApiHandler());
        }
    }

    @Override
    public void postInit(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.activeMods){
            mod.postInit(game, RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
        }
    }

    @Override
    public void postPostInit(){
        IGameInstance game = RockBottomAPI.getGame();
        for(IMod mod : this.activeMods){
            mod.postPostInit(game, RockBottomAPI.getApiHandler(), RockBottomAPI.getEventHandler());
        }
    }

    @Override
    public IMod getMod(String id){
        for(IMod mod : this.allMods){
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
    public List<IMod> getAllTheMods(){
        return Collections.unmodifiableList(this.allMods);
    }

    @Override
    public List<IMod> getActiveMods(){
        return Collections.unmodifiableList(this.activeMods);
    }

    @Override
    public List<IMod> getDisabledMods(){
        return Collections.unmodifiableList(this.disabledMods);
    }

    @Override
    public ModSettings getModSettings(){
        return this.modSettings;
    }
}
