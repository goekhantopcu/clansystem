package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanJumpCommand extends ClanCommand {

    public ClanJumpCommand() {
        super("jump");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan jump <Spieler>");
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
            this.utils.sendMessage(player, "§cIhr seid nicht in einem Clan!");
            return;
        }

        this.utils.sendMessage(player, "§7Verbinde zu " + args[0]);
        this.utils.connect(player.getUniqueId(), uniqueId);
    }
}
