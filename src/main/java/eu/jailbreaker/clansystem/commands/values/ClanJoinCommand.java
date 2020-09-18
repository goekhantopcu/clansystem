package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class ClanJoinCommand extends ClanCommand {

    public ClanJoinCommand() {
        super("join", Collections.singletonList("accept"));
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan join <Clan-Name>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.utils.sendMessage(player, "§cEin Fehler ist aufgetreten!");
            return;
        }

        this.inviteRepository.accept(args[0], clanPlayer).whenCompleteAsync((clan, throwable) -> {
            if (clan == null) {
                this.utils.sendMessage(player, "§cDu hast keine Einladung von diesem Clan erhalten!");
                return;
            }

            final List<ClanPlayer> players = this.relationRepository.findPlayersByClan(clan).join();
            players.forEach(member -> this.utils.sendMessage(
                    member.getUniqueId(),
                    "§a" + player.getName() + " §7ist dem Clan beigetreten!"
            ));
            this.utils.sendMessage(player, "§aDu bist nun ein Mitglied des Clans " + clan.getName());
            this.plugin.callTagEvent(player, clan);
        });
    }
}
