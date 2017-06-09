package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.data.settings.Settings;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.IInteractionManager;
import de.ellpeck.rockbottom.api.gui.IGuiManager;
import de.ellpeck.rockbottom.api.mod.IMod;
import de.ellpeck.rockbottom.api.net.chat.IChatLog;
import de.ellpeck.rockbottom.api.particle.IParticleManager;
import de.ellpeck.rockbottom.api.util.IAction;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import org.newdawn.slick.GameContainer;

import java.io.File;
import java.net.URLClassLoader;
import java.util.UUID;

public interface IGameInstance extends IMod{

    boolean isInWorld();

    void startWorld(File worldFile, WorldInfo info);

    void joinWorld(DataSet playerSet, WorldInfo info, NameToIndexInfo tileRegInfo);

    void quitWorld();

    void openIngameMenu();

    void scheduleAction(IAction action);

    GameContainer getContainer();

    double getWidthInWorld();

    double getHeightInWorld();

    double getWidthInGui();

    double getHeightInGui();

    float getMouseInGuiX();

    float getMouseInGuiY();

    IDataManager getDataManager();

    Settings getSettings();

    AbstractEntityPlayer getPlayer();

    IGuiManager getGuiManager();

    IInteractionManager getInteractionManager();

    IChatLog getChatLog();

    IWorld getWorld();

    IAssetManager getAssetManager();

    IParticleManager getParticleManager();

    UUID getUniqueId();

    boolean isDebug();

    boolean isLightDebug();

    boolean isForegroundDebug();

    boolean isBackgroundDebug();

    int getTpsAverage();

    int getFpsAverage();

    URLClassLoader getClassLoader();
}
