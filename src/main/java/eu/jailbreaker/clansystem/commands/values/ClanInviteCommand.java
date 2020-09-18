package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanInvite;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ClanInviteCommand extends ClanCommand {

    public ClanInviteCommand() {
        super("invite");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan invite <Spieler>");
            return;
        }

        if (args[0].equalsIgnoreCase(player.getName())) {
            this.utils.sendMessage(player, "§cDu darfst nicht mit dir selbst interagieren!");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.utils.sendMessage(player, "§cEin Fehler ist aufgetreten!");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            this.utils.sendMessage(player, "§cDu bist in keinem Clan!");
            return;
        }

        if (clanPlayer.getRole() != ClanRole.MODERATOR && clanPlayer.getRole() != ClanRole.OWNER) {
            this.utils.sendMessage(player, "§cDu hast keine Rechte um jemanden in den Clan einzuladen!");
            return;
        }

        final UUID uniqueId = this.utils.getUniqueId(args[0]);
        if (uniqueId == null) {
            this.utils.sendMessage(player, "§cDieser Spieler existiert nicht!");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.find(uniqueId).join();
        if (targetPlayer == null) {
            this.utils.sendMessage(player, "§cDieser Spieler existiert nicht!");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (clan.equals(targetClan)) {
            this.utils.sendMessage(player, "§cDieser Spieler ist bereits in deinem Clan!");
            return;
        }

        if (targetClan != null) {
            this.utils.sendMessage(player, "§cDieser Spieler ist bereits in einem Clan!");
            return;
        }

        if (!targetPlayer.isReceiveInvitations()) {
            this.utils.sendMessage(player, "§cDieser Spieler hat seine Anfragen deaktiviert");
            return;
        }

        final ClanInvite invite = this.inviteRepository.findInvitationByClanAndPlayer(clan, targetPlayer).join();
        if (invite != null) {
            this.utils.sendMessage(player, "§cDu hast diesen Spieler bereits eingeladen!");
            return;
        }

        this.inviteRepository.create(clan, targetPlayer).whenComplete((unused, throwable) -> {
            this.utils.sendMessage(args[0], "§7Du wurdest in den Clan §e" + clan.getName() + " §7eingeladen!");
            this.utils.sendMessage(player, "§7Du hast den Spieler §e" + args[0] + " §7in deinen Clan eingeladen!");
        });
    }
}
