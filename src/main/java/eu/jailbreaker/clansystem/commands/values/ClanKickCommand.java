package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanKickCommand extends ClanCommand {

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private RelationRepository relationRepository;

    public ClanKickCommand() {
        super("kick");
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

        if (clanPlayer.getRole() != ClanRole.MODERATOR && clanPlayer.getRole() != ClanRole.OWNER) {
            player.sendMessage("§cDu hast keine Rechte um diesen Spieler aus dem Clan zu werfen!");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.find(Bukkit.getPlayerExact(args[0]).getUniqueId()).join();
        if (targetPlayer == null) {
            player.sendMessage("§cDieser Spieler existiert nicht!");
            return;
        }

        if (targetPlayer.getPlayerId().equals(clan.getCreator())) {
            player.sendMessage("§cDer Claninhaber darf nicht aus dem Clan gekickt werden!");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (!clan.equals(targetClan)) {
            player.sendMessage("§cDieser Nutzer ist nicht in deinem Clan!");
            return;
        }

        this.relationRepository.delete(clan, targetPlayer).whenComplete((unused, throwable) -> {
            Bukkit.getPlayerExact(args[0]).sendMessage("§cDu wurdest aus dem Clan " + clan.getName() + " geworfen!");
            player.sendMessage("§cDu hast §4" + args[0] + " §caus dem Clan geschmissen!");
        });
    }
}
