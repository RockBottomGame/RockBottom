package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.log.Logging;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;

public final class Main{

    public static CustomClassLoader classLoader;

    public static File gameDir;
    public static File nativeDir;
    public static File unpackedModsDir;

    public static boolean skipIntro;
    public static int width;
    public static int height;
    public static boolean fullscreen;

    public static boolean isDedicatedServer;
    public static int port;

    public static void main(String[] args){
        try{
            OptionParser parser = new OptionParser();
            parser.allowsUnrecognizedOptions();
            OptionSpec<String> optionLogLevel = parser.accepts("logLevel").withRequiredArg().ofType(String.class).defaultsTo(Level.INFO.getName());
            File defaultGameDir = new File(".", "rockbottom");
            OptionSpec<File> optionGameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(defaultGameDir);
            OptionSpec<File> optionTempDir = parser.accepts("nativeDir").withRequiredArg().ofType(File.class).defaultsTo(new File(defaultGameDir, "lib"));
            OptionSpec<File> optionUnpackedDir = parser.accepts("unpackedModsDir").withRequiredArg().ofType(File.class);
            OptionSpec<Integer> optionWidth = parser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(1280);
            OptionSpec<Integer> optionHeight = parser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(720);
            OptionSpec<Integer> optionPort = parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
            OptionSpec optionSkipIntro = parser.accepts("skipIntro");
            OptionSpec optionFullscreen = parser.accepts("fullscreen");
            OptionSpec optionServer = parser.accepts("server");
            OptionSpec optionIgnored = parser.nonOptions();

            OptionSet options = parser.parse(args);

            gameDir = options.valueOf(optionGameDir);
            Logging.createMain(options.valueOf(optionLogLevel));

            Logging.mainLogger.info("Found launch args "+Arrays.toString(args));
            Logging.mainLogger.info("Ignoring unrecognized launch args "+options.valuesOf(optionIgnored));

            Logging.mainLogger.info("Setting log level to "+Logging.mainLogger.getLevel());
            Logging.mainLogger.info("Setting game folder to "+gameDir);

            isDedicatedServer = options.has(optionServer);
            port = options.valueOf(optionPort);

            if(!isDedicatedServer){
                nativeDir = options.valueOf(optionTempDir);
                Logging.mainLogger.info("Setting native library folder to "+nativeDir);
            }

            unpackedModsDir = options.valueOf(optionUnpackedDir);
            if(unpackedModsDir != null){
                Logging.mainLogger.info("Setting unpacked mods folder to "+unpackedModsDir);
            }

            skipIntro = options.has(optionSkipIntro);
            width = options.valueOf(optionWidth);
            height = options.valueOf(optionHeight);
            fullscreen = options.has(optionFullscreen);

            try{
                URLClassLoader loader = (URLClassLoader)Main.class.getClassLoader();

                classLoader = new CustomClassLoader(loader.getURLs(), loader);
                Thread.currentThread().setContextClassLoader(classLoader);

                Logging.mainLogger.info("Replacing class loader "+loader+" with new loader "+classLoader);

                loader.close();
            }
            catch(Exception e){
                throw new RuntimeException("Failed to override original class loader", e);
            }

            try{
                String className = isDedicatedServer ? "RockBottomServer" : "RockBottom";
                Class gameClass = Class.forName("de.ellpeck.rockbottom.init."+className, false, classLoader);
                Method method = gameClass.getMethod("startGame");
                method.invoke(null);
            }
            catch(NoSuchMethodException | IllegalAccessException | ClassNotFoundException e){
                throw new RuntimeException("There was an error initializing the game", e);
            }
            catch(InvocationTargetException e){
                Throwable target = e.getTargetException();
                throw new RuntimeException("There was an exception running the game", target == null ? e : target);
            }
        }
        catch(Throwable t){
            File dir = new File(gameDir, "crashes");
            if(!dir.exists()){
                dir.mkdirs();
            }

            File file = new File(dir, new SimpleDateFormat("dd.MM.yy_HH.mm.ss").format(new Date())+".txt");

            if(Logging.mainLogger != null){
                Logging.mainLogger.log(Level.SEVERE, "Detected game crash, saving report to "+file, t);
            }

            try{
                PrintWriter writer = new PrintWriter(file);
                t.fillInStackTrace().printStackTrace(writer);
                writer.flush();
            }
            catch(Exception e2){
                if(Logging.mainLogger != null){
                    Logging.mainLogger.log(Level.SEVERE, "Couldn't save crash report to "+file, e2);
                }
            }

            System.exit(1);
        }
    }

    private static String loadLib(String libName){
        if(nativeDir != null){
            try{
                String mapped = System.mapLibraryName(libName);

                if(!nativeDir.exists()){
                    nativeDir.mkdirs();
                }

                File file = new File(nativeDir, mapped);
                if(file.exists()){
                    Logging.mainLogger.info("Using native library cache file "+file);
                    return file.getAbsolutePath();
                }
                else{
                    Logging.mainLogger.info("Creating native library cache file "+file);

                    InputStream in = classLoader.getResourceAsStream(mapped);
                    FileOutputStream out = new FileOutputStream(file);
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

                    return file.getAbsolutePath();
                }
            }
            catch(Exception e){
                throw new RuntimeException("Couldn't load native library with name "+libName, e);
            }
        }
        else{
            throw new UnsupportedOperationException("Tried loading native library "+libName+" with the native library folder being null! This is likely due to the dedicated server trying to load a native library which is disallowed!");
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
            String lib = loadLib(libName);

            if(lib != null && !lib.isEmpty()){
                return lib;
            }
            else{
                return super.findLibrary(libName);
            }
        }
    }
}
