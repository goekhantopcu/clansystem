package eu.jailbreaker.clansystem.commands;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import eu.jailbreaker.clansystem.utils.Configuration;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public abstract class ClanCommand {

    @Getter
    private final String name;

    @Getter
    private final List<String> aliases;

    @Getter
    private final String permission;

    @Inject
    @Named("messages")
    protected Configuration messages;

    public ClanCommand(String name) {
        this(name, Collections.emptyList(), null);
    }

    public ClanCommand(String name, List<String> aliases) {
        this(name, aliases, null);
    }

    public ClanCommand(String name, String permission) {
        this(name, Collections.emptyList(), permission);
    }

    public abstract void execute(Player player, String... args);

}
