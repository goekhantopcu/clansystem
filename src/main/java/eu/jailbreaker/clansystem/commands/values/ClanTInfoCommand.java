package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.utils.player.PlayerUtils;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;

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
            this.messages.commandUsage(player, "tinfo <Clan-Tag>");
            return;
        }

        final Clan clan = this.clanRepository.findByTag(args[0]).join();
        if (clan == null) {
            this.messages.sendMessage(player, "§cDieser Clan existiert nicht!");
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
