package de.ellpeck.rockbottom.util;

import java.io.File;
import java.nio.file.Paths;

public enum OSType {
    
    WINDOWS(File.separator + "Documents" + File.separator + "RockBottom"),
    LINUX("~" + File.separator + ".local" + File.separator + "RockBottom"),
    MACOS("~" + File.separator + "Documents" + File.separator + "RockBottom"),
    OTHER("." + File.separator + "RockBottom");

    public final String defaultGamePath;

    OSType(String defaultGamePath) {
        this.defaultGamePath = defaultGamePath;
    }
}
