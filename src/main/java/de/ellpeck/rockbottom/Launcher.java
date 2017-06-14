package de.ellpeck.rockbottom;

import de.ellpeck.rockbottom.util.LogSystem;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class Launcher{

    private static final Random RANDOM = new Random();

    public static void main(String[] args){
        LogSystem.init();

        File tempDir = null;

        try{
            String temp = System.getProperty("java.io.tmpdir");
            tempDir = new File(temp+File.separator+"rockbottom"+RANDOM.nextInt());

            String javaHome = System.getProperty("java.home");
            String javaBin = javaHome+File.separator+"bin"+File.separator+"java";
            String classPath = System.getProperty("java.class.path");
            String className = Main.class.getCanonicalName();

            List<String> arguments = new ArrayList<>();
            arguments.add(javaBin);
            arguments.add("-cp");
            arguments.add(classPath);
            arguments.add(className);

            arguments.addAll(Arrays.asList("--tempDir", tempDir.toString()));
            arguments.addAll(Arrays.asList(args));

            ProcessBuilder builder = new ProcessBuilder(arguments);
            builder.inheritIO();

            Log.info("Launching game");
            Process process = builder.start();
            process.waitFor();
        }
        catch(Exception e){
            throw new RuntimeException("Failed to launch game", e);
        }
        finally{
            if(tempDir != null){
                File[] files = tempDir.listFiles();
                for(File file : files){
                    if(!file.delete()){
                        Log.warn("Couldn't delete temp file "+file);
                    }
                }

                if(tempDir.delete()){
                    Log.info("Deleted temp folder "+tempDir);
                }
                else{
                    Log.warn("Couldn't delete temp folder "+tempDir);
                }
            }
        }
    }
}
