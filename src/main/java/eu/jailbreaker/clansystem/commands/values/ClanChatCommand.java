package eu.jailbreaker.clansystem.commands.values;

import eu.jailbreaker.clansystem.commands.ClanCommand;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.entities.ClanPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ClanChatCommand extends ClanCommand {

    public ClanChatCommand() {
        super("chat");
    }

    @Override
    public void execute(Player player, String... args) {
        if (args.length == 0) {
            this.messages.commandUsage(player, "chat <Nachricht>");
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

        final StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            if (builder.length() != 0) {
                builder.append(" ");
            }
            builder.append(arg);
        }

        final List<ClanPlayer> players = this.relationRepository.findPlayersByClan(clan).join();
        players.forEach(member -> CompletableFuture.runAsync(() ->
                this.messages.sendMessage(member.getUniqueId(), "clan_chat_format", player.getName(), builder.toString())
        ));
    }
}
