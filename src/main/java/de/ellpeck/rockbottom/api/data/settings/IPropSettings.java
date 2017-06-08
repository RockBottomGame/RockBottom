package de.ellpeck.rockbottom.api.data.settings;

import de.ellpeck.rockbottom.game.data.DataManager;

import java.io.File;
import java.util.Properties;

public interface IPropSettings{

    void load(Properties props);

    void save(Properties props);

    File getFile(DataManager manager);

    String getName();
}
