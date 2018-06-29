package de.ellpeck.rockbottom.world.entity.player.statistics;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.entity.player.statistics.NumberStatistic;
import de.ellpeck.rockbottom.api.gui.AbstractStatGui;
import de.ellpeck.rockbottom.api.gui.component.ComponentMenu;
import de.ellpeck.rockbottom.api.gui.component.ComponentStatistic;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Collections;
import java.util.List;

public class TimeStatistic extends NumberStatistic {

    public TimeStatistic(ResourceName name, ResourceName textureLocation) {
        super(name, textureLocation);
    }

    public TimeStatistic(ResourceName name, ResourceName textureLocation, int defaultValue) {
        super(name, textureLocation, defaultValue);
    }

    @Override
    public List<ComponentStatistic> getDisplayComponents(IGameInstance game, Stat stat, AbstractStatGui gui, ComponentMenu menu) {
        return Collections.singletonList(new ComponentStatistic(gui, () -> game.getAssetManager().localize(this.getName().addPrefix("stat.")), () -> String.format("%02d:%02d", stat.getValue() / 60, stat.getValue() % 60), stat.getValue(), null) {
            @Override
            public void renderStatGraphic(IGameInstance game, IAssetManager manager, IRenderer g, int x, int y) {
                manager.getTexture(TimeStatistic.this.textureLocation).draw(x + 1, y + 1, 12F, 12F);
            }
        });
    }
}
