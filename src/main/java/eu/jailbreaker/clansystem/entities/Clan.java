package eu.jailbreaker.clansystem.entities;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Clan {

    private final Integer clanId;
    private final Integer creator;

    private String name;
    private String tag;

    public static Clan create(Integer clanId, Integer creator, String name, String tag) {
        return new Clan(clanId, creator, name, tag);
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
