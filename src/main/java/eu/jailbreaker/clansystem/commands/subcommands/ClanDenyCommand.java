package eu.jailbreaker.clansystem.commands.subcommands;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

public final class ClanDenyCommand extends ClanCommand {

    public ClanDenyCommand() {
        super("deny");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.messages.sendCommandUsage(player, "deny <Clan-Name>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.findByUniqueId(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        final String clanName = args[0];
        this.inviteRepository.deny(clanName, clanPlayer).whenComplete((clan, throwable) -> {
            if (clan == null) {
                this.messages.sendMessage(player, "no_invitation_received");
                return;
            }
            this.messages.sendMessage(player, "denied_invitation", clan.getDisplayName());
        });
    }
}
