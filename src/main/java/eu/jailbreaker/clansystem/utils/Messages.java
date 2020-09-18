package eu.jailbreaker.clansystem.utils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import eu.jailbreaker.clansystem.utils.player.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.MessageFormat;
import java.util.UUID;

public final class Messages {

    @Inject
    private PlayerUtils utils;

    @Inject
    @Named("messages")
    private Configuration messages;

    private String formatInput(String path, Object... replacement) {
        return ChatColor.translateAlternateColorCodes(
                '&',
                MessageFormat.format(this.messages.getString(path), replacement)
        );
    }

    public void sendMessage(UUID uniqueId, String path, Object... replacement) {
        this.utils.sendMessage(uniqueId, this.formatInput(path, replacement));
    }

    public void sendMessage(Player player, String path, Object... replacement) {
        this.utils.sendMessage(player, this.formatInput(path, replacement));
    }

    public void sendMessage(String name, String path, Object... replacement) {
        this.utils.sendMessage(name, this.formatInput(path, replacement));
    }

    public void commandUsage(Player player, String command) {
        this.utils.sendMessage(player, this.formatInput("command_usage", command));
    }
}
