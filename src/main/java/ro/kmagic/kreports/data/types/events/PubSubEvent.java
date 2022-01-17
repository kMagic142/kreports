package ro.kmagic.kreports.data.types.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PubSubEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final String channel;
    private final String message;

    public PubSubEvent(String c, String m) {
        this.channel = c;
        this.message = m;
    }

    public String getChannel() {
        return channel;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
