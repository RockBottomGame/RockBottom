package de.ellpeck.rockbottom.api.data;

import de.ellpeck.rockbottom.api.data.settings.IPropSettings;

import java.io.File;

public interface IDataManager{

    File getGameDir();

    File getWorldsDir();

    File getGameDataFile();

    File getSettingsFile();

    File getCommandPermsFile();

    void loadPropSettings(IPropSettings settings);

    void savePropSettings(IPropSettings settings);

}
