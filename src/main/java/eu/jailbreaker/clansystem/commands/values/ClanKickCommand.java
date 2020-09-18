package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanKickCommand extends ClanCommand {

    public ClanKickCommand() {
        super("kick");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan kick <Spieler>");
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

        if (clanPlayer.getRole() != ClanRole.MODERATOR && clanPlayer.getRole() != ClanRole.OWNER) {
            this.utils.sendMessage(player, "§cDu hast keine Rechte um diesen Spieler aus dem Clan zu werfen!");
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

        if (targetPlayer.getPlayerId().equals(clan.getCreator())) {
            this.utils.sendMessage(player, "§cDer Claninhaber darf nicht aus dem Clan gekickt werden!");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (!clan.equals(targetClan)) {
            this.utils.sendMessage(player, "§cDieser Nutzer ist nicht in deinem Clan!");
            return;
        }

        this.relationRepository.delete(clan, targetPlayer).whenComplete((unused, throwable) -> {
            this.utils.sendMessage(args[0], "§cDu wurdest aus dem Clan " + clan.getName() + " geworfen!");
            this.utils.sendMessage(player, "§cDu hast §4" + args[0] + " §caus dem Clan geschmissen!");

            this.plugin.callTagEvent(Bukkit.getPlayerExact(args[0]));
        });
    }
}
