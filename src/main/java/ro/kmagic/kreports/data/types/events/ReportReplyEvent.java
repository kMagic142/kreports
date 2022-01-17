package ro.kmagic.kreports.data.types.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import ro.kmagic.kreports.data.types.reports.Report;

public class ReportReplyEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Report report;
    private final String reply;
    private final String replier;

    public ReportReplyEvent(Report report, String reply, String replier) {
        this.report = report;
        this.reply = reply;
        this.replier = replier;
    }

    public Report getReport() {
        return report;
    }

    public String getReply() {
        return reply;
    }

    public String getReplier() {
        return replier;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
