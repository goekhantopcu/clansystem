package eu.jailbreaker.clansystem.commands.subcommands;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanInvite;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.utils.player.PlayerUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanInviteCommand extends ClanCommand {

    @Inject
    private PlayerUtils utils;

    public ClanInviteCommand() {
        super("invite");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.messages.sendCommandUsage(player, "invite <Spieler>");
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

        if (clanPlayer.getRole() != ClanRole.MODERATOR && clanPlayer.getRole() != ClanRole.OWNER) {
            this.messages.sendMessage(player, "not_permitted");
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
        if (clan.equals(targetClan)) {
            this.messages.sendMessage(player, "target_already_in_same_clan");
            return;
        }

        if (targetClan != null) {
            this.messages.sendMessage(player, "target_already_in_same_clan");
            return;
        }

        if (!targetPlayer.isReceiveInvitations()) {
            this.messages.sendMessage(player, "target_toggled_invitations");
            return;
        }

        final ClanInvite invite = this.inviteRepository.findInvitationByClanAndPlayer(clan, targetPlayer).join();
        if (invite != null) {
            this.messages.sendMessage(player, "already_invited");
            return;
        }

        this.inviteRepository.create(
                clan,
                targetPlayer
        ).whenComplete((unused, throwable) -> {
            this.sendReceiveInvitation(targetName, clan.getName(), clan.getDisplayName());
            this.messages.sendMessage(player, "invited_player", targetName);
        });
    }

    private void sendReceiveInvitation(String name, String clanName, String clanDisplayName) {
        final Player target = Bukkit.getPlayerExact(name);
        if (target != null) {
            final String invitation = this.messages.formatInput("received_invitation", clanDisplayName);
            final TextComponent component = new TextComponent(invitation);
            final TextComponent accept = new TextComponent(this.messages.formatInput("received_invitation_accept"));
            accept.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/clan join " + clanName
            ));
            final TextComponent deny = new TextComponent(this.messages.formatInput("received_invitation_deny"));
            deny.setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    "/clan deny " + clanName
            ));
            final String splitter = this.messages.formatInput("received_invitation_splitter");
            final TextComponent concat = new TextComponent(this.messages.formatInput("received_invitation_title"));
            concat.addExtra(accept);
            concat.addExtra(splitter);
            concat.addExtra(deny);
            target.spigot().sendMessage(component);
            target.spigot().sendMessage(concat);
        } else {
            this.messages.sendMessage(name, "received_invitation", clanDisplayName);
        }
    }
}
