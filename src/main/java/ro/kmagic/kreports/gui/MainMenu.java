package ro.kmagic.kreports.gui;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.reasons.Reason;
import ro.kmagic.kreports.utils.Utils;
import xyz.upperlevel.spigot.book.BookUtil;

public class MainMenu {

    private final ItemStack book;
    private final Player player;

    public MainMenu(Player player, String reportedPlayer) {
        this.player = player;

        Reports reports = Reports.getInstance();
        FileConfiguration config = reports.getConfig();

        Player bukkitReportedPlayer = Bukkit.getPlayerExact(reportedPlayer);

        BookUtil.PageBuilder pageBuilder = new BookUtil.PageBuilder().add(Utils.color(bukkitReportedPlayer, config.getString("main-menu.title"))).newLine().newLine();

        config.getConfigurationSection("main-menu.reasons").getKeys(false).forEach(key -> {
            ConfigurationSection reason = config.getConfigurationSection("main-menu.reasons").getConfigurationSection(key);
            String text = reason.getString("text");
            String hoverText = reason.getString("hover-text");
            Reason itemReason = reports.getReasonManager().getReason(reason.getString("reason"));

            pageBuilder.add(
                    BookUtil.TextBuilder.of(Utils.color(player, text))
                            .onClick(BookUtil.ClickAction.runCommand("/report " + reportedPlayer + " " + itemReason.getName()))
                            .onHover(BookUtil.HoverAction.showText(Utils.color(bukkitReportedPlayer, hoverText)))
                            .build()
            ).newLine().newLine();
        });

        this.book = BookUtil
                .writtenBook()
                .pages(pageBuilder.build())
                .build();
    }

    public void open() {
        BookUtil.openPlayer(player, book);
    }
}
