package eu.jailbreaker.clansystem.db;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.jailbreaker.clansystem.ClanSystem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Closeable;
import java.sql.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Singleton
public final class DatabaseConnection implements Closeable {

    @Inject
    private ClanSystem plugin;

    @Inject
    private Connection connection;

    public boolean isNotConnected() {
        return this.connection == null;
    }

    @NotNull
    public CompletableFuture<@Nullable Void> update(@NotNull String query, Object... parameters) {
        if (this.isNotConnected()) {
            this.plugin.getLogger().log(Level.SEVERE, "No active connection!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            try {
                this.createStatement(query, parameters).executeUpdate();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        });
    }

    @NotNull
    public CompletableFuture<ResultSet> insert(@NotNull String query, Object... parameters) {
        if (this.isNotConnected()) {
            this.plugin.getLogger().log(Level.SEVERE, "No active connection!");
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.supplyAsync(() -> {
            try {
                final PreparedStatement statement = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
                statement.executeUpdate();
                return statement.getGeneratedKeys();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @NotNull
    public CompletableFuture<ResultSet> execute(String query, Object... parameters) {
        if (this.isNotConnected()) {
            this.plugin.getLogger().log(Level.SEVERE, "No active connection!");
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                final PreparedStatement statement = this.createStatement(query, parameters);
                return statement.executeQuery();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @NotNull
    private PreparedStatement createStatement(@NotNull String query, Object... parameters) throws SQLException {
        final PreparedStatement statement = this.connection.prepareStatement(query);
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
        return statement;
    }

    @Override
    public void close() {
        if (this.isNotConnected()) {
            return;
        }

        try {
            this.connection.close();
            this.plugin.getLogger().log(Level.INFO, "Successfully disconnected from database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
