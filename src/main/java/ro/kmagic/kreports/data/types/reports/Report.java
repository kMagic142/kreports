package ro.kmagic.kreports.data.types.reports;

import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.reasons.Reason;

public class Report {

    private final String player;
    private final String reportedPlayer;
    private final Reason reason;
    private final String server;
    private String claimer;
    private boolean conversation;
    private String hastebinURL;


    public Report(String player, String reportedPlayer, String reason, String server, boolean conversation, String claimer, String hastebinURL) {
        this.player = player;
        this.reportedPlayer = reportedPlayer;
        this.reason = Reports.getInstance().getReasonManager().getReason(reason);
        this.server = server;
        this.claimer = claimer;
        this.conversation = conversation;
        this.hastebinURL = hastebinURL;
    }

    public Report(String player, String reportedPlayer, Reason reason) {
        this.player = player;
        this.reportedPlayer = reportedPlayer;
        this.reason = reason;
        this.server = Reports.getInstance().getConfig().getString("server");
        this.claimer = null;
        this.conversation = false;
        this.hastebinURL = null;
    }

    public String getPlayer() {
        return player;
    }

    public String getReportedPlayer() {
        return reportedPlayer;
    }

    public String getClaimer() {
        return claimer;
    }

    public String getServer() {
        return server;
    }

    public Reason getReason() {
        return reason;
    }

    public boolean isConversation() {
        return conversation;
    }

    public void setConversation(boolean conversation) {
        this.conversation = conversation;
    }

    public String getHastebinURL() {
        return hastebinURL;
    }

    public void setHastebinURL(String hastebinURL) {
        this.hastebinURL = hastebinURL;
    }

    public void setClaimer(String claimer) {
        this.claimer = claimer;
    }
}
