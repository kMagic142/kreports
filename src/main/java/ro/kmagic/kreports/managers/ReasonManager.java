package ro.kmagic.kreports.managers;

import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.reasons.Reason;
import ro.kmagic.kreports.utils.Utils;

import java.util.LinkedList;
import java.util.List;

public class ReasonManager {

    private final List<Reason> cache;

    public ReasonManager() {
        cache = new LinkedList<>();
    }

    public void loadReasons() {
        Reports.getInstance().getConfig().getConfigurationSection("reasons").getKeys(false).forEach(k -> {
            String category = Reports.getInstance().getConfig().getConfigurationSection("reasons").getString(k);
            cache.add(new Reason(k, category));
        });
    }

    public Reason getReason(String name) {
        return cache.stream().filter(r -> r.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Reason> getReasons() {
        return cache;
    }
}
