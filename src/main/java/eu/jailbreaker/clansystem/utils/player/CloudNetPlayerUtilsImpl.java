package eu.jailbreaker.clansystem.utils.player;

import com.google.inject.Singleton;
import de.dytanic.cloudnet.api.CloudAPI;
import de.dytanic.cloudnet.api.player.PlayerExecutorBridge;
import de.dytanic.cloudnet.lib.player.CloudPlayer;

import java.util.UUID;

@Singleton
public final class CloudNetPlayerUtilsImpl implements PlayerUtils {

    @Override
    public void sendMessage(UUID uniqueId, String message) {
        final CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uniqueId);
        if (cloudPlayer != null) {
            PlayerExecutorBridge.INSTANCE.sendMessage(cloudPlayer, message);
        }
    }

    @Override
    public void sendMessage(String name, String message) {
        final UUID uniqueId = CloudAPI.getInstance().getPlayerUniqueId(name);
        if (uniqueId != null) {
            this.sendMessage(uniqueId, message);
        }
    }

    @Override
    public void connect(UUID uniqueId, UUID to) {
        final CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uniqueId);
        final CloudPlayer cloudPlayerTo = CloudAPI.getInstance().getOnlinePlayer(to);
        if (cloudPlayer != null && cloudPlayerTo != null) {
            PlayerExecutorBridge.INSTANCE.sendPlayer(cloudPlayer, cloudPlayerTo.getServer());
        }
    }

    @Override
    public void connect(UUID uniqueId, String server) {
        final CloudPlayer cloudPlayer = CloudAPI.getInstance().getOnlinePlayer(uniqueId);
        if (cloudPlayer != null) {
            PlayerExecutorBridge.INSTANCE.sendPlayer(cloudPlayer, server);
        }
    }

    @Override
    public String getName(UUID uniqueId) {
        return CloudAPI.getInstance().getPlayerName(uniqueId);
    }

    @Override
    public UUID getUniqueId(String name) {
        return CloudAPI.getInstance().getPlayerUniqueId(name);
    }
}
