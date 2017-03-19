package de.ellpeck.game.util;

import de.ellpeck.game.Constants;

public final class CommonUtil{

    public static String getExtension(String fileName){
        int index = fileName.lastIndexOf(Constants.FILE_EXTENSION_SEPARATOR);
        if(index > 0){
            return fileName.substring(index+1);
        }
        return null;
    }

    public static String getExtensionlessName(String fileName){
        int index = fileName.lastIndexOf(Constants.FILE_EXTENSION_SEPARATOR);
        if(index > 0){
            return fileName.substring(0, index);
        }
        return fileName;
    }
}
