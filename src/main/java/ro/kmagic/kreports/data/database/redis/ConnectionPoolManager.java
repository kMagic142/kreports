package ro.kmagic.kreports.data.database.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.utils.Utils;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private JedisPool pool;
    private String hostname;
    private int port;
    private String password;
    private int maximumConnections;
    private boolean ssl;

    public ConnectionPoolManager() {
        init();
        setupPool();
    }

    private void init() {
        Reports instance = Reports.getInstance();

        this.hostname = instance.getConfig().getString("redis.hostname");
        this.port = Integer.parseInt(instance.getConfig().getString("redis.port"));
        this.password = instance.getConfig().getString("redis.password");
        this.maximumConnections = instance.getConfig().getInt("redis.maximum-connections");
        this.ssl = instance.getConfig().getBoolean("redis.useSSL");
    }

    private void setupPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maximumConnections);
        config.setTestOnBorrow(true);
        config.setMaxWaitMillis(500L);
        pool = new JedisPool(config, hostname, port, 5000, password, ssl);

        try (Jedis rsc = pool.getResource()) {
            if(rsc != null) Utils.info("Successfully connected to Redis.");
        }
    }

    public Jedis getConnection() {
        try {
            if (pool != null) return pool.getResource();
        } catch (JedisException e) {
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
        if (this.pool != null && !this.pool.isClosed())
            this.pool.close();
    }

}


