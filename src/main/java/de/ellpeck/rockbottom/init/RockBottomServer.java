package de.ellpeck.rockbottom.init;

import de.ellpeck.rockbottom.Main;
import de.ellpeck.rockbottom.api.*;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.FormattingCode;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.ServerSettings;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.event.IEventHandler;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.net.chat.ICommandSender;
import de.ellpeck.rockbottom.api.net.chat.component.ChatComponent;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.render.IPlayerDesign;
import de.ellpeck.rockbottom.api.toast.IToaster;
import de.ellpeck.rockbottom.api.util.Util;
import de.ellpeck.rockbottom.api.world.DynamicRegistryInfo;
import de.ellpeck.rockbottom.api.world.WorldInfo;

import java.io.File;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Level;

public class RockBottomServer extends AbstractGame{

    protected ServerSettings settings;

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
                return FormattingCode.RED.toString();
            }

            @Override
            public void sendMessageTo(IChatLog chat, ChatComponent message){
                RockBottomAPI.logger().info(message.getUnformattedWithChildren());
            }
        };

        Thread consoleThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(this.isRunning){
                String input = scanner.nextLine();
                this.enqueueAction((game, object) -> this.chatLog.sendCommandSenderMessage(input, consoleCommandSender), null);
            }
            scanner.close();
        }, "ConsoleListener");
        consoleThread.setDaemon(true);
        consoleThread.start();

        boolean isNew;
        File file = new File(this.dataManager.getWorldsDir(), "world_server");

        WorldInfo info = new WorldInfo(file);
        if(WorldInfo.exists(file)){
            info.load();
            isNew = false;
        }
        else{
            info.seed = Util.RANDOM.nextLong();
            info.save();
            isNew = true;
        }

        this.startWorld(file, info, isNew);

        try{
            RockBottomAPI.getNet().init(null, Main.port, true);
        }
        catch(Exception e){
            RockBottomAPI.logger().log(Level.SEVERE, "Couldn't start server", e);
            this.exit();
        }
    }

    @Override
    public void preInit(IGameInstance game, IApiHandler apiHandler, IEventHandler eventHandler){
        this.settings = new ServerSettings();
        this.settings.load();
    }

    @Override
    public int getAutosaveInterval(){
        return this.settings.autosaveIntervalSeconds;
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
    public IRenderer getRenderer(){
        throw new UnsupportedOperationException("Cannot get the graphics on a dedicated server");
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
    public void setFullscreen(boolean fullscreen){
        throw new UnsupportedOperationException("Cannot set fullscreen on a dedicated server");
    }

    @Override
    public IPlayerDesign getPlayerDesign(){
        throw new UnsupportedOperationException("Cannot get player design on a dedicated server");
    }

    @Override
    public void setPlayerDesign(String jsonString){
        throw new UnsupportedOperationException("Cannot set player design on a dedicated server");
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
    public IInputHandler getInput(){
        throw new UnsupportedOperationException("Cannot get input on a dedicated server");
    }

    @Override
    public IToaster getToaster(){
        throw new UnsupportedOperationException("Cannot get toaster on a dedicated server");
    }

    @Override
    public int getWidth(){
        throw new UnsupportedOperationException("Cannot get width on a dedicated server");
    }

    @Override
    public int getHeight(){
        throw new UnsupportedOperationException("Cannot get height on a dedicated server");
    }

    @Override
    public long getWindow(){
        throw new UnsupportedOperationException("Cannot get window on a dedicated server");
    }

    @Override
    public Settings getSettings(){
        throw new UnsupportedOperationException("Cannot get settings on a dedicated server");
    }
}
