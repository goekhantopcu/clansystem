package eu.jailbreaker.clansystem.commands.subcommands;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.utils.player.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanJumpCommand extends ClanCommand {

    @Inject
    private PlayerUtils utils;

    public ClanJumpCommand() {
        super("jump");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.messages.sendCommandUsage(player, "jump <Spieler>");
            return;
        }

        final String targetName = args[0];
        if (targetName.equalsIgnoreCase(player.getName())) {
            this.messages.sendMessage(player, "cant_interact_self");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.findByUniqueId(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            this.messages.sendMessage(player, "not_in_clan");
            return;
        }

        final UUID uniqueId = this.utils.getUniqueId(targetName);
        if (uniqueId == null) {
            this.messages.sendMessage(player, "player_does_not_exist");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.findByUniqueId(uniqueId).join();
        if (targetPlayer == null) {
            this.messages.sendMessage(player, "player_does_not_exist");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (!clan.equals(targetClan)) {
            this.messages.sendMessage(player, "both_not_in_same_clan");
            return;
        }

        this.messages.sendMessage(player, "connecting_to", targetName);
        this.utils.connect(player.getUniqueId(), uniqueId);
    }
}
