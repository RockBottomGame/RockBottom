package de.ellpeck.rockbottom.api.mod;

import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.io.File;
import java.util.List;

public interface IModLoader{

    void loadModsFromDir(File dir);

    void sortMods();

    void preInit();

    void init();

    void postInit();

    void makeAssets();

    IMod getMod(String id);

    IResourceName createResourceName(IMod mod, String resource);

    IResourceName createResourceName(String combined);

    List<IMod> getAllMods();
}
