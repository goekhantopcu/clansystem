package eu.jailbreaker.clansystem.commands.subcommands;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.entity.Player;

import java.util.List;

public final class ClanLeaveCommand extends ClanCommand {

    public ClanLeaveCommand() {
        super("leave");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 0) {
            this.messages.sendCommandUsage(player, "leave");
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

        if (clanPlayer.getRole() == ClanRole.OWNER && clan.getCreator().equals(clanPlayer.getPlayerId())) {
            this.messages.sendMessage(player, "cant_leave_as_owner");
            return;
        }

        this.relationRepository.delete(clan, clanPlayer).whenCompleteAsync((unused, throwable) -> {
            final List<ClanPlayer> players = this.relationRepository.findPlayersByClan(clan).join();
            players.forEach(
                    member -> this.messages.sendMessage(member.getUniqueId(), "user_left", player.getName())
            );
            this.messages.sendMessage(player, "clan_left");
            this.plugin.removeClanTag(player);
        });
    }
}
