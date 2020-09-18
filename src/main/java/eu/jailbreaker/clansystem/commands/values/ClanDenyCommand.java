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
            this.utils.sendMessage(player, "Verwende: /clan deny <Clan-Name>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.utils.sendMessage(player, "§cEin Fehler ist aufgetreten!");
            return;
        }

        this.inviteRepository.deny(args[0], clanPlayer).whenComplete((clan, throwable) -> {
            if (clan == null) {
                this.utils.sendMessage(player, "§cDu hast keine Einladung von diesem Clan erhalten!");
                return;
            }
            this.utils.sendMessage(player, "§aDu hast die Anfrage des Clans " + clan.getName() + " abgelehnt!");
        });
    }
}
