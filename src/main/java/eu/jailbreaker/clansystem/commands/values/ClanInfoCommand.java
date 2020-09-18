package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.utils.player.PlayerUtils;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;

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
            this.messages.commandUsage(player, "info");
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

        this.messages.sendMessage(
                player,
                "clan_info",
                clan.getName(),
                clan.getTag(),
                this.utils.getName(this.playerRepository.find(clan.getCreator()).join().getUniqueId()),
                this.formatter.format(clan.getTimestamp().toLocalDateTime())
        );
    }
}
