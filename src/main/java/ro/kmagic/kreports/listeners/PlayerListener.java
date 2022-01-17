package ro.kmagic.kreports.listeners;

import com.google.common.collect.ArrayListMultimap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerListener implements Listener {

    public ArrayListMultimap<String, String> map = ArrayListMultimap.create();

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        String playerName = event.getPlayer().getName();

        if(map.get(playerName) != null && map.get(playerName).size() > 10) {
            map.get(playerName).remove(0);
        }

        map.put(playerName, message);

    }

}
