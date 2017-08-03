package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.Constants;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import org.newdawn.slick.Color;
import org.newdawn.slick.Input;
import org.newdawn.slick.util.Log;

import java.io.File;
import java.util.Scanner;
import java.util.UUID;

public class RockBottomServer extends AbstractGame{

    public static void startGame(){
        doInit(new RockBottomServer());
    }

    @Override
    public void init(){
        super.init();

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
            public void sendMessageTo(IChatLog chat, ChatComponent message){
                Log.info(message.getUnformattedWithChildren());
            }
        };

        Thread consoleThread = new Thread(() -> {
            while(this.isRunning){
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();

                this.scheduleAction(() -> {
                    this.chatLog.sendCommandSenderMessage(input, consoleCommandSender);
                    return true;
                });
            }
        }, "ConsoleListener");
        consoleThread.setDaemon(true);
        consoleThread.start();

        File file = new File(this.dataManager.getWorldsDir(), "world_server");

        WorldInfo info = new WorldInfo(file);
        if(file.isDirectory()){
            info.load();
        }

        this.startWorld(file, info);

        try{
            RockBottomAPI.getNet().init(null, Main.port, true);
        }
        catch(Exception e){
            Log.error("Couldn't start server", e);
            this.exit();
        }
    }

    @Override
    public int getAutosaveInterval(){
        return 60;
    }

    @Override
    public void joinWorld(DataSet playerSet, WorldInfo info, DynamicRegistryInfo regInfo){
        throw new UnsupportedOperationException("Cannot join a world on a dedicated server");
    }

    @Override
    public void openIngameMenu(){
        throw new UnsupportedOperationException("Cannot open the ingame menu on a dedicated server");
    }

    @Override
    public int getGuiScale(){
        throw new UnsupportedOperationException("Cannot get gui scale on a dedicated server");
    }

    @Override
    public int getWorldScale(){
        throw new UnsupportedOperationException("Cannot get world scale on a dedicated server");
    }

    @Override
    public double getWidthInWorld(){
        throw new UnsupportedOperationException("Cannot get the width in the world on a dedicated server");
    }

    @Override
    public double getHeightInWorld(){
        throw new UnsupportedOperationException("Cannot get the height in the world on a dedicated server");
    }

    @Override
    public double getWidthInGui(){
        throw new UnsupportedOperationException("Cannot get the width in the gui on a dedicated server");
    }

    @Override
    public double getHeightInGui(){
        throw new UnsupportedOperationException("Cannot get the height in the gui on a dedicated server");
    }

    @Override
    public float getMouseInGuiX(){
        throw new UnsupportedOperationException("Cannot get mouse coordinates on a dedicated server");
    }

    @Override
    public float getMouseInGuiY(){
        throw new UnsupportedOperationException("Cannot get mouse coordinates on a dedicated server");
    }

    @Override
    public AbstractEntityPlayer getPlayer(){
        throw new UnsupportedOperationException("Cannot get the player on a dedicated server");
    }

    @Override
    public IGuiManager getGuiManager(){
        throw new UnsupportedOperationException("Cannot get the gui manager on a dedicated server");
    }

    @Override
    public IInteractionManager getInteractionManager(){
        throw new UnsupportedOperationException("Cannot get the interaction manager on a dedicated server");
    }

    @Override
    public IAssetManager getAssetManager(){
        throw new UnsupportedOperationException("Cannot get the asset manager on a dedicated server");
    }

    @Override
    public IParticleManager getParticleManager(){
        throw new UnsupportedOperationException("Cannot get the particle manager on a dedicated server");
    }

    @Override
    public UUID getUniqueId(){
        throw new UnsupportedOperationException("Cannot get the unique id on a dedicated server");
    }

    @Override
    public boolean isDebug(){
        throw new UnsupportedOperationException("Cannot get debug mode on a dedicated server");
    }

    @Override
    public boolean isLightDebug(){
        throw new UnsupportedOperationException("Cannot get debug mode on a dedicated server");
    }

    @Override
    public boolean isForegroundDebug(){
        throw new UnsupportedOperationException("Cannot get debug mode on a dedicated server");
    }

    @Override
    public boolean isBackgroundDebug(){
        throw new UnsupportedOperationException("Cannot get debug mode on a dedicated server");
    }

    @Override
    public boolean isItemInfoDebug(){
        throw new UnsupportedOperationException("Cannot get debug mode on a dedicated server");
    }

    @Override
    public boolean isChunkBorderDebug(){
        throw new UnsupportedOperationException("Cannot get debug mode on a dedicated server");
    }

    @Override
    public void setFullscreen(boolean fullscreen){
        throw new UnsupportedOperationException("Cannot set fullscreen on a dedicated server");
    }

    @Override
    public IPlayerDesign getPlayerDesign(){
        throw new UnsupportedOperationException("Cannot get player design on a dedicated server");
    }

    @Override
    public boolean isDedicatedServer(){
        return true;
    }

    @Override
    public void setUniqueId(UUID id){
        throw new UnsupportedOperationException("Cannot set unique id on a dedicated server");
    }

    @Override
    public Input getInput(){
        throw new UnsupportedOperationException("Cannot get input on a dedicated server");
    }

    @Override
    public Settings getSettings(){
        throw new UnsupportedOperationException("Cannot get settings on a dedicated server");
    }
}
