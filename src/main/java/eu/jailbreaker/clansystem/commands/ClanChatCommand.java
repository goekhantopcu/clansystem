package eu.jailbreaker.clansystem.commands;

import com.google.inject.Inject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ClanChatCommand implements CommandExecutor {

    @Inject
    private ClanHelper helper;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDu musst ein Spieler sein!");
            return true;
        }

        final Player player = (Player) sender;

        helper.find("chat").ifPresent(
                subCommand -> subCommand.execute(player, args)
        );
        return true;
    }
}
