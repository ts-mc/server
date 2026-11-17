package com.theysix.tsxmas;

import java.nio.file.*;
import java.sql.*;
import java.util.*;

public final class Store implements AutoCloseable {
    private final Connection db;
    public final String season;
    @FunctionalInterface public interface Work<T> { T run() throws Exception; }
    public Store(Path file, String season) throws Exception {
        this.season = season;
        Class.forName("org.sqlite.JDBC");
        db = DriverManager.getConnection("jdbc:sqlite:" + file.toAbsolutePath());
        try (Statement s = db.createStatement()) {
            s.execute("PRAGMA busy_timeout=5000");
            s.execute("PRAGMA journal_mode=WAL");
            s.execute("PRAGMA synchronous=FULL");
            int version;
            try (ResultSet r = s.executeQuery("PRAGMA user_version")) { version = r.getInt(1); }
            if (version > 1) { db.close();throw new IllegalStateException("Database is newer than this plugin; refusing downgrade"); }
            tx(() -> {
                exec("CREATE TABLE IF NOT EXISTS state (season TEXT NOT NULL, scope TEXT NOT NULL, owner TEXT NOT NULL, key TEXT NOT NULL, value TEXT NOT NULL, PRIMARY KEY(season,scope,owner,key))");
                exec("CREATE TABLE IF NOT EXISTS wallets (season TEXT NOT NULL, uuid TEXT NOT NULL, tokens INTEGER NOT NULL DEFAULT 0 CHECK(tokens>=0), PRIMARY KEY(season,uuid))");
                exec("CREATE TABLE IF NOT EXISTS outbox (id INTEGER PRIMARY KEY AUTOINCREMENT, season TEXT NOT NULL, uuid TEXT NOT NULL, source TEXT NOT NULL, step INTEGER NOT NULL, payload TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'PENDING', UNIQUE(season,uuid,source,step))");
                exec("CREATE TABLE IF NOT EXISTS audit (id INTEGER PRIMARY KEY AUTOINCREMENT, time TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP, actor TEXT NOT NULL, action TEXT NOT NULL)");
                exec("PRAGMA user_version=1");
                exec("UPDATE outbox SET status='HELD' WHERE status='IN_FLIGHT'");
                return null;
            });
        }
    }
    public synchronized <T> T tx(Work<T> body) throws Exception {
        if (!db.getAutoCommit()) return body.run();
        db.setAutoCommit(false);
        try { T result = body.run(); db.commit(); return result; }
        catch (Exception | Error e) { db.rollback(); throw e; }
        finally { db.setAutoCommit(true); }
    }
    public synchronized int exec(String sql, Object... args) throws SQLException {
        try (PreparedStatement p = db.prepareStatement(sql)) {
            for (int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
            return p.executeUpdate();
        }
    }
    public synchronized List<Map<String,String>> query(String sql, Object... args) throws SQLException {
        try (PreparedStatement p = db.prepareStatement(sql)) {
            for (int i=0;i<args.length;i++) p.setObject(i+1,args[i]);
            try (ResultSet r = p.executeQuery()) {
                List<Map<String,String>> rows = new ArrayList<>();
                while (r.next()) {
                    Map<String,String> row = new LinkedHashMap<>();
                    for (int i=1;i<=r.getMetaData().getColumnCount();i++) row.put(r.getMetaData().getColumnLabel(i),r.getString(i));
                    rows.add(row);
                }
                return rows;
            }
        }
    }
    public synchronized String get(String scope, String owner, String key, String fallback) throws SQLException {
        var r = query("SELECT value FROM state WHERE season=? AND scope=? AND owner=? AND key=?",season,scope,owner,key);
        return r.isEmpty() ? fallback : r.getFirst().get("value");
    }
    public synchronized void put(String scope, String owner, String key, Object value) throws SQLException {
        exec("INSERT INTO state VALUES (?,?,?,?,?) ON CONFLICT(season,scope,owner,key) DO UPDATE SET value=excluded.value",season,scope,owner,key,String.valueOf(value));
    }
    public synchronized boolean once(String scope, String owner, String key) throws SQLException {
        return exec("INSERT OR IGNORE INTO state VALUES (?,?,?,?,?)",season,scope,owner,key,"1") == 1;
    }
    public synchronized List<Map<String,String>> list(String scope) throws SQLException {
        return query("SELECT owner,key,value FROM state WHERE season=? AND scope=? ORDER BY owner,key",season,scope);
    }
    public synchronized long tokens(String uuid) throws SQLException {
        var r=query("SELECT tokens FROM wallets WHERE season=? AND uuid=?",season,uuid);
        return r.isEmpty()?0:Long.parseLong(r.getFirst().get("tokens"));
    }
    public synchronized void tokens(String uuid, long change) throws SQLException {
        long next = Math.addExact(tokens(uuid),change);
        if (next < 0) throw new IllegalArgumentException("Not enough event tokens");
        exec("INSERT INTO wallets VALUES (?,?,?) ON CONFLICT(season,uuid) DO UPDATE SET tokens=excluded.tokens",season,uuid,next);
    }
    public synchronized void enqueue(String uuid,String source,int step,String payload) throws SQLException {
        exec("INSERT OR IGNORE INTO outbox(season,uuid,source,step,payload) VALUES (?,?,?,?,?)",season,uuid,source,step,payload);
    }
    public synchronized List<Map<String,String>> pending(String uuid) throws SQLException {
        return query("SELECT * FROM outbox WHERE season=? AND uuid=? AND status='PENDING' ORDER BY id LIMIT 64",season,uuid);
    }
    public synchronized void audit(String actor,String action) throws SQLException { exec("INSERT INTO audit(actor,action) VALUES (?,?)",actor,action); }
    public synchronized void backup(Path target) throws SQLException {
        if (Files.exists(target)) throw new IllegalArgumentException("Backup already exists");
        try (Statement s=db.createStatement()) { s.execute("VACUUM INTO '"+target.toAbsolutePath().toString().replace("'","''")+"'"); }
    }
    public synchronized void close() throws SQLException { db.close(); }
}
