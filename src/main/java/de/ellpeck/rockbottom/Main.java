package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.util.LogSystem;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public final class Main{

    public static CustomClassLoader classLoader;
    public static File gameDir;
    public static File tempDir;

    public static void main(String[] args){
        LogSystem.init();

        Log.info("Found launch args "+Arrays.toString(args));

        try{
            gameDir = new File(args[0]);
            Log.info("Setting game folder to "+gameDir);

            tempDir = new File(args[1]);
            Log.info("Setting temp folder to "+tempDir);

            if(!tempDir.exists()){
                tempDir.mkdirs();
                Log.info("Creating temp folder at "+tempDir);
            }
        }
        catch(Exception e){
            throw new RuntimeException("Couldn't parse launch args", e);
        }

        try{
            URLClassLoader loader = (URLClassLoader)Main.class.getClassLoader();

            classLoader = new CustomClassLoader(loader.getURLs(), loader);
            Thread.currentThread().setContextClassLoader(classLoader);

            Log.info("Replacing class loader "+loader+" with new loader "+classLoader);

            loader.close();
        }
        catch(Exception e){
            throw new RuntimeException("Failed to override original class loader", e);
        }

        try{
            Class gameClass = Class.forName("de.ellpeck.rockbottom.RockBottom", false, classLoader);
            Method method = gameClass.getMethod("init");
            method.invoke(null);
        }
        catch(Exception e){
            throw new RuntimeException("Could not initialize game", e);
        }
    }

    public static class CustomClassLoader extends URLClassLoader{

        public CustomClassLoader(URL[] urls, ClassLoader parent){
            super(urls, parent);
        }

        @Override
        public void addURL(URL url){
            super.addURL(url);
        }

        @Override
        protected String findLibrary(String libName){
            String mapped = System.mapLibraryName(libName);
            InputStream stream = this.getResourceAsStream("natives/"+mapped);
            if(stream != null){
                String lib = loadLib(stream, mapped);
                if(lib != null && !lib.isEmpty()){
                    return lib;
                }
            }
            return super.findLibrary(libName);
        }
    }

    private static String loadLib(InputStream in, String libName){
        try{
            File temp = new File(tempDir, libName);
            if(temp.exists()){
                Log.info("File "+temp+" already exists, using existing version");
            }
            else{
                Log.info("Creating temporary file "+temp);
            }

            FileOutputStream out = new FileOutputStream(temp);
            byte[] buffer = new byte[65536];

            while(true){
                int bufferSize = in.read(buffer, 0, buffer.length);

                if(bufferSize != -1){
                    out.write(buffer, 0, bufferSize);
                }
                else{
                    break;
                }
            }

            out.close();

            return temp.getAbsolutePath();
        }
        catch(IOException e){
            throw new RuntimeException("Couldn't load lib with name "+libName, e);
        }
    }
}
