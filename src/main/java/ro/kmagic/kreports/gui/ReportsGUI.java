package ro.kmagic.kreports.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.managers.ReportsManager;
import ro.kmagic.kreports.utils.Utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ReportsGUI {
    private final PaginatedGui gui;
    private int updateTask;

    public ReportsGUI(Player player) {
        Reports instance = Reports.getInstance();
        ConfigurationSection menuSection = instance.getConfig().getConfigurationSection("reports-gui");
        ReportsManager reportsManager = instance.getReportManager();

        reportsManager.loadReports();

        gui = Gui.paginated()
                .title(Component.text(Utils.color(menuSection.getString("title"))))
                .rows(6)
                .pageSize(45)
                .disableAllInteractions()
                .create();

        gui.setCloseGuiAction(event -> {
            if(updateTask != 0) Bukkit.getScheduler().cancelTask(updateTask);
        });

        boolean arrowEnabled = menuSection.getConfigurationSection("exit").getBoolean("enabled");

        if(arrowEnabled) {
            String backname = menuSection.getConfigurationSection("exit").getString("name");
            List<String> backlore = menuSection.getConfigurationSection("exit").getStringList("lore");
            String backid = menuSection.getConfigurationSection("exit").getString("id");
            byte backdata = 0;

            if(backid.contains(":")) backdata = Byte.parseByte(backid.split(":")[1]);

            String backmat = backid.split(":")[0];

            ItemStack itemstack = Material.getMaterial(backmat).getNewData(backdata).toItemStack();
            itemstack.getItemMeta().addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            GuiItem item = ItemBuilder.from(itemstack)
                    .name(Component.text(Utils.color(backname)))
                    .lore(Utils.formatComponentList((backlore)))
                    .amount(1)
                    .asGuiItem(event -> {
                        gui.close(player);
                    });
            gui.setItem(6, 5, item);
        }

        String name = menuSection.getString("items-name");
        String id = menuSection.getString("items-id");
        byte data = 0;

        if(id.contains(":")) data = Byte.parseByte(id.split(":")[1]);

        String material = id.split(":")[0];

        for(Report report : reportsManager.getReports()) {
            if(!report.getPlayer().equalsIgnoreCase(player.getName())) continue;

            Instant instant = Instant.ofEpochSecond(report.getTimestamp());
            Date date1 = Date.from(instant);

            SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            String date = DateFor.format(date1);

            if(report.getClaimer() != null) {
                String baname = menuSection.getString("claimed-name")
                        .replace("{reportedPlayer}", report.getReportedPlayer())
                        .replace("{reason}", "" + report.getReason().getName())
                        .replace("{server}", report.getServer())
                        .replace("{date}", date)
                        .replace("{claimer}", report.getClaimer());

                List<String> balore = new LinkedList<>();

                for(String str : menuSection.getStringList("claimed-lore")) {
                    balore.add(str.replace("{reportedPlayer}", report.getReportedPlayer())
                            .replace("{reason}", "" + report.getReason().getName())
                            .replace("{server}", report.getServer())
                            .replace("{date}", date)
                            .replace("{claimer}", report.getClaimer()));
                }

                String baid = menuSection.getString("claimed-id");
                byte badata = 0;

                if(baid.contains(":")) badata = Byte.parseByte(baid.split(":")[1]);

                String bamat = baid.split(":")[0];

                ItemStack itemstack = Material.getMaterial(bamat).getNewData(badata).toItemStack();
                itemstack.getItemMeta().addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                GuiItem item = ItemBuilder.from(itemstack)
                        .name(Component.text(Utils.color(baname)))
                        .lore(Utils.formatComponentList((balore)))
                        .amount(1)
                        .asGuiItem();

                gui.addItem(item);
                continue;
            }

            List<String> balore = new LinkedList<>();

            for(String str : menuSection.getStringList("items-lore")) {
                balore.add(str.replace("{reportedPlayer}", report.getReportedPlayer())
                        .replace("{reason}", "" + report.getReason().getName())
                        .replace("{server}", report.getServer())
                        .replace("{date}", date));
            }

            ItemStack itemstack = Material.getMaterial(material).getNewData(data).toItemStack();
            itemstack.getItemMeta().addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

            GuiItem item = ItemBuilder.from(itemstack)
                    .name(Component.text(Utils.color(name.replace("{reportedPlayer}", report.getReportedPlayer())
                            .replace("{reason}", "" + report.getReason().getName())
                            .replace("{date}", date)
                            .replace("{server}", report.getServer()))))
                    .lore(Utils.formatComponentList(balore))
                    .amount(1)
                    .asGuiItem();

            gui.addItem(item);
        }

        String previousname = menuSection.getConfigurationSection("previous").getString("name");
        String previousid = menuSection.getConfigurationSection("previous").getString("id");
        byte previousdata = 0;

        if(previousid.contains(":")) previousdata = Byte.parseByte(previousid.split(":")[1]);

        String previousmaterial = previousid.split(":")[0];

        GuiItem previous = ItemBuilder.from(Material.getMaterial(previousmaterial).getNewData(previousdata).toItemStack())
                .name(Component.text(Utils.color(previousname)))
                .amount(1)
                .asGuiItem(event -> gui.previous());

        String nextname = menuSection.getConfigurationSection("next").getString("name");
        String nextid = menuSection.getConfigurationSection("next").getString("id");
        byte nextdata = 0;

        if(nextid.contains(":")) nextdata = Byte.parseByte(nextid.split(":")[1]);

        String nextmaterial = nextid.split(":")[0];

        GuiItem next = ItemBuilder.from(Material.getMaterial(nextmaterial).getNewData(nextdata).toItemStack())
                .name(Component.text(Utils.color(nextname)))
                .amount(1)
                .asGuiItem(event -> gui.next());


        gui.setItem(6, 3, previous);
        gui.setItem(6, 7, next);
        gui.getFiller().fillBottom(ItemBuilder.from(new MaterialData(Material.STAINED_GLASS_PANE, (byte) 15).toItemStack(1)).asGuiItem());
    }

    public PaginatedGui getGui() {
        return gui;
    }

    public PaginatedGui updateMenu(int interval) {
        updateTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Reports.getInstance(), gui::update, 0, interval* 20L);
        return gui;
    }
}
