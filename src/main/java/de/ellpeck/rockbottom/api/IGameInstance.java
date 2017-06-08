package de.ellpeck.rockbottom.api;

import de.ellpeck.rockbottom.api.data.IDataManager;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.util.IAction;
import de.ellpeck.rockbottom.api.util.reg.NameToIndexInfo;
import de.ellpeck.rockbottom.api.world.IWorld;
import de.ellpeck.rockbottom.api.world.WorldInfo;
import de.ellpeck.rockbottom.game.assets.AssetManager;
import de.ellpeck.rockbottom.game.data.settings.Settings;
import de.ellpeck.rockbottom.game.gui.GuiManager;
import de.ellpeck.rockbottom.game.net.chat.ChatLog;
import de.ellpeck.rockbottom.game.particle.ParticleManager;
import de.ellpeck.rockbottom.game.world.entity.player.EntityPlayer;
import de.ellpeck.rockbottom.game.world.entity.player.InteractionManager;
import org.newdawn.slick.GameContainer;

import java.io.File;
import java.util.UUID;

public interface IGameInstance{

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

    EntityPlayer getPlayer();

    GuiManager getGuiManager();

    InteractionManager getInteractionManager();

    ChatLog getChatLog();

    IWorld getWorld();

    AssetManager getAssetManager();

    ParticleManager getParticleManager();

    UUID getUniqueId();

    boolean isDebug();

    boolean isLightDebug();

    boolean isForegroundDebug();

    boolean isBackgroundDebug();

    int getTpsAverage();

    int getFpsAverage();
}
