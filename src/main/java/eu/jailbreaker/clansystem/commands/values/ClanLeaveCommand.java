package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.entity.Player;

import java.util.List;

public final class ClanLeaveCommand extends ClanCommand {

    public ClanLeaveCommand() {
        super("leave");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 0) {
            this.utils.sendMessage(player, "Verwende: /clan leave");
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

        if (clanPlayer.getRole() == ClanRole.OWNER && clan.getCreator().equals(clanPlayer.getPlayerId())) {
            this.utils.sendMessage(player, "§cDu kannst deinen Clan nicht verlassen, du musst ihn mit /clan delete löschen!");
            return;
        }

        this.relationRepository.delete(clan, clanPlayer).whenCompleteAsync((unused, throwable) -> {
            final List<ClanPlayer> players = this.relationRepository.findPlayersByClan(clan).join();
            players.forEach(member ->
                    this.utils.sendMessage(member.getUniqueId(), "§c" + player.getName() + " hat den Clan verlassen!")
            );
            this.utils.sendMessage(player, "§cDu hast den Clan verlassen!");

            this.plugin.callTagEvent(player);
        });
    }
}
