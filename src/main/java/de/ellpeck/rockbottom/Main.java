package de.ellpeck.rockbottom;

import sun.security.action.GetPropertyAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;

public final class Main{

    private static final File JAVA_TEMP = new File(AccessController.doPrivileged(new GetPropertyAction("java.io.tmpdir")));
    private static final File TEMP_DIR = new File(JAVA_TEMP, "rockbottom");

    public static CustomClassLoader classLoader;

    public static void main(String[] args){
        try{
            URLClassLoader loader = (URLClassLoader)Main.class.getClassLoader();

            classLoader = new CustomClassLoader(loader.getURLs(), loader);
            Thread.currentThread().setContextClassLoader(classLoader);

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
            if(!TEMP_DIR.exists()){
                TEMP_DIR.mkdirs();
            }

            File temp = new File(TEMP_DIR, libName);
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
