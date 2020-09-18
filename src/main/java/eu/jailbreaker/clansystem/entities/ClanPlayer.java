package eu.jailbreaker.clansystem.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClanPlayer {

    private final Integer playerId;
    private final UUID uniqueId;
    private ClanRole role;
    private boolean receiveInvitations;

    public static ClanPlayer create(Integer playerId, UUID uniqueId) {
        return new ClanPlayer(playerId, uniqueId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final ClanPlayer player = (ClanPlayer) obj;
        return this.playerId.equals(player.playerId) &&
                this.uniqueId.equals(player.uniqueId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.playerId, this.uniqueId);
    }
}
