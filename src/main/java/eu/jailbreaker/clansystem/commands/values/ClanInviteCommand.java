package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanInvite;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.InviteRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ClanInviteCommand extends ClanCommand {

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private RelationRepository relationRepository;

    @Inject
    private InviteRepository inviteRepository;

    public ClanInviteCommand() {
        super("invite");
    }

    @Override
    public void execute(Player player, String... args) {
        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            player.sendMessage("§cEin Fehler ist aufgetreten!");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            player.sendMessage("§cDu bist in keinem Clan!");
            return;
        }

        if (clanPlayer.getRole() != ClanRole.MODERATOR && clanPlayer.getRole() != ClanRole.OWNER) {
            player.sendMessage("§cDu hast keine Rechte um jemanden in den Clan einzuladen!");
            return;
        }

        final ClanPlayer targetPlayer = this.playerRepository.find(Bukkit.getPlayerExact(args[0]).getUniqueId()).join();
        if (targetPlayer == null) {
            player.sendMessage("§cDieser Spieler existiert nicht!");
            return;
        }

        final Clan targetClan = this.relationRepository.findClanByPlayer(targetPlayer).join();
        if (clan.equals(targetClan)) {
            player.sendMessage("§cDieser Spieler ist bereits in deinem Clan!");
            return;
        }

        if (targetClan != null) {
            player.sendMessage("§cDieser Spieler ist bereits in einem Clan!");
            return;
        }

        if (!targetPlayer.isReceiveInvitations()) {
            player.sendMessage("§cDieser Spieler hat seine Anfragen deaktiviert");
            return;
        }

        final ClanInvite invite = this.inviteRepository.findInvitationByClanAndPlayer(clan, targetPlayer).join();
        if (invite != null) {
            player.sendMessage("§cDu hast diesen Spieler bereits eingeladen!");
            return;
        }

        this.inviteRepository.create(clan, targetPlayer).whenComplete((unused, throwable) -> {
            Bukkit.getPlayerExact(args[0]).sendMessage("§7Du wurdest in den Clan §e" + clan.getName() + " §7eingeladen!");
            player.sendMessage("§7Du hast den Spieler §e" + args[0] + " §7in deinen Clan eingeladen!");
        });
    }
}
