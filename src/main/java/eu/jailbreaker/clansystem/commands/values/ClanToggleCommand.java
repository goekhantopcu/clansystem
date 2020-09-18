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
            this.utils.sendMessage(player, "Verwende: /clan toggle");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.utils.sendMessage(player, "§cEin Fehler ist aufgetreten!");
            return;
        }

        this.playerRepository.setReceiveInvitations(clanPlayer).whenComplete((unused, throwable) -> {
            this.utils.sendMessage(
                    player,
                    clanPlayer.isReceiveInvitations() ?
                            "§cDu hast die Anfragen deaktiviert!" :
                            "§aDu hast die Anfragen aktiviert!"
            );
        });
    }
}
