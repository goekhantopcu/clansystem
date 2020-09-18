package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanPromoteCommand extends ClanCommand {

    public ClanPromoteCommand() {
        super("promote");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan promote <Spieler>");
            return;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            this.utils.sendMessage(player, "§cDu darfst nicht mit dir selbst interagieren!");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.utils.sendMessage(player, "§cEin Fehler ist aufgetreten!");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            this.utils.sendMessage(player, "§cDu hast keinen Clan");
            return;
        }

        if (clanPlayer.getRole() != ClanRole.OWNER) {
            this.utils.sendMessage(player, "§cDu bist nicht der Inhaber des Clans");
            return;
        }

        final UUID uniqueId = this.utils.getUniqueId(args[0]);
        if (uniqueId == null) {
            this.utils.sendMessage(player, "§cDieser Spieler existiert nicht!");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.find(uniqueId).join();
        if (targetPlayer == null) {
            this.utils.sendMessage(player, "§cDieser Spieler existiert nicht!");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (!clan.equals(targetClan)) {
            this.utils.sendMessage(player, "§cDieser Nutzer ist nicht in deinem Clan!");
            return;
        }

        this.playerRepository.setRole(
                clanPlayer,
                targetPlayer.getRole() == ClanRole.USER ? ClanRole.MODERATOR : ClanRole.OWNER
        ).whenComplete((unused, throwable) -> {
            this.utils.sendMessage(args[0], "§aDu wurdest befördert");
            this.utils.sendMessage(player, "§aDu hast " + args[0] + " befördert");
        });
    }
}
