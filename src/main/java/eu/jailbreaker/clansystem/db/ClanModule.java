package eu.jailbreaker.clansystem.db;

import com.google.common.io.Closer;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import eu.jailbreaker.clansystem.ClanSystem;
import eu.jailbreaker.clansystem.utils.Configuration;
import lombok.RequiredArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;
import java.sql.Connection;

@RequiredArgsConstructor
public final class ClanModule extends AbstractModule {

    private final ClanSystem plugin;

    protected void configure() {
        final Configuration messagesConfig = new Configuration(Paths.get(
                this.plugin.getDataFolder().toString(),
                "messages.json"
        ));

        final Configuration databaseConfig = new Configuration(Paths.get(
                this.plugin.getDataFolder().toString(),
                "database.json"
        ));

        messagesConfig.save();
        databaseConfig.save();

        bind(JavaPlugin.class).toInstance(this.plugin);
        bind(ClanSystem.class).toInstance(this.plugin);
        bind(Closer.class).toInstance(Closer.create());
        bind(Connection.class).toProvider(ConnectionProvider.class);

        bind(Configuration.class).annotatedWith(Names.named("messages")).toInstance(messagesConfig);
        bind(Configuration.class).annotatedWith(Names.named("database")).toInstance(databaseConfig);
    }
}
