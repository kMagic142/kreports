package ro.kmagic.kreports.data.types.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ro.kmagic.kreports.data.types.reports.Report;

public class ReportClosedEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Report report;
    private final boolean isCloserStaff;
    private final Boolean positive;

    public ReportClosedEvent(Report report, boolean closer, boolean positive) {
        this.report = report;
        this.isCloserStaff = closer;
        this.positive = positive;
    }

    public ReportClosedEvent(Report report, boolean closer) {
        this.report = report;
        this.isCloserStaff = closer;
        this.positive = null;
    }

    public boolean isCloserStaff() {
        return isCloserStaff;
    }

    public Report getReport() {
        return report;
    }

    public Boolean getPositive() {
        return positive;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
