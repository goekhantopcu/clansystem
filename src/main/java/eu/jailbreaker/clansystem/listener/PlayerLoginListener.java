package eu.jailbreaker.clansystem.listener;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.ClanSystem;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public final class PlayerLoginListener implements Listener {

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private RelationRepository relationRepository;

    @Inject
    private ClanSystem plugin;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        this.plugin.getListNames().put(uniqueId, player.getPlayerListName());

        this.playerRepository.findByUniqueId(uniqueId).whenCompleteAsync((clanPlayer, throwable) -> {
            if (clanPlayer == null) {
                this.playerRepository.create(uniqueId);
            } else {
                final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
                if (clan != null) {
                    this.plugin.setClanTag(player, clan);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.plugin.getListNames().remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        this.plugin.getListNames().remove(event.getPlayer().getUniqueId());
    }
}
