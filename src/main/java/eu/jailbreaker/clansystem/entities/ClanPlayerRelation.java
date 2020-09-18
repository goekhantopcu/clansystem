package eu.jailbreaker.clansystem.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@RequiredArgsConstructor
public final class ClanPlayerRelation {

    private Integer clanId;
    private Integer playerId;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final ClanPlayerRelation relation = (ClanPlayerRelation) obj;
        return this.clanId.equals(relation.clanId) &&
                this.playerId.equals(relation.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clanId, this.playerId);
    }
}
