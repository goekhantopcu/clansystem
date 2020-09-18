package eu.jailbreaker.clansystem.commands.subcommands;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public final class ClanJoinCommand extends ClanCommand {

    public ClanJoinCommand() {
        super("join");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.messages.sendCommandUsage(player, "join <Clan-Name>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.findByUniqueId(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        final String clanName = args[0];
        this.inviteRepository.accept(clanName, clanPlayer).whenCompleteAsync((clan, throwable) -> {
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
            this.messages.sendMessage(player, "joined_clan", clan.getDisplayName());
            this.plugin.callTagAddEvent(player.getUniqueId(), clan);
        });
    }
}
