package eu.jailbreaker.clansystem.commands.subcommands;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.utils.player.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.List;

public final class ClanInfoCommand extends ClanCommand {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Inject
    private PlayerUtils utils;

    public ClanInfoCommand() {
        super("info");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 0) {
            this.messages.sendCommandUsage(player, "info");
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

        final List<ClanPlayer> players = this.relationRepository.findPlayersByClan(clan).join();
        this.messages.sendMessage(
                player,
                "clan_info",
                clan.getDisplayName(),
                clan.getDisplayTag(),
                players.size(),
                this.messages.accumulateClanMembers(players, ClanRole.OWNER, ChatColor.DARK_RED),
                this.messages.accumulateClanMembers(players, ClanRole.MODERATOR, ChatColor.RED),
                this.messages.accumulateClanMembers(players, ClanRole.USER, ChatColor.GRAY)
        );
    }
}
