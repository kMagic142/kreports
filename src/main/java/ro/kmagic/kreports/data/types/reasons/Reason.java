package ro.kmagic.kreports.data.types.reasons;

import ro.kmagic.kreports.data.types.reports.ReportCategory;

public class Reason {

    private final String name;
    private ReportCategory category;

    public Reason(String name, String category) {
        this.name = name;
        try {
            this.category = ReportCategory.valueOf(category);
        } catch(IllegalArgumentException e) {
            this.category = ReportCategory.MISC;
        }
    }

    public String getName() {
        return name;
    }

    public ReportCategory getCategory() {
        return category;
    }
}
