package de.ellpeck.rockbottom.util;

public enum OSType {
    WINDOWS("\\Documents\\RockBottom\\"),
    LINUX("~\\.local\\RockBottom"),
    MACOS("~\\Documents\\RockBottom"),
    OTHER(".\\RockBottom");

    public final String defaultGamePath;

    OSType(String defaultGamePath) {
        this.defaultGamePath = defaultGamePath;
    }
}
