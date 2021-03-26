package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.log.Logging;
import de.ellpeck.rockbottom.util.CrashManager;
import de.ellpeck.rockbottom.util.OSType;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.logging.Level;

public final class Main {

    public static byte[] memReserve = new byte[10 * 1024 * 1024];
    public static CustomClassLoader classLoader;
    public static Thread mainThread;

    public static OSType OS;
    public static File gameDir;
    public static File unpackedModsDir;

    public static boolean debugMode;
    public static boolean skipIntro;
    public static int width;
    public static int height;
    public static boolean fullscreen;
    public static int vertexCache;
    public static boolean saveTextureSheet;

    public static boolean isDedicatedServer;
    public static int port;

    public static void main(String[] args) {
        try {
            OS = getOS();
            OptionParser parser = new OptionParser();
            parser.allowsUnrecognizedOptions();
            OptionSpec<String> optionLogLevel = parser.accepts("logLevel").withRequiredArg().ofType(String.class).defaultsTo(Level.INFO.getName());
            File defaultGameDir = new File(OS.defaultGamePath);
            OptionSpec<File> optionGameDir = parser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(defaultGameDir);
            OptionSpec<File> optionUnpackedDir = parser.accepts("unpackedModsDir").withRequiredArg().ofType(File.class);
            OptionSpec<Integer> optionWidth = parser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(1280);
            OptionSpec<Integer> optionHeight = parser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(720);
            OptionSpec<Integer> optionVertexCache = parser.accepts("vertexCache").withRequiredArg().ofType(Integer.class).defaultsTo(65536);
            OptionSpec<Integer> optionPort = parser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(8000);
            OptionSpec optionDebugMode = parser.accepts("debugMode");
            OptionSpec optionSkipIntro = parser.accepts("skipIntro");
            OptionSpec optionFullscreen = parser.accepts("fullscreen");
            OptionSpec optionSaveTextureSheet = parser.accepts("saveTextureSheet");
            OptionSpec optionServer = parser.accepts("server");
            OptionSpec optionIgnored = parser.nonOptions();

            OptionSet options = parser.parse(args);

            gameDir = options.valueOf(optionGameDir);
            Logging.init(options.valueOf(optionLogLevel));

            Logging.mainLogger.info("Using Java version " + System.getProperty("java.version"));

            Logging.mainLogger.info("Found launch args " + Arrays.toString(args));
            Logging.mainLogger.info("Ignoring unrecognized launch args " + options.valuesOf(optionIgnored));

            Logging.mainLogger.info("Setting log level to " + Logging.mainLogger.getLevel());
            Logging.mainLogger.info("Setting game folder to " + gameDir);

            isDedicatedServer = options.has(optionServer);
            port = options.valueOf(optionPort);

            unpackedModsDir = options.valueOf(optionUnpackedDir);
            if (unpackedModsDir != null) {
                Logging.mainLogger.info("Setting unpacked mods folder to " + unpackedModsDir);
            }

            debugMode = options.has(optionDebugMode);
            skipIntro = options.has(optionSkipIntro);
            width = options.valueOf(optionWidth);
            height = options.valueOf(optionHeight);
            vertexCache = options.valueOf(optionVertexCache);
            fullscreen = options.has(optionFullscreen);
            saveTextureSheet = options.has(optionSaveTextureSheet);

            Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
                throw new RuntimeException("There was an unhandled exception in thread " + t.getName(), e);
            });
            mainThread = Thread.currentThread();

            try {
                ClassLoader loader = Main.class.getClassLoader();

                URL[] urls;
                if (loader instanceof URLClassLoader) {
                    urls = ((URLClassLoader) loader).getURLs();
                } else {
                    String classPath = appendPath(System.getProperty("java.class.path"), System.getProperty("env.class.path"));
                    urls = pathToURLs(classPath);
                }

                classLoader = new CustomClassLoader(urls, loader);
                mainThread.setContextClassLoader(classLoader);

                Logging.mainLogger.info("Replacing class loader " + loader + " with new loader " + classLoader);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to override original class loader", e);
            }

            try {
                String className = isDedicatedServer ? "RockBottomServer" : "RockBottom";
                Class gameClass = Class.forName("de.ellpeck.rockbottom.init." + className, false, classLoader);
                Method method = gameClass.getMethod("startGame");
                method.invoke(null);
            } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException e) {
                throw new RuntimeException("There was an error initializing the game", e);
            } catch (InvocationTargetException e) {
                Throwable target = e.getTargetException();
                throw target == null ? e : target;
            }
        } catch (Throwable t) {
            CrashManager.makeCrashReport(t);
            System.exit(1);
        }
        System.exit(0);
    }

    private static OSType getOS() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        if (osName.contains("win")) {
            return OSType.WINDOWS;
        }
        if (osName.contains("mac")) {
            return OSType.MACOS;
        }
        if (osName.contains("nux")) {
            return OSType.LINUX;
        }

        return OSType.OTHER;
    }

    private static URL fileToURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            Logging.mainLogger.log(Level.WARNING, "Couldn't convert " + file + " to URL", e);
            return null;
        }
    }

    private static String appendPath(String pathTo, String pathFrom) {
        if (pathTo == null || pathTo.isEmpty()) {
            return pathFrom;
        } else if (pathFrom == null || pathFrom.isEmpty()) {
            return pathTo;
        } else {
            return pathTo + File.pathSeparator + pathFrom;
        }
    }

    private static URL[] pathToURLs(String path) {
        StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
        URL[] urls = new URL[tokenizer.countTokens()];

        int count = 0;
        while (tokenizer.hasMoreTokens()) {
            URL url = fileToURL(new File(tokenizer.nextToken()));
            if (url != null) {
                urls[count] = url;
                count++;
            }
        }

        if (urls.length != count) {
            URL[] tmp = new URL[count];
            System.arraycopy(urls, 0, tmp, 0, count);
            urls = tmp;
        }

        return urls;
    }

    public static class CustomClassLoader extends URLClassLoader {

        public CustomClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        public void addURL(URL url) {
            super.addURL(url);
        }
    }
}