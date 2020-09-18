package eu.jailbreaker.clansystem.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClanRole {
    OWNER("§4Owner"), MODERATOR("§2Moderator"), USER("§7Nutzer");

    private final String display;
}
