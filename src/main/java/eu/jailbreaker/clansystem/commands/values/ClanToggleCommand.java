package eu.jailbreaker.clansystem.commands.values;

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
            this.messages.commandUsage(player, "toggle");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        this.playerRepository.setReceiveInvitations(clanPlayer).whenComplete((unused, throwable) -> {
            this.messages.sendMessage(
                    player,
                    clanPlayer.isReceiveInvitations() ?
                            "invitations_deactivated" :
                            "invitations_activated"
            );
        });
    }
}
