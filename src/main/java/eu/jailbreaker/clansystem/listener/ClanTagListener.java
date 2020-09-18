package eu.jailbreaker.clansystem.listener;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.ClanSystem;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.events.ClanTagAddEvent;
import eu.jailbreaker.clansystem.events.ClanTagRemoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.UUID;

public final class ClanTagListener implements Listener {

    @Inject
    private ClanSystem plugin;

    @EventHandler
    public void onClanTagRemove(ClanTagRemoveEvent event) {
        final Player player = event.getPlayer();
        final String listName = this.plugin.getListNames().containsKey(player.getUniqueId()) ?
                this.plugin.getListNames().get(player.getUniqueId()) :
                player.getPlayerListName();
        player.setPlayerListName(listName);
    }

    @EventHandler
    public void onClanTagAdd(ClanTagAddEvent event) {
        final Clan clan = event.getClan();
        final Player player = event.getPlayer();
        final UUID uniqueId = player.getUniqueId();
        final String suffix = "§8[" + clan.getDisplayTag() + "§8]";
        final Map<UUID, String> listNames = this.plugin.getListNames();

        if (listNames.containsKey(uniqueId)) {
            player.setPlayerListName(listNames.get(uniqueId) + suffix);
        } else {
            listNames.put(uniqueId, player.getPlayerListName());
            player.setPlayerListName(player.getPlayerListName() + suffix);
        }
    }
}
