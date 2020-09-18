package eu.jailbreaker.clansystem.commands.values;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import org.bukkit.entity.Player;

public final class ClanCreateCommand extends ClanCommand {

    @Inject
    private ClanRepository clanRepository;

    @Inject
    private PlayerRepository playerRepository;

    @Inject
    private RelationRepository relationRepository;

    public ClanCreateCommand() {
        super("create");
    }

    @Override
    public void execute(Player player, String... args) {
        try {
            final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
            if (clanPlayer == null) {
                player.sendMessage("§cEin Fehler ist aufgetreten!");
                return;
            }

            Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
            if (clan != null) {
                player.sendMessage("§cDu hast bereits einen Clan [" + clan.getName() + "]");
                return;
            }

            final String tag = args[1];
            final String name = args[0];

            clan = this.clanRepository.create(clanPlayer, name, tag).join();
            if (clan == null) {
                player.sendMessage("§cEin Fehler ist aufgetreten, konnte keinen Clan erstellen!");
                return;
            }

            this.playerRepository.setClan(clanPlayer, clan, ClanRole.OWNER);

            player.sendMessage("§7Du hast einen Clan erstellt");
            player.sendMessage("§7Name: §e" + clan.getName());
            player.sendMessage("§7Tag: §b" + clan.getTag());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
