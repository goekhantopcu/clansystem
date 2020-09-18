package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.entity.Player;

public final class ClanToggleCommand extends ClanCommand {

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private RelationRepository relationRepository;

    public ClanToggleCommand() {
        super("toggle");
    }

    @Override
    public void execute(Player player, String... args) {
        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            player.sendMessage("§cEin Fehler ist aufgetreten!");
            return;
        }

        this.playerRepository.setReceiveInvitations(clanPlayer).whenComplete((unused, throwable) -> {
            player.sendMessage(
                    clanPlayer.isReceiveInvitations() ?
                            "§cDu hast die Anfragen deaktiviert!" :
                            "§aDu hast die Anfragen aktiviert!"
            );
        });
    }
}
