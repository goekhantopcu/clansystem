package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public final class ClanSetTagColorCommand extends ClanCommand {

    private final String formattedColors = this.formatColors();

    public ClanSetTagColorCommand() {
        super("color");
    }

    @Override
    public void execute(Player player, String... args) {
        if (!player.hasPermission("clansystem.admin")) {
            this.messages.sendMessage(player, "no_perm");
            return;
        }

        if (args.length != 2) {
            this.messages.commandUsage(player, "color <Name> <" + this.formattedColors + "§7>");
            return;
        }

        final Clan clan = this.clanRepository.find(args[0]).join();
        if (clan == null) {
            this.messages.sendMessage(player, "clan_not_exist");
            return;
        }

        try {
            this.clanRepository.setTagColor(
                    clan,
                    ChatColor.valueOf(args[1].toUpperCase())
            ).whenComplete((unused, throwable) ->
                    this.messages.sendMessage(
                            player,
                            "successfully_set_tag_color",
                            clan.getName(),
                            args[1].toUpperCase()
                    )
            );
        } catch (IllegalArgumentException e) {
            this.messages.sendMessage(player, "following_colors_exist", this.formattedColors);
        }
    }

    private String formatColors() {
        final StringBuilder builder = new StringBuilder();
        for (ChatColor value : ChatColor.values()) {
            if (builder.length() != 0) {
                builder.append("§7, ");
            }
            builder.append("§").append(value.getChar()).append(value.name());
        }
        return builder.toString();
    }
}
