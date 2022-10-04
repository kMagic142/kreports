package ro.kmagic.kreports.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.events.ReportClosedEvent;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.utils.Utils;

import java.util.List;

public class StaffActionsGUI {
    private final Gui gui;
    private int updateTask;

    public StaffActionsGUI(Player player) {
        Reports instance = Reports.getInstance();
        ConfigurationSection menuSection = instance.getConfig().getConfigurationSection("staff-actions-gui");
        Report report = instance.getMySQL().getReportClaimedBy(player.getName());

        if(instance.getMySQL().getStaff(player.getName()) == 0) instance.getMySQL().setStaff(player.getName(), 0);

        gui = Gui.gui()
                .title(Component.text(Utils.color(menuSection.getString("title"))))
                .rows(menuSection.getInt("rows"))
                .disableAllInteractions()
                .create();

        gui.setCloseGuiAction(event -> {
            if(updateTask != 0) Bukkit.getScheduler().cancelTask(updateTask);
        });

        if(report == null) {
            GuiItem error = ItemBuilder.from(Material.BARRIER)
                    .name(Component.text(Utils.color("&c&lAn error occured. Report was null.")))
                    .amount(1)
                    .asGuiItem(event -> {
                        gui.close(player);
                    });

            gui.setItem(17, error);
            return;
        }

        String name = menuSection.getConfigurationSection("close").getString("name");
        List<String> lore = menuSection.getConfigurationSection("close").getStringList("lore");
        int slot = menuSection.getConfigurationSection("close").getInt("slot") - 1;
        String id = menuSection.getConfigurationSection("close").getString("id");

        byte data = 0;

        if(id.contains(":")) data = Byte.parseByte(id.split(":")[1]);

        String material = id.split(":")[0];
        
        GuiItem close = ItemBuilder.from(Material.getMaterial(material).getNewData(data).toItemStack())
                .name(Component.text(Utils.color(name)))
                .lore(Utils.formatComponentList((lore)))
                .amount(1)
                .asGuiItem(event -> {
                    instance.getRedis().reportClosed(report, true);
                    instance.getReportManager().removeReport(report);
                    instance.getMySQL().deleteReport(report);
                    instance.getMySQL().setStaff(report.getClaimer(), instance.getMySQL().getStaff(report.getClaimer()));
                    gui.close(player);
                });

        gui.setItem(slot, close);

        String name2 = menuSection.getConfigurationSection("close2").getString("name");
        List<String> lore2 = menuSection.getConfigurationSection("close2").getStringList("lore");
        int slot2 = menuSection.getConfigurationSection("close2").getInt("slot") - 1;
        String id2 = menuSection.getConfigurationSection("close2").getString("id");

        byte data2 = 0;

        if(id2.contains(":")) data2 = Byte.parseByte(id2.split(":")[1]);

        String material2 = id2.split(":")[0];
        
        GuiItem close2 = ItemBuilder.from(Material.getMaterial(material2).getNewData(data2).toItemStack())
                .name(Component.text(Utils.color(name2)))
                .lore(Utils.formatComponentList(lore2))
                .amount(1)
                .asGuiItem(event -> {
                    instance.getRedis().reportClosed(report, true, true);
                    instance.getReportManager().removeReport(report);
                    instance.getMySQL().deleteReport(report);
                    instance.getMySQL().setStaff(report.getClaimer(), instance.getMySQL().getStaff(report.getClaimer()));
                    player.sendMessage(Utils.color(instance.getMessages().getString("staff-report-closed-with-positive-response")));
                    gui.close(player);
                });

        gui.setItem(slot2, close2);

        String name3 = menuSection.getConfigurationSection("close3").getString("name");
        List<String> lore3 = menuSection.getConfigurationSection("close3").getStringList("lore");
        int slot3 = menuSection.getConfigurationSection("close3").getInt("slot") - 1;
        String id3 = menuSection.getConfigurationSection("close3").getString("id");

        byte data3 = 0;

        if(id3.contains(":")) data3 = Byte.parseByte(id3.split(":")[1]);

        String material3 = id3.split(":")[0];

        GuiItem close3 = ItemBuilder.from(Material.getMaterial(material3).getNewData(data3).toItemStack())
                .name(Component.text(Utils.color(name3)))
                .lore(Utils.formatComponentList(lore3))
                .amount(1)
                .asGuiItem(event -> {
                    instance.getRedis().reportClosed(report, true, false);
                    instance.getReportManager().removeReport(report);
                    instance.getMySQL().deleteReport(report);
                    instance.getMySQL().setStaff(report.getClaimer(), instance.getMySQL().getStaff(report.getClaimer()));
                    player.sendMessage(Utils.color(instance.getMessages().getString("staff-report-closed-with-negative-response")));
                    gui.close(player);
                });

        gui.setItem(slot3, close3);

        if(!report.isConversation()) {
            String name4 = menuSection.getConfigurationSection("conversation").getString("name");
            List<String> lore4 = menuSection.getConfigurationSection("conversation").getStringList("lore");
            int slot4 = menuSection.getConfigurationSection("conversation").getInt("slot") - 1;
            String id4 = menuSection.getConfigurationSection("conversation").getString("id");
            byte data4 = 0;
            
            if(id4.contains(":")) data4 = Byte.parseByte(id4.split(":")[1]);

            String material4 = id4.split(":")[0];
            
            GuiItem conversation = ItemBuilder.from(Material.getMaterial(material4).getNewData(data4).toItemStack())
                    .name(Component.text(Utils.color(name4)))
                    .lore(Utils.formatComponentList(lore4))
                    .amount(1)
                    .asGuiItem(event -> {
                        report.setConversation(true);
                        instance.getMySQL().updateConversation(report);
                        instance.getRedis().conversationUpdate(report, true);
                        for(String s : instance.getMessages().getStringList("conversation-opened")) {
                            player.sendMessage(Utils.color(s.replace("{player}", report.getPlayer())));
                        }
                        gui.close(player);
                    });

            gui.setItem(slot4, conversation);
        } else {
            String name4 = menuSection.getConfigurationSection("closeconversation").getString("name");
            List<String> lore4 = menuSection.getConfigurationSection("closeconversation").getStringList("lore");
            int slot4 = menuSection.getConfigurationSection("closeconversation").getInt("slot") - 1;
            String id4 = menuSection.getConfigurationSection("closeconversation").getString("id");

            byte data4 = 0;

            if(id4.contains(":")) data4 = Byte.parseByte(id4.split(":")[1]);

            String material4 = id4.split(":")[0];

            GuiItem conversation = ItemBuilder.from(Material.getMaterial(material4).getNewData(data4).toItemStack())
                    .name(Component.text(Utils.color(name4)))
                    .lore(Utils.formatComponentList(lore4))
                    .amount(1)
                    .asGuiItem(event -> {
                        report.setConversation(false);
                        instance.getMySQL().updateConversation(report);
                        instance.getRedis().conversationUpdate(report, false);
                        player.sendMessage(Utils.color(instance.getMessages().getString("conversation-closed")));
                        gui.close(player);
                    });

            gui.setItem(slot4, conversation);
        }

        if(!menuSection.getConfigurationSection("back").getBoolean("enabled")) return;

        String backname = menuSection.getConfigurationSection("back").getString("name");
        int backslot = menuSection.getConfigurationSection("back").getInt("slot") - 1;
        String backid = menuSection.getConfigurationSection("back").getString("id");

        byte backdata = 0;

        if(backid.contains(":")) backdata = Byte.parseByte(backid.split(":")[1]);

        String backmaterial = backid.split(":")[0];

        GuiItem backitem = ItemBuilder.from(Material.getMaterial(backmaterial).getNewData(backdata).toItemStack())
                .name(Component.text(Utils.color(backname)))
                .amount(1)
                .asGuiItem(event -> new StaffReportsGUI(player).updateMenu(20).open(player));

        gui.setItem(backslot, backitem);

    }

    public Gui getGui() {
        return gui;
    }

    public Gui updateMenu(int interval) {
        updateTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(Reports.getInstance(), gui::update, 0, interval* 20L);
        return gui;
    }
}
