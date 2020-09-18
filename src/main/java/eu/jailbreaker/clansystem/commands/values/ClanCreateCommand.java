package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import org.bukkit.entity.Player;

public final class ClanCreateCommand extends ClanCommand {

    public ClanCreateCommand() {
        super("create");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 2) {
            this.utils.sendMessage(player, "Verwende: /clan create <Name> <Tag>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.utils.sendMessage(player, "§cEin Fehler ist aufgetreten!");
            return;
        }

        Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan != null) {
            this.utils.sendMessage(player, "§cDu hast bereits einen Clan [" + clan.getName() + "]");
            return;
        }

        final String tag = args[1];
        final String name = args[0];

        clan = this.clanRepository.create(clanPlayer, name, tag).join();
        if (clan == null) {
            this.utils.sendMessage(player, "§cEin Fehler ist aufgetreten, konnte keinen Clan erstellen!");
            return;
        }

        this.playerRepository.setClan(clanPlayer, clan, ClanRole.OWNER);

        this.utils.sendMessage(player, "§7Du hast einen Clan erstellt");
        this.utils.sendMessage(player, "§7Name: §e" + clan.getName());
        this.utils.sendMessage(player, "§7Tag: §b" + clan.getTag());

        this.plugin.callTagEvent(player, clan);
    }
}
