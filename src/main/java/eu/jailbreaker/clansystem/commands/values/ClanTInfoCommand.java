package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import org.bukkit.entity.Player;

public final class ClanTInfoCommand extends ClanCommand {

    public ClanTInfoCommand() {
        super("tinfo");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 1) {
            this.utils.sendMessage(player, "Verwende: /clan tinfo <Clan-Tag>");
            return;
        }

        final Clan clan = this.clanRepository.findByTag(args[0]).join();
        if (clan == null) {
            this.utils.sendMessage(player, "§cDieser Clan existiert nicht!");
            return;
        }

        this.utils.sendMessage(player, "Clanname: " + clan.getName());
        this.utils.sendMessage(player, "Tag: §7[§f" + clan.getTag() + "§7]");
        this.utils.sendMessage(player, "Ersteller: §e" + this.playerRepository.find(clan.getCreator()).join().getUniqueId());
        this.utils.sendMessage(player, "Erstellt am: " + clan.getTimestamp().toString());
    }
}
