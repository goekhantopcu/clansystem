package eu.jailbreaker.clansystem.commands;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import eu.jailbreaker.clansystem.ClanSystem;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.google.common.reflect.ClassPath.ClassInfo;
import static com.google.common.reflect.ClassPath.from;

@Singleton
public final class ClanHelper {

    private final List<ClanCommand> commands = Lists.newArrayList();

    @Inject
    private ClanSystem plugin;

    public void loadCommands(Injector injector) {
        try {
            final ImmutableSet<ClassInfo> classes = from(this.plugin.getClass().getClassLoader())
                    .getTopLevelClasses("eu.jailbreaker.clansystem.commands.values");
            for (ClassInfo info : classes) {
                final Class<?> clazz = info.load();
                final Object instance = injector.getInstance(clazz);
                if (instance instanceof ClanCommand) {
                    this.commands.add((ClanCommand) instance);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Optional<ClanCommand> find(String name) {
        return this.commands.stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst();
    }

    private Predicate<? super ClanCommand> predicate(String input) {
        return command -> command.getName().equalsIgnoreCase(input) ||
                (
                        command.getAliases() != null &&
                                command.getAliases().stream().anyMatch(alias -> alias.equalsIgnoreCase(input))
                );
    }
}
