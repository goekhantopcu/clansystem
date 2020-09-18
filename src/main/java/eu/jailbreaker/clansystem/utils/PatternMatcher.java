package eu.jailbreaker.clansystem.utils;

import java.util.regex.Pattern;

public enum PatternMatcher {
    TAG("^[a-zA-Z0-9_]{2,5}$"),
    NAME("^[a-zA-Z0-9_]{2,16}$");

    private final Pattern pattern;

    PatternMatcher(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public boolean matches(String input) {
        if (input.isEmpty()) {
            return false;
        }
        return this.pattern.matcher(input).find();
    }
}