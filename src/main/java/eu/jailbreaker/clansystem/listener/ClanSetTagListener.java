package eu.jailbreaker.clansystem.listener;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.ClanSystem;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.events.ClanSetTagEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ClanSetTagListener implements Listener {

    @Inject
    private ClanSystem plugin;

    @EventHandler
    public void onClanSetTag(ClanSetTagEvent event) {
        final Player player = event.getPlayer();
        final Clan clan = event.getClan();
        if (clan == null) {
            if (this.plugin.getListNames().containsKey(player.getUniqueId())) {
                player.setPlayerListName(this.plugin.getListNames().get(player.getUniqueId()));
            }
        } else {
            final String suffix = clan.getName().equalsIgnoreCase("Team") ?
                    " §8[§cTeam§8]" :
                    " §8[" + clan.getDisplayTag() + "§8]";
            if (this.plugin.getListNames().containsKey(player.getUniqueId())) {
                player.setPlayerListName(this.plugin.getListNames().get(player.getUniqueId()) + suffix);
            } else {
                this.plugin.getListNames().put(player.getUniqueId(), player.getPlayerListName());
                player.setPlayerListName(
                        player.getPlayerListName() + suffix
                );
            }
        }
    }
}
