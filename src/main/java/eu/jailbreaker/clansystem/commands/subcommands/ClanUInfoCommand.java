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
import java.util.UUID;

public final class ClanUInfoCommand extends ClanCommand {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Inject
    private PlayerUtils utils;

    public ClanUInfoCommand() {
        super("uinfo");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.messages.sendCommandUsage(player, "uinfo <Spieler>");
            return;
        }

        final String targetName = args[0];
        if (targetName.equalsIgnoreCase(player.getName())) {
            this.messages.sendMessage(player, "cant_interact_self");
            return;
        }

        final UUID uniqueId = this.utils.getUniqueId(targetName);
        if (uniqueId == null) {
            this.messages.sendMessage(player, "player_does_not_exist");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.findByUniqueId(uniqueId).join();
        final Clan clan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (clan == null) {
            this.messages.sendMessage(player, "target_not_in_clan");
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
