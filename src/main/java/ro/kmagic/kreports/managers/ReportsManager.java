package ro.kmagic.kreports.managers;

import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.reports.Report;

import java.util.LinkedList;
import java.util.List;

public class ReportsManager {

    private final List<Report> cache;

    public ReportsManager() {
        cache = new LinkedList<>();
    }

    public void addReport(Report report) {
        cache.add(report);
    }

    public void removeReport(Report report) {
        cache.remove(report);
    }

    public List<Report> getReports() {
        return cache;
    }

    public void loadReports() {
        clear();
        List<Report> reports = Reports.getInstance().getMySQL().getReports();
        cache.addAll(reports);
    }

    public void clear() {
        cache.clear();
    }

}
