package eu.jailbreaker.clansystem.commands.values;

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
            this.messages.commandUsage(player, "deny <Clan-Name>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        this.inviteRepository.deny(args[0], clanPlayer).whenComplete((clan, throwable) -> {
            if (clan == null) {
                this.messages.sendMessage(player, "no_invitation_received");
                return;
            }
            this.messages.sendMessage(player, "denied_invitation", clan.getName());
        });
    }
}
