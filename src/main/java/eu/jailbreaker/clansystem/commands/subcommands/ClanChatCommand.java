package eu.jailbreaker.clansystem.commands.subcommands;

import com.google.common.base.Joiner;
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
            this.messages.sendCommandUsage(player, "chat <Nachricht>");
            return;
        }

        final ClanPlayer clanPlayer = this.playerRepository.findByUniqueId(player.getUniqueId()).join();
        if (clanPlayer == null) {
            this.messages.sendMessage(player, "error_occured");
            return;
        }

        final Clan clan = this.relationRepository.findClanByPlayer(clanPlayer).join();
        if (clan == null) {
            this.messages.sendMessage(player, "not_in_clan");
            return;
        }

        final List<ClanPlayer> players = this.relationRepository.findPlayersByClan(clan).join();
        players.forEach(member -> CompletableFuture.runAsync(() ->
                this.messages.sendMessage(
                        member.getUniqueId(),
                        "clan_chat_format",
                        player.getName(),
                        Joiner.on(" ").join(args)
                )
        ));
    }
}
