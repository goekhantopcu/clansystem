package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanPromoteCommand extends ClanCommand {

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private RelationRepository relationRepository;

    public ClanPromoteCommand() {
        super("promote");
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
            player.sendMessage("§cDu hast keinen Clan");
            return;
        }

        if (clanPlayer.getRole() != ClanRole.OWNER) {
            player.sendMessage("§cDu bist nicht der Inhaber des Clans");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.find(Bukkit.getPlayerExact(args[0]).getUniqueId()).join();
        if (targetPlayer == null) {
            player.sendMessage("§cDieser Spieler existiert nicht!");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (!clan.equals(targetClan)) {
            player.sendMessage("§cDieser Nutzer ist nicht in deinem Clan!");
            return;
        }

        this.playerRepository.setRole(
                clanPlayer,
                targetPlayer.getRole() == ClanRole.USER ? ClanRole.MODERATOR : ClanRole.OWNER
        ).whenComplete((unused, throwable) -> {
            Bukkit.getPlayerExact(args[0]).sendMessage("§aDu wurdest befördert");
            player.sendMessage("§aDu hast " + args[0] + " befördert");
        });
    }
}
