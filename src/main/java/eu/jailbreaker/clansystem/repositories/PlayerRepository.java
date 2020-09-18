package eu.jailbreaker.clansystem.repositories;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.jailbreaker.clansystem.db.DatabaseConnection;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;

import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Singleton
public final class PlayerRepository {

    @Inject
    private DatabaseConnection connection;

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private RelationRepository relationRepository;

    public CompletableFuture<Void> createTable() {
        return this.connection.update(
                "CREATE TABLE IF NOT EXISTS players (" +
                        "playerId INT(11) NOT NULL AUTO_INCREMENT, " +
                        "role VARCHAR(36) NOT NULL DEFAULT 'USER', " +
                        "uniqueId VARCHAR(36) NOT NULL, " +
                        "receive_invitations TINYINT(1) NOT NULL DEFAULT 1, " +
                        "PRIMARY KEY(playerId)" +
                        ")"
        );
    }

    public CompletableFuture<ClanPlayer> findByUniqueId(UUID uniqueId) {
        return this.fromResult(this.connection.execute(
                "SELECT * FROM players WHERE uniqueId=?",
                uniqueId.toString()
        ));
    }

    public CompletableFuture<ClanPlayer> findById(Integer playerId) {
        return this.fromResult(this.connection.execute(
                "SELECT * FROM players WHERE playerId=?",
                playerId
        ));
    }

    public void create(UUID uniqueId) {
        this.connection.insert(
                "INSERT INTO players (uniqueId) VALUES (?)",
                uniqueId.toString()
        );
    }

    private CompletableFuture<ClanPlayer> fromResult(CompletableFuture<ResultSet> future) {
        return future.thenApplyAsync(result -> {
            try {
                if (result.next()) {
                    final ClanPlayer clanPlayer = ClanPlayer.create(
                            result.getInt(1),
                            UUID.fromString(result.getString("uniqueId"))
                    );
                    clanPlayer.setRole(ClanRole.valueOf(result.getString("role").toUpperCase()));
                    clanPlayer.setReceiveInvitations(result.getBoolean("receive_invitations"));
                    return clanPlayer;
                }
                return null;
            } catch (Throwable throwable) {
                return null;
            }
        });
    }

    public CompletableFuture<Void> setRole(ClanPlayer clanPlayer, ClanRole clanRole) {
        try {
            return this.connection.update(
                    "UPDATE players SET role=? WHERE playerId=?",
                    clanRole.name(),
                    clanPlayer.getPlayerId()
            );
        } catch (Throwable throwable) {
            return CompletableFuture.completedFuture(null);
        }
    }

    public void setClan(ClanPlayer clanPlayer, Clan clan, ClanRole role) {
        this.connection.insert(
                "INSERT INTO clan_player_relation (clanId, playerId) VALUES (?, ?)",
                clan.getClanId(),
                clanPlayer.getPlayerId()
        ).exceptionally(throwable -> {
            this.connection.update(
                    "UPDATE clan_player_relation SET clanId=? WHERE playerId=?",
                    clan.getClanId(),
                    clanPlayer.getPlayerId()
            );
            return null;
        }).whenComplete((unused, throwable) -> this.setRole(clanPlayer, role));
    }

    public CompletionStage<Void> updateReceiveInvitations(ClanPlayer clanPlayer) {
        return this.connection.update(
                "UPDATE players SET receive_invitations=? WHERE playerId=?",
                !clanPlayer.isReceiveInvitations(),
                clanPlayer.getPlayerId()
        );
    }
}
