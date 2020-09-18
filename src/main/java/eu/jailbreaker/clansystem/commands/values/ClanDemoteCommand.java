package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.utils.player.PlayerUtils;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanDemoteCommand extends ClanCommand {

    @Inject
    private PlayerUtils utils;

    public ClanDemoteCommand() {
        super("demote");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.messages.commandUsage(player, "demote <Spieler>");
            return;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            this.messages.sendMessage(player, "cant_interact_self");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            this.messages.sendMessage(player, "not_in_clan");
            return;
        }

        if (clanPlayer.getRole() != ClanRole.OWNER) {
            this.messages.sendMessage(player, "not_permitted");
            return;
        }

        final UUID uniqueId = this.utils.getUniqueId(args[0]);
        if (uniqueId == null) {
            this.messages.sendMessage(player, "player_does_not_exist");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.find(uniqueId).join();
        if (targetPlayer == null) {
            this.messages.sendMessage(player, "player_does_not_exist");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (!clan.equals(targetClan)) {
            this.messages.sendMessage(player, "target_not_in_same_clan");
            return;
        }

        if (targetPlayer.getPlayerId().equals(clan.getCreator())) {
            this.messages.sendMessage(player, "cant_degradate_owner");
            return;
        }

        if (targetPlayer.getRole() == ClanRole.OWNER && !clan.getCreator().equals(clanPlayer.getPlayerId())) {
            this.messages.sendMessage(player, "cant_degradate_owner");
            return;
        }

        this.playerRepository.setRole(
                targetPlayer,
                ClanRole.USER
        ).whenComplete((unused, throwable) -> {
            this.messages.sendMessage(args[0], "self_degradated");
            this.messages.sendMessage(player, "target_degradated", args[0]);
        });
    }
}
