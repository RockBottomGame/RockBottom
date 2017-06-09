package de.ellpeck.rockbottom.api.mod;

import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.io.File;

public interface IModLoader{

    void loadModsFromDir(File dir);

    void sortMods();

    void preInit();

    void init();

    void postInit();

    void makeAssets();

    IResourceName createResourceName(IMod mod, String resource);

    IResourceName createResourceName(String combined);
}
