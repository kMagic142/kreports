package ro.kmagic.kreports.data.database.mysql;

import ro.kmagic.kreports.data.types.reports.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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

            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS reports_data(id INT, name VARCHAR(16) NOT NULL, reported VARCHAR(16) NOT NULL, server VARCHAR(32) NOT NULL, claimedBy VARCHAR(16), reason VARCHAR(24), conversation BIT(1), timestamp BIGINT, PRIMARY KEY (id))");
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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports_data WHERE name=?");
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                int id = result.getInt("id");
                String reported = result.getString("reported");
                String server = result.getString("server");
                String claimedBy = result.getString("claimedBy");
                String reason = result.getString("reason");
                long timestamp = result.getLong("timestamp");
                boolean conversation = result.getBoolean("conversation");

                return new Report(id, name, reported, reason, server, conversation, claimedBy, null, timestamp);
            }

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }

        return null;
    }

    public Report getReport(int id) {
        Connection connection = null;
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports_data WHERE id=?");
            statement.setInt(1, id);

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                String name = result.getString("name");
                String reported = result.getString("reported");
                String server = result.getString("server");
                String claimedBy = result.getString("claimedBy");
                String reason = result.getString("reason");
                long timestamp = result.getLong("timestamp");
                boolean conversation = result.getBoolean("conversation");

                return new Report(id, name, reported, reason, server, conversation, claimedBy, null, timestamp);
            }

        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        } finally {
            if (getConnectionPoolManager() != null) getConnectionPoolManager().close(connection);
        }

        return null;
    }

    public List<Report> getReports() {
        Connection connection = null;
        List<Report> reports = new LinkedList<>();
        try {
            connection = getConnectionPoolManager().getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports_data");
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                int id = result.getInt("id");
                String reported = result.getString("reported");
                String server = result.getString("server");
                String claimedBy = result.getString("claimedBy");
                String reason = result.getString("reason");
                boolean conversation = result.getBoolean("conversation");
                long timestamp = result.getLong("timestamp");
                String playerName = result.getString("name");

                reports.add(new Report(id, playerName, reported, reason, server, conversation, claimedBy, null, timestamp));
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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports_data WHERE claimedBy=?");
            statement.setString(1, name);

            ResultSet result = statement.executeQuery();

            if(result.next()) {
                int id = result.getInt("id");
                String playerName = result.getString("name");
                String reported = result.getString("reported");
                String server = result.getString("server");
                String reason = result.getString("reason");
                long timestamp = result.getLong("timestamp");
                boolean conversation = result.getBoolean("conversation");

                return new Report(id, playerName, reported, reason, server, conversation, name, null, timestamp);
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
            PreparedStatement statement = connection.prepareStatement("INSERT INTO reports_data(id, name, reported, server, claimedBy, conversation, reason, timestamp) VALUES(?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE claimedBy=?, conversation=?");
            statement.setInt(1, report.getId());
            statement.setString(2, report.getPlayer());
            statement.setString(3, report.getReportedPlayer());
            statement.setString(4, report.getServer());
            statement.setString(5, report.getClaimer());
            statement.setBoolean(6, report.isConversation());
            statement.setString(7, report.getReason().getName());
            statement.setLong(8, report.getTimestamp());

            statement.setString(9, report.getClaimer());
            statement.setBoolean(10, report.isConversation());

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
            PreparedStatement statement = connection.prepareStatement("UPDATE reports_data SET conversation=? WHERE id=?");
            statement.setBoolean(1, report.isConversation());
            statement.setInt(2, report.getId());

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
            PreparedStatement statement = connection.prepareStatement("DELETE FROM reports_data WHERE id=?");
            statement.setInt(1, report.getId());

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
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM reports_staff WHERE name=?");
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

        return 0;
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

            statement.setInt(3, resolved);

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
