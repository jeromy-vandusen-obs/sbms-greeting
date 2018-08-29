package com.sbms.greeting.domain;

public class Identity {
    private final String name;
    private final String version;

    public Identity(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
