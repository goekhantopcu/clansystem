package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

public final class ClanInfoCommand extends ClanCommand {

    public ClanInfoCommand() {
        super("info");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 0) {
            this.utils.sendMessage(player, "Verwende: /clan info");
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

        this.utils.sendMessage(player, "Clanname: " + clan.getName());
        this.utils.sendMessage(player, "Tag: §7[§f" + clan.getTag() + "§7]");
        this.utils.sendMessage(player, "Ersteller: §e" + this.playerRepository.find(clan.getCreator()).join().getUniqueId());
        this.utils.sendMessage(player, "Erstellt am: " + clan.getTimestamp().toString());
    }
}
