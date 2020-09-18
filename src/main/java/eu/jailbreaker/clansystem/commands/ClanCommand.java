package eu.jailbreaker.clansystem.commands;

import com.google.inject.Inject;
import eu.jailbreaker.clansystem.ClanSystem;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.InviteRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import eu.jailbreaker.clansystem.utils.Messages;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public abstract class ClanCommand {

    @Getter
    private final String name;

    @Inject
    protected ClanSystem plugin;

    @Inject
    protected ClanRepository clanRepository;

    @Inject
    protected PlayerRepository playerRepository;

    @Inject
    protected RelationRepository relationRepository;

    @Inject
    protected InviteRepository inviteRepository;

    @Inject
    protected Messages messages;

    public abstract void execute(Player player, String... args);

}
