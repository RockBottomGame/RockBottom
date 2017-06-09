package de.ellpeck.rockbottom.api.mod;

import de.ellpeck.rockbottom.api.util.reg.IResourceName;

import java.io.File;
import java.net.URLClassLoader;

public interface IModLoader{

    void loadModsFromDir(File dir);

    void preInit();

    void init();

    void postInit();

    IResourceName createResourceName(IMod mod, String resource);

    IResourceName createResourceName(String combined);

    URLClassLoader getClassLoader();
}
