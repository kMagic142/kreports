package ro.kmagic.kreports.data.database.mysql;

import ro.kmagic.kreports.data.types.reports.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQL {

    private final ConnectionPoolManager connectionPoolManager;

    public MySQL() {
        connectionPoolManager = new ConnectionPoolManager();
        setupTables();
    }

    private void setupTables() {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();

            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reports_data(name VARCHAR(16) NOT NULL, reported VARCHAR(16) NOT NULL, server VARCHAR(32) NOT NULL, claimedBy VARCHAR(16), reason VARCHAR(24), PRIMARY KEY (name))");
            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reports_staff(name VARCHAR(16) NOT NULL, resolved INT, PRIMARY KEY (name))");
            statement.executeUpdate();
            statement.close();

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }
    }

    public Report getReport(String name) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM reports_data WHERE name=?");
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String claimedBy = result.getString("claimedBy");
                String reason = result.getString("reason");
                String playerName = result.getString("name");
                String reported = result.getString("reported");
                String server = result.getString("server");
                boolean conversation = result.getBoolean("conversation");

                return new Report(playerName, reported, reason, server, conversation, claimedBy, null);
            }

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }

        return null;
    }

    public HashMap<String, Report> getReports() {
        Connection connection = null;
        HashMap<String, Report> reports = new HashMap<>();
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports_data");
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String claimedBy = result.getString("claimedBy");
                String reason = result.getString("reason");
                String playerName = result.getString("name");
                String reported = result.getString("reported");
                String server = result.getString("server");
                boolean conversation = result.getBoolean("conversation");

                reports.put(playerName, new Report(playerName, reported, reason, server, conversation, claimedBy, null));
            }

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }

        return reports;
    }

    public Report getReportClaimedBy(String name) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM reports_data WHERE claimedBy=?");
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String claimedBy = result.getString("claimedBy");
                String reason = result.getString("reason");
                String playerName = result.getString("name");
                String reported = result.getString("reported");
                String server = result.getString("server");
                boolean conversation = result.getBoolean("conversation");

                return new Report(playerName, reported, reason, server, conversation, claimedBy, null);
            }

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }

        return null;
    }

    public void addReport(Report report) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO reports_data(name, reported, server, claimedBy, conversation, reason) VALUES(?, ?, ?, ?, ?, ?)");
            statement.setString(1, report.getPlayer());
            statement.setString(2, report.getReportedPlayer());
            statement.setString(3, report.getServer());
            statement.setString(4, report.getClaimer());
            statement.setBoolean(5, report.isConversation());
            statement.setString(6, report.getReason().getName());

            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }
    }

    public void updateConversation(Report report) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE reports_data SET conversation=? WHERE claimedBy=?");
            statement.setString(1, report.getClaimer());
            statement.setBoolean(2, report.isConversation());

            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }
    }

    public void deleteReport(Report report) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM reports_data WHERE name=?");
            statement.setString(1, report.getPlayer());

            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }
    }

    public Integer getStaff(String name) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM reports_staff WHERE name=?");
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                return result.getInt("resolved");
            }

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }

        return null;
    }

    public void removeStaff(String name) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("DELETE FROM reports_staff WHERE name=?");
            statement.setString(1, name);

            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }
    }

    public void setStaff(String name, int resolved) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("INSERT INTO reports_staff(name, resolved) VALUES (?, ?) ON DUPLICATE KEY UPDATE resolved=?");
            statement.setString(1, name);
            statement.setInt(2, resolved);
            statement.setString(3, name);

            statement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }
    }

    public ConnectionPoolManager getConnectionPoolManager() {
        return connectionPoolManager;
    }

}
