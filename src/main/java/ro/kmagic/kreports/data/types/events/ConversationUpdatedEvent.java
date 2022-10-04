package ro.kmagic.kreports.data.types.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ro.kmagic.kreports.data.types.reports.Report;

public class ConversationUpdatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Report report;
    private final boolean conversation;

    public ConversationUpdatedEvent(Report report, boolean conversation) {
        this.report = report;
        this.conversation = conversation;
    }

    public Report getReport() {
        return report;
    }

    public boolean isConversation() {
        return conversation;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
