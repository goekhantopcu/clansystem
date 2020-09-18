package eu.jailbreaker.clansystem.utils.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import eu.jailbreaker.clansystem.ClanSystem;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.logging.Level;

@Singleton
public class SimplePlayerUtilsImpl implements PlayerUtils {

    @Inject
    private ClanSystem plugin;

    @Override
    public void sendMessage(UUID uniqueId, String message) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getPlayer(uniqueId).sendMessage(message);
        } else {
            Bukkit.getScheduler().runTask(
                    this.plugin, () -> Bukkit.getPlayer(uniqueId).sendMessage(message)
            );
        }
    }

    @Override
    public void sendMessage(String name, String message) {
        if (Bukkit.isPrimaryThread()) {
            Bukkit.getPlayerExact(name).sendMessage(message);
        } else {
            Bukkit.getScheduler().runTask(
                    this.plugin, () -> Bukkit.getPlayerExact(name).sendMessage(message)
            );
        }
    }

    @Override
    public void connect(UUID uniqueId, UUID to) {
        this.plugin.getLogger().log(Level.SEVERE, "Server-Switch not supported in SimplePlayerUtilsImpl");
    }

    @Override
    public void connect(UUID uniqueId, String server) {
        this.plugin.getLogger().log(Level.SEVERE, "Server-Switch not supported in SimplePlayerUtilsImpl");
    }

    @Override
    public String getName(UUID uniqueId) {
        return Bukkit.getOfflinePlayer(uniqueId).getName();
    }

    @Override
    public UUID getUniqueId(String name) {
        return Bukkit.getOfflinePlayer(name).getUniqueId();
    }
}
