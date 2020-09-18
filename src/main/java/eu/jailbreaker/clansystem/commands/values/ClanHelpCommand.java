package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import org.bukkit.entity.Player;

public final class ClanHelpCommand extends ClanCommand {

    public ClanHelpCommand() {
        super("help");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length == 1) {
            if (args[0].equals("2")) {
                this.sendHelp(player, 1);
            } else {
                this.sendHelp(player, 0);
            }
        } else {
            this.sendHelp(player, 0);
        }
    }

    private void sendHelp(Player player, int pageId) {
        if (pageId == 1) {
            this.messages.sendMessage(player, "help_title", 2, 2);
            this.messages.sendMessage(player, "help_page_2");
        } else {
            this.messages.sendMessage(player, "help_title", 1, 2);
            this.messages.sendMessage(player, "help_page_1");
        }
    }
}
