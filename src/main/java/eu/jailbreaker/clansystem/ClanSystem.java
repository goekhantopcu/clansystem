package eu.jailbreaker.clansystem;

import com.google.common.io.Closer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import eu.jailbreaker.clansystem.commands.BaseClanCommand;
import eu.jailbreaker.clansystem.commands.ClanHelper;
import eu.jailbreaker.clansystem.db.ClanModule;
import eu.jailbreaker.clansystem.listener.PlayerLoginListener;
import eu.jailbreaker.clansystem.repositories.ClanRepository;
import eu.jailbreaker.clansystem.repositories.InviteRepository;
import eu.jailbreaker.clansystem.repositories.PlayerRepository;
import eu.jailbreaker.clansystem.repositories.RelationRepository;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

@Getter
public final class ClanSystem extends JavaPlugin {

    private Closer closer;

    @Override
    public void onEnable() {
        this.getDataFolder().mkdirs();

        final Injector injector = Guice.createInjector(new ClanModule(this));

        this.closer = injector.getInstance(Closer.class);
        injector.getInstance(ClanHelper.class).loadCommands(injector);

        injector.getInstance(ClanRepository.class).createTable();
        injector.getInstance(PlayerRepository.class).createTable();
        injector.getInstance(RelationRepository.class).createTable();
        injector.getInstance(InviteRepository.class).createTable();

        this.getCommand("clan").setExecutor(injector.getInstance(BaseClanCommand.class));

        this.getServer().getPluginManager().registerEvents(injector.getInstance(PlayerLoginListener.class), this);
    }

    @Override
    public void onDisable() {
        try {
            this.closer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
