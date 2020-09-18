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

        this.clanRepository.rename(clan, args[0]).whenComplete(
                (unused, throwable) -> this.messages.sendMessage(player, "changed_clan_name", args[0])
        );
    }
}
