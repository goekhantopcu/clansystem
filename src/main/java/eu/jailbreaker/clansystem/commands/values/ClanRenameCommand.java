package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

public final class ClanRenameCommand extends ClanCommand {

    public ClanRenameCommand() {
        super("rename");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan rename <Neuer Name>");
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

        if (!clan.getCreator().equals(clanPlayer.getPlayerId())) {
            this.utils.sendMessage(player, "§cDu bist nicht der ClanInhaber");
            return;
        }

        this.clanRepository.rename(clan, args[0]).whenComplete(
                (unused, throwable) -> this.utils.sendMessage(player, "§7Dein neuer Clanname: §e" + args[0])
        );
    }
}
