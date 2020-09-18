package eu.jailbreaker.clansystem.entities;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClanInvite {

    private final Integer clanId;
    private final Integer inviteId;
    private final Integer invitedId;

    public static ClanInvite create(Integer clanId, Integer inviteId, Integer invitedId) {
        return new ClanInvite(clanId, inviteId, invitedId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final ClanInvite invite = (ClanInvite) obj;
        return this.clanId.equals(invite.clanId) &&
                this.inviteId.equals(invite.inviteId) &&
                this.invitedId.equals(invite.invitedId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.clanId, this.inviteId, this.invitedId);
    }
}
