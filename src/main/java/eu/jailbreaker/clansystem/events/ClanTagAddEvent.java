package eu.jailbreaker.clansystem.events;

import eu.jailbreaker.clansystem.entities.Clan;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
@RequiredArgsConstructor
public final class ClanTagAddEvent extends Event {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Clan clan;
    private final Player player;

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}

