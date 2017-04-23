package de.ellpeck.rockbottom.data.settings;

import java.util.Properties;

public interface IPropSettings{

    void load(Properties props);

    void save(Properties props);
}
