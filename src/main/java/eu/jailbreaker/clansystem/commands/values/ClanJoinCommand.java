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
            this.messages.commandUsage(player, "join <Clan-Name>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        this.inviteRepository.accept(args[0], clanPlayer).whenCompleteAsync((clan, throwable) -> {
            if (clan == null) {
                this.messages.sendMessage(player, "no_invitation_received");
                return;
            }

            final List<ClanPlayer> players = this.relationRepository.findPlayersByClan(clan).join();
            players.forEach(member -> this.messages.sendMessage(
                    member.getUniqueId(),
                    "target_joined",
                    player.getName()
            ));
            this.messages.sendMessage(player, "joined_clan", clan.getName());
            this.plugin.callTagEvent(player, clan);
        });
    }
}
