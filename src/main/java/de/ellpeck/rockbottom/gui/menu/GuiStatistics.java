package de.ellpeck.rockbottom.gui.menu;

import de.ellpeck.rockbottom.api.IGameInstance;
import de.ellpeck.rockbottom.api.IRenderer;
import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.assets.IAssetManager;
import de.ellpeck.rockbottom.api.assets.font.IFont;
import de.ellpeck.rockbottom.api.entity.player.AbstractEntityPlayer;
import de.ellpeck.rockbottom.api.entity.player.statistics.Statistic;
import de.ellpeck.rockbottom.api.gui.AbstractStatGui;
import de.ellpeck.rockbottom.api.gui.Gui;
import de.ellpeck.rockbottom.api.gui.component.ComponentButton;
import de.ellpeck.rockbottom.api.gui.component.ComponentMenu;
import de.ellpeck.rockbottom.api.gui.component.ComponentStatistic;
import de.ellpeck.rockbottom.api.gui.component.MenuComponent;
import de.ellpeck.rockbottom.api.util.BoundingBox;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;
import de.ellpeck.rockbottom.net.packet.backandforth.PacketStats;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GuiStatistics extends AbstractStatGui {

    public boolean statsReceived = false;
    private boolean hasData;

    public GuiStatistics(Gui parent) {
        super(parent);
    }

    @Override
    public AbstractStatGui makeSubGui(List<ComponentStatistic> subComponents) {
        return new GuiStatistics(this) {
            @Override
            protected List<ComponentStatistic> getComponents(IGameInstance game, AbstractEntityPlayer player, ComponentMenu menu) {
                return subComponents;
            }
        };
    }

    @Override
    public void init(IGameInstance game) {
        super.init(game);
        AbstractEntityPlayer player = game.getPlayer();

        this.hasData = !player.world.isClient() || this.statsReceived;
        if (this.hasData) {
            ComponentMenu menu = new ComponentMenu(this, 0, 0, this.height - 24, 1, 7, new BoundingBox(0, 0, 150, this.height - 24).add(this.x + 8, this.y));
            this.components.add(menu);

            List<ComponentStatistic> components = this.getComponents(game, player, menu);
            components.sort(Comparator.comparingInt(ComponentStatistic::getPriority).reversed());

            for (ComponentStatistic comp : components) {
                menu.add(new MenuComponent(150, 18).add(0, 0, comp));
            }
            menu.organize();
        } else {
            RockBottomAPI.getNet().sendToServer(new PacketStats(player.getUniqueId()));
        }

        this.components.add(new ComponentButton(this, this.width / 2 - 40, this.height - 16, 80, 16, () -> {
            game.getGuiManager().openGui(this.parent);
            return true;
        }, game.getAssetManager().localize(ResourceName.intern("button.back"))));
    }

    protected List<ComponentStatistic> getComponents(IGameInstance game, AbstractEntityPlayer player, ComponentMenu menu) {
        List<ComponentStatistic> components = new ArrayList<>();
        for (Statistic stat : player.getStatistics().getActiveStats().values()) {
            components.addAll(stat.getInitializer().getDisplayComponents(game, stat, this, menu));
        }
        return components;
    }

    @Override
    public void render(IGameInstance game, IAssetManager manager, IRenderer g) {
        super.render(game, manager, g);

        if (!this.hasData) {
            IFont font = manager.getFont();
            font.drawCenteredString(this.x + this.width / 2, this.y + this.height / 2 - 20, "Fetching data...", 0.3F, true);
        }
    }

    @Override
    public ResourceName getName() {
        return ResourceName.intern("statistics");
    }
}
