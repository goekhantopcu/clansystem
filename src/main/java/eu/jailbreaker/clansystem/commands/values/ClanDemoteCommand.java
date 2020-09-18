package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanDemoteCommand extends ClanCommand {

    public ClanDemoteCommand() {
        super("demote");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan demote <Spieler>");
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
            this.utils.sendMessage(player, "§cDu bist in keinem Clan!");
            return;
        }

        if (clanPlayer.getRole() != ClanRole.OWNER) {
            this.utils.sendMessage(player, "§cDu hast keine Rechte um diese Aktion durchzuführen!");
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
            this.utils.sendMessage(player, "§cDieser Spieler ist nicht in deinem Clan!");
            return;
        }

        if (targetPlayer.getPlayerId().equals(clan.getCreator())) {
            this.utils.sendMessage(player, "§cDer Claninhaber darf nicht degradiert werden!");
            return;
        }

        if (targetPlayer.getRole() == ClanRole.OWNER && !clan.getCreator().equals(clanPlayer.getPlayerId())) {
            this.utils.sendMessage(player, "§cDu darfst diesen Spieler nicht degradieren!");
            return;
        }

        this.playerRepository.setRole(
                targetPlayer,
                targetPlayer.getRole() == ClanRole.OWNER ? ClanRole.MODERATOR : ClanRole.USER
        ).whenComplete((unused, throwable) -> {
            this.utils.sendMessage(args[0], "§cDu wurdest degradiert");
            this.utils.sendMessage(player, "§cDu hast " + args[0] + " degradiert");
        });
    }
}
