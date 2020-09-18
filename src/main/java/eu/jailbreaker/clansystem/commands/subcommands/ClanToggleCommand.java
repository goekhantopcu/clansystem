package eu.jailbreaker.clansystem.commands.subcommands;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

public final class ClanToggleCommand extends ClanCommand {

    public ClanToggleCommand() {
        super("toggle");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 0) {
            this.messages.sendCommandUsage(player, "toggle");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.findByUniqueId(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        this.playerRepository.updateReceiveInvitations(clanPlayer).whenComplete((unused, throwable) -> {
            this.messages.sendMessage(
                    player,
                    clanPlayer.isReceiveInvitations() ? "invitations_deactivated" : "invitations_activated"
            );
        });
    }
}
