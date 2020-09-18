package eu.jailbreaker.clansystem;

import com.google.common.collect.Maps;
import com.google.common.io.Closer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.jailbreaker.clansystem.commands.BaseClanCommand;
import eu.jailbreaker.clansystem.commands.ClanChatCommand;
import eu.jailbreaker.clansystem.commands.ClanCommandRegistry;
import eu.jailbreaker.clansystem.db.ClanModule;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.events.ClanTagAddEvent;
import eu.jailbreaker.clansystem.events.ClanTagRemoveEvent;
import eu.jailbreaker.clansystem.listener.ClanTagListener;
import eu.jailbreaker.clansystem.listener.PlayerLoginListener;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.InviteRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import lombok.Getter;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Getter
public final class ClanSystem extends JavaPlugin {

    private final Map<UUID, String> listNames = Maps.newHashMap();

    private Closer closer;

    @Override
    public void onEnable() {
        this.getDataFolder().mkdirs();

        final Injector injector = Guice.createInjector(new ClanModule(this));

        this.closer = injector.getInstance(Closer.class);

        injector.getInstance(ClanCommandRegistry.class).loadCommands(injector);

        injector.getInstance(ClanRepository.class).createTable();
        injector.getInstance(PlayerRepository.class).createTable();
        injector.getInstance(InviteRepository.class).createTable();
        injector.getInstance(RelationRepository.class).createTable();

        this.getCommand("cc").setExecutor(injector.getInstance(ClanChatCommand.class));
        this.getCommand("clan").setExecutor(injector.getInstance(BaseClanCommand.class));

        this.getServer().getPluginManager().registerEvents(
                injector.getInstance(ClanTagListener.class),
                this
        );
        this.getServer().getPluginManager().registerEvents(
                injector.getInstance(PlayerLoginListener.class),
                this
        );
    }

    @Override
    public void onDisable() {
        try {
            this.closer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void callTagRemoveEvent(UUID uniqueId) {
        final Server server = this.getServer();
        final Player player = server.getPlayer(uniqueId);
        if (player == null) {
            return;
        }

        if (server.isPrimaryThread()) {
            server.getPluginManager().callEvent(new ClanTagRemoveEvent(player));
        } else {
            server.getScheduler().runTask(
                    this, () -> server.getPluginManager().callEvent(new ClanTagRemoveEvent(player))
            );
        }
    }

    public void callTagAddEvent(UUID uniqueId, Clan clan) {
        final Server server = this.getServer();
        final Player player = server.getPlayer(uniqueId);
        if (player == null) {
            return;
        }

        if (server.isPrimaryThread()) {
            server.getPluginManager().callEvent(new ClanTagAddEvent(clan, player));
        } else {
            server.getScheduler().runTask(
                    this, () -> server.getPluginManager().callEvent(new ClanTagAddEvent(clan, player))
            );
        }
    }
}
