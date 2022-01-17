package ro.kmagic.kreports.managers;

import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.reports.Report;

import java.util.HashMap;

public class ReportsManager {

    private final HashMap<String, Report> cache;

    public ReportsManager() {
        cache = new HashMap<>();
    }

    public void addReport(Report report) {
        cache.putIfAbsent(report.getPlayer(), report);
    }

    public void removeReport(Report report) {
        cache.remove(report.getPlayer());
    }

    public HashMap<String, Report> getReports() {
        return cache;
    }

    public void loadReports() {
        HashMap<String, Report> reports = Reports.getInstance().getMySQL().getReports();
        reports.forEach(cache::putIfAbsent);
    }

}
