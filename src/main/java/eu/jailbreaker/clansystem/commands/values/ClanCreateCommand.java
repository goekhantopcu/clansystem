package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import eu.jailbreaker.clansystem.entities.ClanRole;
import eu.jailbreaker.clansystem.utils.PatternMatcher;
import org.bukkit.entity.Player;

public final class ClanCreateCommand extends ClanCommand {

    public ClanCreateCommand() {
        super("create");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length != 2) {
            this.messages.commandUsage(player, "create <Name> <Tag>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.find(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan != null) {
            this.messages.sendMessage(player, "already_have_clan", clan.getDisplayName());
            return;
        }

        final String tag = args[1];
        final String name = args[0];

        if (!PatternMatcher.TAG.matches(tag)) {
            this.messages.sendMessage(player, "invalid_tag");
            return;
        }

        if (!PatternMatcher.NAME.matches(name)) {
            this.messages.sendMessage(player, "invalid_name");
            return;
        }

        clan = this.clanRepository.create(clanPlayer, name, tag).join();
        if (clan == null) {
            this.messages.sendMessage(player, "cant_create_clan");
            return;
        }

        this.playerRepository.setClan(clanPlayer, clan, ClanRole.OWNER);
        this.messages.sendMessage(player, "clan_created", clan.getDisplayName(), clan.getDisplayTag());
        this.plugin.callTagEvent(player, clan);
    }
}
