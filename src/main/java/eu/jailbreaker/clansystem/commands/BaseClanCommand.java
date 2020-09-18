package eu.jailbreaker.clansystem.commands;

import com.google.inject.Inject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public final class BaseClanCommand implements CommandExecutor {

    @Inject
    private ClanHelper helper;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cDu musst ein Spieler sein!");
            return true;
        }

        final Player player = (Player) sender;
        CompletableFuture.runAsync(() -> {
            final Optional<ClanCommand> optional = this.helper.find(args[0]);
            if (!optional.isPresent()) {
                player.sendMessage("§cUnbekannter Befehl");
                return;
            }
            String[] arguments = {};
            if (args.length > 1) {
                arguments = Arrays.copyOfRange(args, 1, args.length);
            }
            optional.get().execute(player, arguments);
        });
        return true;
    }
}
