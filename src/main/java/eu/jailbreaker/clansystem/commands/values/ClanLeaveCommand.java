package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.InviteRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public final class ClanLeaveCommand extends ClanCommand {

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private RelationRepository relationRepository;

    @Inject
    private InviteRepository inviteRepository;

    public ClanLeaveCommand() {
        super("leave");
    }

    @Override
    public void execute(Player player, String... args) {
        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            player.sendMessage("§cEin Fehler ist aufgetreten!");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            player.sendMessage("§cDu bist in keinem Clan!");
            return;
        }

        if (clanPlayer.getRole() == ClanRole.OWNER && clan.getCreator().equals(clanPlayer.getPlayerId())) {
            player.sendMessage("§cDu kannst deinen Clan nicht verlassen, du musst ihn mit /clan delete löschen!");
            return;
        }

        this.relationRepository.delete(clan, clanPlayer).whenCompleteAsync((unused, throwable) -> {
            final List<ClanPlayer> players = this.relationRepository.findPlayersByClan(clan).join();
            players.forEach(targetPlayer -> Bukkit.getPlayer(targetPlayer.getUniqueId()).sendMessage("§c" + player.getName() + " hat den Clan verlassen!"));
            player.sendMessage("§cDu hast den Clan verlassen!");
        });
    }
}
