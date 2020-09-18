package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.entity.Player;

public final class ClanRenameCommand extends ClanCommand {

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private RelationRepository relationRepository;

    public ClanRenameCommand() {
        super("rename");
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

        if (!clan.getCreator().equals(clanPlayer.getPlayerId())) {
            player.sendMessage("§cDu bist nicht der ClanInhaber");
            return;
        }

        this.clanRepository.rename(clan, args[0]).whenComplete(
                (unused, throwable) -> player.sendMessage("§7Dein neuer Clanname: §e" + args[0])
        );
    }
}
