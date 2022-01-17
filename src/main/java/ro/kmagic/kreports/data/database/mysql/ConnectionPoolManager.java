package ro.kmagic.kreports.data.database.mysql;

import com.mysql.cj.exceptions.CJCommunicationsException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import org.bukkit.Bukkit;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class ConnectionPoolManager {

    private HikariDataSource dataSource;
    private String hostname;
    private String port;
    private String database;
    private String username;
    private String password;
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;
    private Map<String, Object> properties;
    private String testQuery;

    public ConnectionPoolManager() {
        init();
        setupPool();
    }

    private void init() {
        Reports instance = Reports.getInstance();

        this.hostname = instance.getConfig().getString("mysql.hostname");
        this.port = instance.getConfig().getString("mysql.port");
        this.database = instance.getConfig().getString("mysql.database");
        this.username = instance.getConfig().getString("mysql.username");
        this.password = instance.getConfig().getString("mysql.password");
        this.minimumConnections = instance.getConfig().getInt("mysql.minimum-connections");
        this.maximumConnections = instance.getConfig().getInt("mysql.maximum-connections");
        this.connectionTimeout = instance.getConfig().getLong("mysql.connection-timeout");
        properties = instance.getConfig().getConfigurationSection("mysql.properties").getValues(false);
        this.testQuery = "SELECT 1";
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("boosters-hikari");
        config.setJdbcUrl("jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.setMinimumIdle(this.minimumConnections);
        config.setMaximumPoolSize(this.maximumConnections);
        config.setConnectionTimeout(this.connectionTimeout);
        config.setConnectionTestQuery(this.testQuery);

        for (Map.Entry<String, Object> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }

        try {
            this.dataSource = new HikariDataSource(config);
            Utils.info("Initialized MySQL (" + username + "@" + hostname + ")");
        } catch (HikariPool.PoolInitializationException | CJCommunicationsException exception) {
            exception.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(Reports.getInstance());
        }
    }

    public Connection getConnection() {
        try {
            if (this.dataSource != null && !dataSource.isClosed()) return this.dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close(Connection conn) {
        if (conn != null)
            try {
                conn.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
    }

    public void closePool() {
        if (this.dataSource != null && !this.dataSource.isClosed())
            this.dataSource.close();
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public boolean isClosed() {
        try {
            if (dataSource != null) return dataSource.isClosed();
        } catch (NullPointerException exception) {
            exception.printStackTrace();
        }
        return true;
    }

}

