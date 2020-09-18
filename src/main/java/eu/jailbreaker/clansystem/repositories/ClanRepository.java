package eu.jailbreaker.clansystem.repositories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.jailbreaker.clansystem.db.DatabaseConnection;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class ClanRepository {

    @Inject
    private DatabaseConnection connection;

    public CompletableFuture<Void> createTable() {
        return this.connection.update(
                "CREATE TABLE IF NOT EXISTS clans (" +
                        "clanId INT(11) NOT NULL AUTO_INCREMENT, " +
                        "creator INT(11) NOT NULL, " +
                        "create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(), " +
                        "name VARCHAR(20) NOT NULL, " +
                        "tag VARCHAR(4) NOT NULL," +
                        "PRIMARY KEY(clanId)" +
                        ")"
        );
    }

    public CompletableFuture<Clan> find(Integer clanId) {
        return this.fromResult(this.connection.execute(
                "SELECT * FROM clans WHERE clanId=?",
                clanId
        ));
    }

    public CompletableFuture<Clan> find(String name) {
        return this.fromResult(this.connection.execute(
                "SELECT * FROM clans WHERE name LIKE ?",
                name
        ));
    }

    public CompletableFuture<Clan> findByTag(String tag) {
        return this.fromResult(this.connection.execute(
                "SELECT * FROM clans WHERE tag LIKE ?",
                tag
        ));
    }

    public CompletableFuture<Clan> create(ClanPlayer creator, String name, String tag) {
        final Timestamp timestamp = Timestamp.from(Instant.now());
        return this.connection.insert(
                "INSERT INTO `clans`(`creator`, `name`, `tag`, `create_date`) VALUES (?, ?, ?, ?)",
                creator.getPlayerId(),
                name,
                tag,
                timestamp
        ).thenApply(result -> {
            try {
                if (result.next()) {
                    return Clan.create(
                            result.getInt(1),
                            creator.getPlayerId(),
                            timestamp,
                            name,
                            tag
                    );
                }
                return null;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
            }
        });
    }

    public CompletableFuture<Void> delete(Clan clan) {
        return this.connection.update(
                "DELETE FROM clans WHERE clanId=?",
                clan.getClanId()
        ).whenComplete((unused, throwable) -> this.connection.update(
                "DELETE FROM clan_player_relation WHERE clanId=?",
                clan.getClanId()
        ));
    }

    public CompletableFuture<Void> rename(Clan clan, String name) {
        return this.connection.update(
                "UPDATE clans SET name=? WHERE clanId=?",
                name,
                clan.getClanId()
        );
    }

    public CompletableFuture<Void> setTag(Clan clan, String tag) {
        return this.connection.update(
                "UPDATE clans SET tag=? WHERE clanId=?",
                tag,
                clan.getClanId()
        );
    }

    protected CompletableFuture<Clan> fromResult(CompletableFuture<ResultSet> future) {
        return future.thenApply(result -> {
            try {
                if (result.next()) {
                    return Clan.create(
                            result.getInt("clanId"),
                            result.getInt("creator"),
                            result.getTimestamp("create_date"),
                            result.getString("name"),
                            result.getString("tag")
                    );
                }
                return null;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return null;
            }
        });
    }
}
