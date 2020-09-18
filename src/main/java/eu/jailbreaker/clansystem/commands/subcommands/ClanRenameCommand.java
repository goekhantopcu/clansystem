package eu.jailbreaker.clansystem.commands.subcommands;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.utils.PatternMatcher;
import org.bukkit.entity.Player;

public final class ClanRenameCommand extends ClanCommand {

    public ClanRenameCommand() {
        super("rename");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.messages.sendCommandUsage(player, "rename <Name>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.findByUniqueId(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            this.messages.sendMessage(player, "not_in_clan");
            return;
        }

        if (!clan.getCreator().equals(clanPlayer.getPlayerId())) {
            this.messages.sendMessage(player, "not_permitted");
            return;
        }

        final String newClanName = args[0];
        if (!PatternMatcher.NAME.matches(newClanName)) {
            this.messages.sendMessage(player, "invalid_name");
            return;
        }

        final Clan otherClan = this.clanRepository.findByName(newClanName).join();
        if (otherClan != null) {
            this.messages.sendMessage(player, "name_already_in_use");
            return;
        }

        this.clanRepository.rename(clan, newClanName).whenComplete(
                (unused, throwable) -> this.messages.sendMessage(player, "changed_clan_name", newClanName)
        );
    }
}
