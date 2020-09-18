package eu.jailbreaker.clansystem.db;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import eu.jailbreaker.clansystem.ClanSystem;
import eu.jailbreaker.clansystem.utils.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

@Singleton
public final class ConnectionProvider implements Provider<Connection> {

    @Inject
    private ClanSystem plugin;

    @Inject
    @Named("database")
    private Configuration configuration;

    public Connection get() {
        try {
            return DriverManager.getConnection(
                    this.configuration.getString("url"),
                    this.configuration.getString("username"),
                    this.configuration.getString("password")
            );
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
