package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.entity.Player;

import java.util.List;

public final class ClanDeleteCommand extends ClanCommand {

    public ClanDeleteCommand() {
        super("delete");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 0) {
            this.messages.commandUsage(player, "delete");
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

        if (clanPlayer.getRole() != ClanRole.OWNER && !clan.getCreator().equals(clanPlayer.getPlayerId())) {
            this.messages.sendMessage(player, "only_owner_can_delete");
            return;
        }

        final List<ClanPlayer> clanPlayers = this.relationRepository.findPlayersByClan(clan).join();
        clanPlayers.forEach(member -> {
            this.messages.sendMessage(member.getUniqueId(), "clan_deleted");
            this.playerRepository.setRole(member, ClanRole.USER);
            this.plugin.callTagEvent(member.getUniqueId());
        });
        this.clanRepository.delete(clan);
    }
}
