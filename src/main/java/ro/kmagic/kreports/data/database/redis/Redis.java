package ro.kmagic.kreports.data.database.redis;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.exceptions.JedisException;
import ro.kmagic.kreports.Reports;
import ro.kmagic.kreports.data.types.events.PubSubEvent;
import ro.kmagic.kreports.data.types.reports.Report;

public class Redis {

    private final ConnectionPoolManager connectionPoolManager;
    private BukkitTask task;

    public Redis() {
        connectionPoolManager = new ConnectionPoolManager();
        init();
    }

    private void init() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                try (Jedis rsc = getConnectionPoolManager().getConnection()) {
                    rsc.subscribe(new JedisPubSub() {
                        @Override
                        public void onMessage(String channel, String message) {
                            Reports.getInstance().getServer().getPluginManager().callEvent(new PubSubEvent(channel, message));
                        }
                    }, "topicraftreports");

                } catch (JedisException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(Reports.getInstance());
    }

    public void sendReport(Report report) {
        try(Jedis rsc = getConnectionPoolManager().getConnection()) {
            String message = "reportcreated" + ";" + report.getId() + ";" + report.getServer();
            rsc.publish("topicraftreports", message);
        }
    }

    public void reportReply(Report report, String reply, String replier) {
        try(Jedis rsc = getConnectionPoolManager().getConnection()) {
            String message = "reportreply" + ";" + report.getId() + ";" + replier + ";" + reply;
            rsc.publish("topicraftreports", message);
        }
    }

    public void reportClaimed(Report report, String claimer) {
        try(Jedis rsc = getConnectionPoolManager().getConnection()) {
            String message = "reportclaimed" + ";" + report.getId() + ";" + report.getServer() + ";" + claimer;
            rsc.publish("topicraftreports", message);
        }
    }

    public void reportClosed(Report report, boolean isClosedByStaff) {
        try(Jedis rsc = getConnectionPoolManager().getConnection()) {
            String message = "reportclosed" + ";" + report.getPlayer() + ";" + report.getServer() + ";" + isClosedByStaff + ";" + report.getClaimer() + ";" + report.getReportedPlayer();
            rsc.publish("topicraftreports", message);
        }
    }

    public void reportClosed(Report report, boolean isClosedByStaff, boolean positive) {
        try(Jedis rsc = getConnectionPoolManager().getConnection()) {
            String message = "reportclosed" + ";" + report.getPlayer() + ";" + report.getServer() + ";" + isClosedByStaff + ";" + report.getClaimer() + ";" + report.getReportedPlayer() + ";" + positive;
            rsc.publish("topicraftreports", message);
        }
    }

    public void conversationUpdate(Report report, boolean conversation) {
        try(Jedis rsc = getConnectionPoolManager().getConnection()) {
            String message = "conversationupdate" + ";" + report.getId() + ";" + conversation;
            rsc.publish("topicraftreports", message);
        }
    }

    public ConnectionPoolManager getConnectionPoolManager() {
        return connectionPoolManager;
    }

    public BukkitTask getTask() {
        return task;
    }

}
