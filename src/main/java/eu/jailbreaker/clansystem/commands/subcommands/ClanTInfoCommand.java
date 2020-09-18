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

public final class ClanTInfoCommand extends ClanCommand {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Inject
    private PlayerUtils utils;

    public ClanTInfoCommand() {
        super("tinfo");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.messages.sendCommandUsage(player, "tinfo <Clan-Tag>");
            return;
        }

        final String clanTag = args[0];
        final Clan clan = this.clanRepository.findByTag(clanTag).join();
        if (clan == null) {
            this.messages.sendMessage(player, "§cDieser Clan existiert nicht!");
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
