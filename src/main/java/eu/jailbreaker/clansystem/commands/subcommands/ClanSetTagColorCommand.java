package eu.jailbreaker.clansystem.commands.subcommands;

import com.google.common.base.Joiner;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ClanSetTagColorCommand extends ClanCommand {

    private final String formattedColors = this.listAllColors();

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
            this.messages.sendCommandUsage(player, "color <Name> <" + this.formattedColors + "§7>");
            return;
        }

        final String clanName = args[0];
        final Clan clan = this.clanRepository.findByName(clanName).join();
        if (clan == null) {
            this.messages.sendMessage(player, "clan_not_exist");
            return;
        }

        try {
            final String color = args[1].toUpperCase();
            this.clanRepository.setTagColor(
                    clan,
                    ChatColor.valueOf(color)
            ).whenComplete((unused, throwable) -> {
                this.messages.sendMessage(
                        player,
                        "successfully_set_tag_color",
                        clan.getName(),
                        color
                );

                final List<ClanPlayer> members = this.relationRepository.findPlayersByClan(clan).join();
                members.forEach(
                        member -> CompletableFuture.runAsync(() -> this.plugin.setClanTag(member.getUniqueId(), clan))
                );
            });
        } catch (IllegalArgumentException e) {
            this.messages.sendMessage(player, "following_colors_exist", this.formattedColors);
        }
    }

    private String listAllColors() {
        return Joiner.on("§7, ").join(
                Arrays.stream(ChatColor.values()).map(color -> color.toString() + color.name()).toArray(String[]::new)
        );
    }
}
