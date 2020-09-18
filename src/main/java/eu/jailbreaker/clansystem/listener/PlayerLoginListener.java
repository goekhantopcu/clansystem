package eu.jailbreaker.clansystem.listener;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public final class PlayerLoginListener implements Listener {

    @Inject
    private PlayerRepository playerRepository;

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        this.playerRepository.find(player.getUniqueId()).whenComplete((clanPlayer, throwable) -> {
            if (clanPlayer == null) {
                this.playerRepository.create(player.getUniqueId());
            }
        });
    }
}
