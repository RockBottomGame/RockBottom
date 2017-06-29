package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.util.Util;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;

import java.util.Scanner;
import java.util.UUID;

public class ContainerServer extends Container{

    public ContainerServer(RockBottomServer game) throws SlickException{
        super(game);
    }

    @Override
    protected void setup() throws SlickException{
        this.doSetup();

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
                return Util.colorToFormattingCode(Color.red);
            }

            @Override
            public void sendMessageTo(IChatLog chat, String message){
                Log.info(message);
            }
        };

        Thread consoleThread = new Thread(() -> {
            while(this.running()){
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();

                this.game.scheduleAction(() -> {
                    this.game.chatLog.sendCommandSenderMessage(input, consoleCommandSender);
                    return true;
                });
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }

    @Override
    protected void gameLoop() throws SlickException{
        this.doGameLoop();
    }

    @Override
    protected void updateAndRender(int delta) throws SlickException{
        this.doUpdate(delta);
    }

    @Override
    public void setDisplayMode(int width, int height, boolean fullscreen) throws SlickException{

    }
}
