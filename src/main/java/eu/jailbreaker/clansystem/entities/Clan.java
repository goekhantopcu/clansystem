package eu.jailbreaker.clansystem.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Clan {

    private final Integer clanId;
    private final Integer creator;
    private final Timestamp timestamp;

    private String name;
    private String tag;
    private ChatColor color;

    public static Clan create(Integer clanId, Integer creator, Timestamp timestamp, String name, String tag, ChatColor color) {
        return new Clan(clanId, creator, timestamp, name, tag, color);
    }

    public String getDisplayTag() {
        if (this.name.equalsIgnoreCase("team")) {
            return ChatColor.RED + this.name;
        }
        return this.color.toString() + this.tag;
    }

    public String getDisplayName() {
        return this.color.toString() + this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final Clan clan = (Clan) obj;
        return this.clanId.equals(clan.clanId) && this.creator.equals(clan.creator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clanId, this.creator);
    }
}
