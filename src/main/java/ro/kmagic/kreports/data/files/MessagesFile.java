package ro.kmagic.kreports.data.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import ro.kmagic.kreports.Reports;

import java.io.File;

public class MessagesFile {

    private FileConfiguration fc;
    private File file;

    public MessagesFile() {
        getFile();
    }

    public File getFile() {
        if (file == null) {
            file = new File(Reports.getInstance().getDataFolder(), "messages.yml");
            if (!file.exists()) Reports.getInstance().saveResource("messages.yml", false);
        }
        return file;
    }

    public FileConfiguration getData() {
        if (fc == null) fc = YamlConfiguration.loadConfiguration(getFile());
        return fc;
    }

    public void reload() {
        fc = YamlConfiguration.loadConfiguration(getFile());
    }

}
