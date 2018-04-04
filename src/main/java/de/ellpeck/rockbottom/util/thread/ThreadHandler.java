package de.ellpeck.rockbottom.util.thread;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.assets.sound.SoundHandler;
import de.ellpeck.rockbottom.init.AbstractGame;

import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public final class ThreadHandler{

    public static Thread mainThread;
    public static Thread consoleThread;
    public static ChunkThread chunkGenThread;
    public static Thread soundThread;

    public static void init(AbstractGame game){
        mainThread = Thread.currentThread();

        chunkGenThread = new ChunkThread(game);
        chunkGenThread.setDaemon(true);
        chunkGenThread.start();

        soundThread = new Thread(() -> SoundHandler.updateSounds(game), "SoundUpdater");
        soundThread.setDaemon(true);
        soundThread.start();

        if(game.isDedicatedServer()){
            ICommandSender consoleCommandSender = new ICommandSender(){
                @Override
                public int getCommandLevel(){
                    return Constants.ADMIN_PERMISSION;
                }

                @Override
                public String getName(){
                    return "Console";
                }

                @Override
                public UUID getUniqueId(){
                    return UUID.randomUUID();
                }

                @Override
                public String getChatColorFormat(){
                    return FormattingCode.RED.toString();
                }

                @Override
                public void sendMessageTo(IChatLog chat, ChatComponent message){
                    RockBottomAPI.logger().info(message.getUnformattedWithChildren());
                }
            };

            consoleThread = new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while(game.isRunning){
                    try{
                        if(scanner.hasNextLine()){
                            String input = scanner.nextLine();
                            game.enqueueAction((g, object) -> g.getChatLog().sendCommandSenderMessage(input, consoleCommandSender), null);
                        }
                    }
                    catch(Exception e){
                        RockBottomAPI.logger().log(Level.WARNING, "There was an exception in the console thread, but it will attempt to keep running", e);
                    }
                    Util.sleepSafe(1);
                }
                scanner.close();
            }, "ConsoleListener");
            consoleThread.setDaemon(true);
            consoleThread.start();
        }
    }
}
