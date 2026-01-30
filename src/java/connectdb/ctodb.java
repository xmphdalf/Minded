package connectdb;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import java.sql.*;
import java.util.Properties;

/**
 * Database connection manager using HikariCP connection pooling.
 * Provides thread-safe, high-performance database connections.
 *
 * @author Minded Team
 * @version 2.0
 */
public class ctodb {

    private static HikariDataSource dataSource;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/minded";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Initialize connection pool on class load
    static {
        initializeConnectionPool();
        runMigrations();
    }

    /**
     * Initialize HikariCP connection pool with optimized settings
     */
    private static void initializeConnectionPool() {
        try {
            HikariConfig config = new HikariConfig();

            // Database connection settings
            config.setJdbcUrl(DB_URL);
            config.setUsername(DB_USER);
            config.setPassword(DB_PASSWORD);
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");

            // Connection pool settings
            config.setMaximumPoolSize(20);              // Max active connections
            config.setMinimumIdle(5);                   // Min idle connections
            config.setConnectionTimeout(30000);         // 30 seconds
            config.setIdleTimeout(600000);              // 10 minutes
            config.setMaxLifetime(1800000);             // 30 minutes

            // Performance optimizations
            config.setAutoCommit(true);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            // Connection validation
            config.setConnectionTestQuery("SELECT 1");

            // Pool name for monitoring
            config.setPoolName("Minded-DB-Pool");

            dataSource = new HikariDataSource(config);

            System.out.println("✓ HikariCP connection pool initialized successfully");

        } catch (Exception e) {
            System.err.println("✗ Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database connection pool initialization failed", e);
        }
    }

    /**
     * Run Flyway database migrations automatically
     */
    private static void runMigrations() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .locations("filesystem:db/migration")
                    .baselineOnMigrate(true)
                    .load();

            flyway.migrate();
            System.out.println("✓ Flyway migrations completed successfully");

        } catch (Exception e) {
            System.err.println("✗ Flyway migration failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get a connection from the pool
     *
     * @return Database connection
     * @throws SQLException if connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Connection pool not initialized");
        }
        return dataSource.getConnection();
    }

    /**
     * Execute an UPDATE/INSERT/DELETE query using prepared statement
     *
     * @param sql SQL query with ? placeholders
     * @param params Parameters to bind to the query
     * @return Number of affected rows
     */
    public static int executeUpdate(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Bind parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            return pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("✗ Execute update failed: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Execute a SELECT query using prepared statement
     *
     * @param sql SQL query with ? placeholders
     * @param params Parameters to bind to the query
     * @return ResultSet containing query results (must be closed by caller)
     */
    public static ResultSet executeQuery(String sql, Object... params) {
        try {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Bind parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            return pstmt.executeQuery();

        } catch (SQLException e) {
            System.err.println("✗ Execute query failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Close database resources safely
     *
     * @param rs ResultSet to close
     * @param stmt Statement to close
     * @param conn Connection to close
     */
    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (stmt != null) stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get connection pool statistics for monitoring
     *
     * @return Pool statistics as string
     */
    public static String getPoolStats() {
        if (dataSource == null) {
            return "Connection pool not initialized";
        }

        return String.format(
            "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
            dataSource.getHikariPoolMXBean().getActiveConnections(),
            dataSource.getHikariPoolMXBean().getIdleConnections(),
            dataSource.getHikariPoolMXBean().getTotalConnections(),
            dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
        );
    }

    /**
     * Shutdown the connection pool gracefully
     */
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("✓ Connection pool shut down successfully");
        }
    }

    // ========== Legacy Methods for Backward Compatibility ==========

    /**
     * @deprecated Use getConnection() instead
     */
    @Deprecated
    public void conset() {
        // No-op for backward compatibility
        // Connection pooling is initialized automatically
    }

    /**
     * @deprecated Use executeUpdate(sql, params) with prepared statements
     */
    @Deprecated
    public int exquery(String qry) {
        return executeUpdate(qry);
    }

    /**
     * @deprecated Use executeQuery(sql, params) with prepared statements
     */
    @Deprecated
    public ResultSet rsquery(String qry) {
        return executeQuery(qry);
    }
}
