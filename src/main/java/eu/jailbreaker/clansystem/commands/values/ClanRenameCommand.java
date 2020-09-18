package eu.jailbreaker.clansystem.commands.values;

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
            this.messages.commandUsage(player, "rename <Name>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
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

        if (!PatternMatcher.NAME.matches(args[0])) {
            this.messages.sendMessage(player, "invalid_name");
            return;
        }

        final Clan otherClan = this.clanRepository.find(args[0]).join();
        if (otherClan != null) {
            this.messages.sendMessage(player, "name_already_in_use");
            return;
        }

        this.clanRepository.rename(clan, args[0]).whenComplete(
                (unused, throwable) -> this.messages.sendMessage(player, "changed_clan_name", args[0])
        );
    }
}
