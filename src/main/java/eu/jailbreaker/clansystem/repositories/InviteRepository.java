package eu.jailbreaker.clansystem.repositories;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import eu.jailbreaker.clansystem.db.DatabaseConnection;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanInvite;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class InviteRepository {

    @Inject
    private DatabaseConnection connection;

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private PlayerRepository playerRepository;

    public CompletableFuture<Void> createTable() {
        return this.connection.update(
                "CREATE TABLE IF NOT EXISTS clan_invitations ( " +
                        "invitationId INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                        "clanId INT (11) NOT NULL, " +
                        "invitedId INT(11) NOT NULL " +
                        ")"
        );
    }

    public CompletableFuture<Void> create(Clan clan, ClanPlayer target) {
        return this.connection.update(
                "INSERT INTO clan_invitations (clanId, invitedId) VALUES (?, ?)",
                clan.getClanId(),
                target.getPlayerId()
        );
    }

    public CompletableFuture<Clan> accept(String name, ClanPlayer clanPlayer) {
        return this.clanRepository.find(name).whenComplete(
                (clan, throwable) -> this.playerRepository.setClan(clanPlayer, clan, ClanRole.USER)
        );
    }

    public void deny(String name, ClanPlayer clanPlayer) {
        this.clanRepository.find(name).whenComplete((clan, throwable) -> this.connection.update(
                "DELETE FROM clan_invitations WHERE clanId=? AND invitedId=?",
                clan.getClanId(),
                clanPlayer.getPlayerId()
        ));
    }

    public CompletableFuture<List<ClanInvite>> findInvitationsByPlayer(ClanPlayer clanPlayer) {
        return this.connection.execute(
                "SELECT * FROM clan_invitations WHERE invitedId=?",
                clanPlayer.getPlayerId()
        ).thenApplyAsync(result -> {
            final List<ClanInvite> invites = Lists.newArrayList();
            try {
                while (result.next()) {
                    invites.add(ClanInvite.create(
                            result.getInt("clanId"),
                            result.getInt("invitationId"),
                            result.getInt("invitedId")
                    ));
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return invites;
        });
    }

    public CompletableFuture<ClanInvite> findInvitationByClanAndPlayer(Clan clan, ClanPlayer target) {
        return this.connection.execute(
                "SELECT * FROM clan_invitations WHERE clanId=? AND invitedId=?",
                clan.getClanId(),
                target.getPlayerId()
        ).thenApplyAsync(result -> {
            try {
                if (result.next()) {
                    return ClanInvite.create(
                            result.getInt("clanId"),
                            result.getInt("invitationId"),
                            result.getInt("invitedId")
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
