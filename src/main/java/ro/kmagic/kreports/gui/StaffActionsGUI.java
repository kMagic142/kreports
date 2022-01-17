package ro.kmagic.kreports.gui;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.events.ReportClosedEvent;
import ro.kmagic.kreports.data.types.reports.Report;
import ro.kmagic.kreports.utils.Utils;

import java.util.List;

public class StaffActionsGUI {
    private final Gui gui;

    public StaffActionsGUI(Player player) {
        Reports instance = Reports.getInstance();
        ConfigurationSection menuSection = instance.getConfig().getConfigurationSection("staff-actions-gui");
        Report report = instance.getMySQL().getReportClaimedBy(player.getName());

        gui = Gui.gui()
                .title(Component.text(Utils.color(menuSection.getString("title"))))
                .rows(menuSection.getInt("rows"))
                .disableAllInteractions()
                .create();


        String name = menuSection.getConfigurationSection("close").getString("name");
        List<String> lore = menuSection.getConfigurationSection("close").getStringList("lore");
        int slot = menuSection.getConfigurationSection("close").getInt("slot") - 1;
        int id = menuSection.getConfigurationSection("close").getInt("id");

        GuiItem close = ItemBuilder.from(new MaterialData(id).getItemType())
                .name(Component.text(Utils.color(name)))
                .lore(Utils.formatComponentList((lore)))
                .asGuiItem(event -> {
                    instance.getReportManager().removeReport(report);
                    instance.getMySQL().deleteReport(report);
                    instance.getMySQL().setStaff(report.getClaimer(), instance.getMySQL().getStaff(report.getClaimer()));
                    instance.getServer().getPluginManager().callEvent(new ReportClosedEvent(report, true));

                });

        gui.setItem(slot, close);

        String name2 = menuSection.getConfigurationSection("close2").getString("name");
        List<String> lore2 = menuSection.getConfigurationSection("close2").getStringList("lore");
        int slot2 = menuSection.getConfigurationSection("close2").getInt("slot") - 1;
        int id2 = menuSection.getConfigurationSection("close2").getInt("id");

        GuiItem close2 = ItemBuilder.from(new MaterialData(id2).getItemType())
                .name(Component.text(Utils.color(name2)))
                .lore(Utils.formatComponentList(lore2))
                .asGuiItem(event -> {
                    instance.getReportManager().removeReport(report);
                    instance.getMySQL().deleteReport(report);
                    instance.getMySQL().setStaff(report.getClaimer(), instance.getMySQL().getStaff(report.getClaimer()));
                    instance.getServer().getPluginManager().callEvent(new ReportClosedEvent(report, true, true));
                    player.sendMessage(Utils.color(instance.getMessages().getString("staff-report-closed-with-positive-response")
                            .replace("{reportedPlayer}", report.getReportedPlayer())));
                });

        gui.setItem(slot2, close2);

        String name3 = menuSection.getConfigurationSection("close3").getString("name");
        List<String> lore3 = menuSection.getConfigurationSection("close3").getStringList("lore");
        int slot3 = menuSection.getConfigurationSection("close3").getInt("slot") - 1;
        int id3 = menuSection.getConfigurationSection("close3").getInt("id");

        GuiItem close3 = ItemBuilder.from(new MaterialData(id3).getItemType())
                .name(Component.text(Utils.color(name3)))
                .lore(Utils.formatComponentList(lore3))
                .asGuiItem(event -> {
                    instance.getReportManager().removeReport(report);
                    instance.getMySQL().deleteReport(report);
                    instance.getMySQL().setStaff(report.getClaimer(), instance.getMySQL().getStaff(report.getClaimer()));
                    instance.getServer().getPluginManager().callEvent(new ReportClosedEvent(report, true, false));
                    player.sendMessage(Utils.color(instance.getMessages().getString("staff-report-closed-with-negative-response")
                            .replace("{reportedPlayer}", report.getReportedPlayer())));
                });

        gui.setItem(slot3, close3);

        if(!report.isConversation()) {
            String name4 = menuSection.getConfigurationSection("conversation").getString("name");
            List<String> lore4 = menuSection.getConfigurationSection("conversation").getStringList("lore");
            int slot4 = menuSection.getConfigurationSection("conversation").getInt("slot") - 1;
            int id4 = menuSection.getConfigurationSection("conversation").getInt("id");

            GuiItem conversation = ItemBuilder.from(new MaterialData(id4).getItemType())
                    .name(Component.text(Utils.color(name4)))
                    .lore(Utils.formatComponentList(lore4))
                    .asGuiItem(event -> {
                        report.setConversation(true);
                        instance.getMySQL().updateConversation(report);
                        instance.getMySQL().setStaff(report.getClaimer(), instance.getMySQL().getStaff(report.getClaimer()));
                        player.sendMessage(Utils.color(instance.getMessages().getString("conversation-opened")
                                .replace("{player}", report.getPlayer())));
                    });

            gui.setItem(slot4, conversation);
        } else {
            String name4 = menuSection.getConfigurationSection("closeconversation").getString("name");
            List<String> lore4 = menuSection.getConfigurationSection("closeconversation").getStringList("lore");
            int slot4 = menuSection.getConfigurationSection("closeconversation").getInt("slot") - 1;
            int id4 = menuSection.getConfigurationSection("closeconversation").getInt("id");

            GuiItem conversation = ItemBuilder.from(new MaterialData(id4).getItemType())
                    .name(Component.text(Utils.color(name4)))
                    .lore(Utils.formatComponentList(lore4))
                    .asGuiItem(event -> {
                        report.setConversation(false);
                        instance.getMySQL().updateConversation(report);
                        player.sendMessage(Utils.color(instance.getMessages().getString("conversation-closed")));
                    });

            gui.setItem(slot4, conversation);
        }

        if(!menuSection.getConfigurationSection("back").getBoolean("enabled")) return;

        String backname = menuSection.getConfigurationSection("back").getString("name");
        int backslot = menuSection.getConfigurationSection("back").getInt("slot") - 1;
        int backid = menuSection.getConfigurationSection("back").getInt("id");

        GuiItem backitem = ItemBuilder.from(new MaterialData(backid).getItemType())
                .name(Component.text(Utils.color(backname)))
                .asGuiItem(event -> gui.close(event.getWhoClicked()));

        gui.setItem(backslot, backitem);

    }

    public Gui getGui() {
        return gui;
    }
}
