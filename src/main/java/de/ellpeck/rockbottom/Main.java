package de.ellpeck.rockbottom;


import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public final class Main{

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
        public void addURL(URL url) {
            super.addURL(url);
        }

        @Override
        protected String findLibrary(String libname) {
            String libName = System.mapLibraryName(libname);
            InputStream stream = getResourceAsStream("natives/" + libName);
            if (stream != null) {
                String s = loadLib(stream, libName);
                if(s != null){
                    return s;
                }
            }
            return super.findLibrary(libname);
        }
    }

    public static String loadLib(InputStream in, String libName){
        try {
            File temp = File.createTempFile(libName, "");
            FileOutputStream out = new FileOutputStream(temp);
            byte[] buffer = new byte[65536];
            int bufferSize;
            while ((bufferSize = in.read(buffer, 0, buffer.length)) != -1) {
                out.write(buffer, 0, bufferSize);
            }
            return temp.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
