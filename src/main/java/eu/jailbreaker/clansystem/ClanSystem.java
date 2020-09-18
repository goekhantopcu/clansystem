package eu.jailbreaker.clansystem;

import com.google.common.collect.Maps;
import com.google.common.io.Closer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.jailbreaker.clansystem.commands.BaseClanCommand;
import eu.jailbreaker.clansystem.commands.ClanHelper;
import eu.jailbreaker.clansystem.db.ClanModule;
import eu.jailbreaker.clansystem.entities.Clan;
import eu.jailbreaker.clansystem.events.ClanSetTagEvent;
import eu.jailbreaker.clansystem.listener.ClanSetTagListener;
import eu.jailbreaker.clansystem.listener.PlayerLoginListener;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.InviteRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Getter
public final class ClanSystem extends JavaPlugin {

    private Closer closer;

    private final Map<UUID, String> listNames = Maps.newHashMap();
    private Injector injector;

    @Override
    public void onEnable() {
        this.getDataFolder().mkdirs();

        this.injector = Guice.createInjector(new ClanModule(this));

        this.closer = this.injector.getInstance(Closer.class);
        this.injector.getInstance(ClanHelper.class).loadCommands(this.injector);

        this.injector.getInstance(ClanRepository.class).createTable();
        this.injector.getInstance(PlayerRepository.class).createTable();
        this.injector.getInstance(InviteRepository.class).createTable();
        this.injector.getInstance(RelationRepository.class).createTable();

        this.getCommand("clan").setExecutor(this.injector.getInstance(BaseClanCommand.class));

        this.getServer().getPluginManager().registerEvents(
                this.injector.getInstance(ClanSetTagListener.class),
                this
        );
        this.getServer().getPluginManager().registerEvents(
                this.injector.getInstance(PlayerLoginListener.class),
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

    public void callTagEvent(Player player) {
        this.callTagEvent(player, null);
    }

    public void callTagEvent(Player player, Clan clan) {
        if (this.getServer().isPrimaryThread()) {
            this.getServer().getPluginManager().callEvent(new ClanSetTagEvent(clan, player));
        } else {
            this.getServer().getScheduler().runTask(
                    this, () -> this.getServer().getPluginManager().callEvent(new ClanSetTagEvent(clan, player))
            );
        }
    }
}
