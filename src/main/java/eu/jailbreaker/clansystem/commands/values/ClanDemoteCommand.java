package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.InviteRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanDemoteCommand extends ClanCommand {

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private RelationRepository relationRepository;

    @Inject
    private InviteRepository inviteRepository;

    public ClanDemoteCommand() {
        super("demote");
    }

    @Override
    public void execute(Player player, String... args) {
        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            player.sendMessage("§cEin Fehler ist aufgetreten!");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            player.sendMessage("§cDu bist in keinem Clan!");
            return;
        }

        if (clanPlayer.getRole() != ClanRole.OWNER) {
            player.sendMessage("§cDu hast keine Rechte um diese Aktion durchzuführen!");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.find(Bukkit.getPlayerExact(args[0]).getUniqueId()).join();
        if (targetPlayer == null) {
            player.sendMessage("§cDieser Spieler existiert nicht!");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (!clan.equals(targetClan)) {
            player.sendMessage("§cDieser Spieler ist nicht in deinem Clan!");
            return;
        }

        if (targetPlayer.getPlayerId().equals(clan.getCreator())) {
            player.sendMessage("§cDer Claninhaber darf nicht degradiert werden!");
            return;
        }

        if (targetPlayer.getRole() == ClanRole.OWNER && !clan.getCreator().equals(clanPlayer.getPlayerId())) {
            player.sendMessage("§cDu darfst diesen Spieler nicht degradieren!");
            return;
        }

        this.playerRepository.setRole(
                targetPlayer,
                targetPlayer.getRole() == ClanRole.OWNER ? ClanRole.MODERATOR : ClanRole.USER
        ).whenComplete((unused, throwable) -> {
            Bukkit.getPlayerExact(args[0]).sendMessage("§cDu wurdest degradiert");
            player.sendMessage("§cDu hast " + args[0] + " degradiert");
        });
    }
}
