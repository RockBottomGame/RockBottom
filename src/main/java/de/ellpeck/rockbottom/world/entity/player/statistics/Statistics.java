package de.ellpeck.rockbottom.world.entity.player.statistics;

import de.ellpeck.rockbottom.api.RockBottomAPI;
import de.ellpeck.rockbottom.api.data.set.DataSet;
import de.ellpeck.rockbottom.api.entity.player.statistics.IStatistics;
import de.ellpeck.rockbottom.api.entity.player.statistics.Statistic;
import de.ellpeck.rockbottom.api.entity.player.statistics.StatisticInitializer;
import de.ellpeck.rockbottom.api.util.reg.ResourceName;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Statistics implements IStatistics {

    private final Map<ResourceName, Statistic> statistics = new HashMap<>();
    private final Map<ResourceName, Statistic> statisticsUnmodifiable = Collections.unmodifiableMap(this.statistics);

    @Override
    public Statistic getOrInit(ResourceName name) {
        return this.statistics.computeIfAbsent(name, n -> {
            StatisticInitializer s = RockBottomAPI.STATISTICS_REGISTRY.get(n);
            return s != null ? s.makeStatistic(this) : null;
        });
    }

    @Override
    public <T extends Statistic> T getOrInit(ResourceName name, Class<? extends StatisticInitializer<T>> initClass) {
        Statistic stat = this.getOrInit(name);
        if (stat != null && initClass.isAssignableFrom(stat.getInitializer().getClass())) {
            return (T) stat;
        } else {
            return null;
        }
    }

    @Override
    public Map<ResourceName, Statistic> getActiveStats() {
        return this.statisticsUnmodifiable;
    }

    @Override
    public void save(DataSet set) {
        int counter = 0;
        for (Map.Entry<ResourceName, Statistic> entry : this.statistics.entrySet()) {
            DataSet sub = new DataSet();
            sub.addString("name", entry.getKey().toString());
            entry.getValue().save(sub);

            set.addDataSet("stat_" + counter, sub);
            counter++;
        }
        set.addInt("stat_amount", counter);
    }

    @Override
    public void load(DataSet set) {
        this.statistics.clear();

        int amount = set.getInt("stat_amount");
        for (int i = 0; i < amount; i++) {
            DataSet sub = set.getDataSet("stat_" + i);
            if (!sub.isEmpty()) {
                ResourceName name = new ResourceName(sub.getString("name"));
                Statistic stat = this.getOrInit(name);
                if (stat != null) {
                    stat.load(sub);
                } else {
                    RockBottomAPI.logger().log(Level.WARNING, "Statistic that was saved with name " + name + " doesn't exist anymore");
                }
            }
        }
    }
}
