package eu.jailbreaker.clansystem.utils.player;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.UUID;

public interface PlayerUtils {

    void sendMessage(UUID uniqueId, String message);

    void sendMessage(String name, String message);

    default void sendMessage(Player player, String message) {
        this.sendMessage(player.getUniqueId(), message);
    }

    void connect(UUID uniqueId, UUID to);

    void connect(UUID uniqueId, String server);

    default void connect(Player player, String server) {
        this.connect(player.getUniqueId(), server);
    }

    @Nullable
    String getName(UUID uniqueId);

    @Nullable
    UUID getUniqueId(String name);
}
