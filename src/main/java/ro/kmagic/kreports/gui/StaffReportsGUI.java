package ro.kmagic.kreports.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.managers.ReportsManager;
import ro.kmagic.kreports.utils.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class StaffReportsGUI {

    private final PaginatedGui gui;

    public StaffReportsGUI(Player player) {
        Reports instance = Reports.getInstance();
        ConfigurationSection menuSection = instance.getConfig().getConfigurationSection("staff-reports-gui");
        ReportsManager reportsManager = instance.getReportManager();

        reportsManager.loadReports();

        gui = Gui.paginated()
                .title(Component.text(Utils.color(menuSection.getString("title"))))
                .rows(6)
                .pageSize(45)
                .disableAllInteractions()
                .create();

        String name = menuSection.getString("items-name");
        List<String> lore = menuSection.getStringList("items-lore");
        int id = menuSection.getInt("items-id");
        boolean arrowEnabled = menuSection.getConfigurationSection("exit").getBoolean("enabled");

        if(arrowEnabled) {
            String backname = menuSection.getConfigurationSection("exit").getString("name");
            List<String> backlore = menuSection.getConfigurationSection("exit").getStringList("lore");
            int backid = menuSection.getConfigurationSection("exit").getInt("id");


            GuiItem item = ItemBuilder.from(new MaterialData(backid).getItemType())
                    .name(Component.text(Utils.color(backname)))
                    .lore(Utils.formatComponentList((backlore)))
                    .asGuiItem(event -> {
                        gui.close(player);
                    });
            gui.setItem(6, 5, item);
        }

        for(Report report : reportsManager.getReports().values()) {
            if(report.getClaimer() != null) {
                if(Objects.equals(report.getClaimer(), player.getName())) {
                    String baname = menuSection.getString("claimed-name")
                            .replace("{reportedPlayer}", report.getReportedPlayer())
                            .replace("{reason}", "" + report.getReason().getName())
                            .replace("{server}", report.getServer())
                            .replace("{claimer}", instance.getMessages().getString("you"));

                    List<String> balore = new LinkedList<>();

                    for (String str : menuSection.getStringList("claimed-lore")) {
                        balore.add(str.replace("{reportedPlayer}", report.getReportedPlayer())
                                .replace("{reason}", "" + report.getReason().getName())
                                .replace("{server}", report.getServer())
                                .replace("{claimer}", instance.getMessages().getString("you")));
                    }

                    int baid = menuSection.getInt("claimed-id");

                    GuiItem item = ItemBuilder.from(new MaterialData(baid).getItemType())
                            .name(Component.text(Utils.color(baname)))
                            .lore(Utils.formatComponentList((balore)))
                            .asGuiItem(event -> {
                                new StaffActionsGUI(player);
                            });

                    gui.addItem(item);
                    continue;
                }

                String baname = menuSection.getString("claimed-name")
                        .replace("{reportedPlayer}", report.getReportedPlayer())
                        .replace("{reason}", "" + report.getReason().getName())
                        .replace("{server}", report.getServer())
                        .replace("{claimer}", report.getClaimer());

                List<String> balore = new LinkedList<>();

                for (String str : menuSection.getStringList("claimed-lore")) {
                    balore.add(str.replace("{reportedPlayer}", report.getReportedPlayer())
                            .replace("{reason}", "" + report.getReason().getName())
                            .replace("{server}", report.getServer())
                            .replace("{claimer}", report.getClaimer()));
                }

                int baid = menuSection.getInt("claimed-id");

                GuiItem item = ItemBuilder.from(new MaterialData(baid).getItemType())
                        .name(Component.text(Utils.color(baname)))
                        .lore(Utils.formatComponentList((balore)))
                        .asGuiItem(event -> {
                            new StaffActionsGUI(player);
                        });

                gui.addItem(item);
                continue;
            }

            GuiItem item = ItemBuilder.from(new MaterialData(id).getItemType())
                    .name(Component.text(Utils.color(name.replace("{reportedPlayer}", report.getReportedPlayer())
                            .replace("{reason}", "" + report.getReason().getName())
                            .replace("{server}", report.getServer()))))
                    .lore(Utils.formatComponentList(lore))
                    .asGuiItem();

            gui.addItem(item);
        }

        String previousname = menuSection.getConfigurationSection("previous").getString("name");
        int previousid = menuSection.getConfigurationSection("previous").getInt("id");

        GuiItem previous = ItemBuilder.from(new MaterialData(previousid).getItemType())
                .name(Component.text(Utils.color(previousname)))
                .asGuiItem(event -> gui.previous());

        String nextname = menuSection.getConfigurationSection("next").getString("name");
        int nextid = menuSection.getConfigurationSection("next").getInt("id");

        GuiItem next = ItemBuilder.from(new MaterialData(nextid).getItemType())
                .name(Component.text(Utils.color(nextname)))
                .asGuiItem(event -> gui.next());


        gui.setItem(6, 3, previous);
        gui.setItem(6, 7, next);
        gui.getFiller().fillBottom(ItemBuilder.from(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15).toItemStack(1)).asGuiItem());
    }

    public PaginatedGui getGui() {
        return gui;
    }

}
