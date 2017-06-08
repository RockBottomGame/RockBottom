package de.ellpeck.rockbottom.api.data.settings;

import de.ellpeck.rockbottom.api.data.IDataManager;

import java.io.File;
import java.util.Properties;

public interface IPropSettings{

    void load(Properties props);

    void save(Properties props);

    File getFile(IDataManager manager);

    String getName();
}
