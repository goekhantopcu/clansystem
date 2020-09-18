package eu.jailbreaker.clansystem.repositories;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.jailbreaker.clansystem.db.DatabaseConnection;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Singleton
public final class RelationRepository {

    @Inject
    private DatabaseConnection connection;

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private PlayerRepository playerRepository;

    public CompletableFuture<Void> createTable() {
        return this.connection.update(
                "CREATE TABLE IF NOT EXISTS clan_player_relation ( " +
                        "clanId INT(11) NOT NULL, " +
                        "playerId INT(11) NOT NULL, " +
                        "PRIMARY KEY(clanId, playerId) " +
                        ")"
        );
    }

    public CompletableFuture<Void> delete(Clan clan, ClanPlayer clanPlayer) {
        return this.connection.update(
                "DELETE FROM clan_player_relation WHERE clanId=? AND playerId=?",
                clan.getClanId(),
                clanPlayer.getPlayerId()
        );
    }

    public CompletableFuture<Clan> findClanByPlayer(ClanPlayer clanPlayer) {
        try {
            return this.clanRepository.fromResult(this.connection.execute(
                    "SELECT * FROM clans " +
                            "INNER JOIN clan_player_relation " +
                            "ON clan_player_relation.clanId=clans.clanId " +
                            "WHERE clan_player_relation.playerId=?",
                    clanPlayer.getPlayerId()
            ));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return null;
        }
    }

    public CompletableFuture<List<ClanPlayer>> findPlayersByClan(Clan clan) {
        return this.connection.execute(
                "SELECT playerId FROM clan_player_relation WHERE clanId=?",
                clan.getClanId()
        ).thenApplyAsync(result -> {
            final List<ClanPlayer> players = Lists.newArrayList();
            try {
                while (result.next()) {
                    final int playerId = result.getInt(1);
                    final ClanPlayer clanPlayer = this.playerRepository.find(playerId).join();
                    if (clanPlayer != null) {
                        players.add(clanPlayer);
                    }
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return players;
        });
    }
}
