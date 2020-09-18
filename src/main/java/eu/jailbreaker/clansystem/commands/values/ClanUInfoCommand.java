package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanUInfoCommand extends ClanCommand {

    public ClanUInfoCommand() {
        super("uinfo");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan uinfo <Spieler>");
            return;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            this.utils.sendMessage(player, "§cVerwende /clan info!");
            return;
        }

        final UUID uniqueId = this.utils.getUniqueId(args[0]);
        if (uniqueId == null) {
            this.utils.sendMessage(player, "§cDieser Spieler existiert nicht!");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.find(uniqueId).join();
        final Clan clan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (clan == null) {
            this.utils.sendMessage(player, "§cDieser Spieler ist in keinem Clan!");
            return;
        }

        this.utils.sendMessage(player, "Clanname: " + clan.getName());
        this.utils.sendMessage(player, "Tag: §7[§f" + clan.getTag() + "§7]");
        this.utils.sendMessage(player, "Ersteller: §e" + this.playerRepository.find(clan.getCreator()).join().getUniqueId());
        this.utils.sendMessage(player, "Erstellt am: " + clan.getTimestamp().toString());
    }
}
