package ro.kmagic.kreports.data.types.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ro.kmagic.kreports.data.types.reports.Report;

public class ReportCreatedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Report report;

    public ReportCreatedEvent(Report report) {
        this.report = report;
    }

    public Report getReport() {
        return report;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
