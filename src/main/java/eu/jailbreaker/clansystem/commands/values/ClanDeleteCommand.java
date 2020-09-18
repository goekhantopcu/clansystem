package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ClanDeleteCommand extends ClanCommand {

    public ClanDeleteCommand() {
        super("delete");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 0) {
            this.utils.sendMessage(player, "Verwende: /clan delete");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.utils.sendMessage(player, "§cEin Fehler ist aufgetreten!");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            this.utils.sendMessage(player, "§cDu bist in keinem Clan");
            return;
        }

        if (clanPlayer.getRole() != ClanRole.OWNER && !clan.getCreator().equals(clanPlayer.getPlayerId())) {
            this.utils.sendMessage(player, "§cNur der Claninhaber kann den Clan löschen!");
            return;
        }

        final List<ClanPlayer> clanPlayers = this.relationRepository.findPlayersByClan(clan).join();
        this.clanRepository.delete(clan).whenComplete((unused, throwable) -> clanPlayers.forEach(member -> CompletableFuture.runAsync(() -> {
            this.utils.sendMessage(member.getUniqueId(), "§cDein Clan hat sich aufgelöst!");
            this.playerRepository.setRole(member, ClanRole.USER);
            this.plugin.callTagEvent(member.getUniqueId());
        })));
    }
}
