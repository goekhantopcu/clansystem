package eu.jailbreaker.clansystem.commands;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.utils.player.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class BaseClanCommand implements CommandExecutor {

    @Inject
    private ClanCommandRegistry helper;

    @Inject
    private PlayerUtils utils;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDu musst ein Spieler sein!");
            return true;
        }

        final Player player = (Player) sender;
        if (args.length == 0) {
            this.helper.findCommand("help").ifPresent(subCommand -> subCommand.execute(player));
            return true;
        }

        final String subCommandName = args[0];
        if (args.length == 1 && (subCommandName.equals("1") || subCommandName.equals("2"))) {
            player.performCommand("clan help " + subCommandName);
            return true;
        }

        CompletableFuture.runAsync(() -> {
            try {
                final Optional<ClanCommand> optional = this.helper.findCommand(subCommandName);
                if (!optional.isPresent()) {
                    this.helper.findCommand("help").ifPresent(subCommand -> subCommand.execute(player));
                    return;
                }
                String[] arguments = {};
                if (args.length > 1) {
                    arguments = Arrays.copyOfRange(args, 1, args.length);
                }
                optional.get().execute(player, arguments);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        return true;
    }
}
