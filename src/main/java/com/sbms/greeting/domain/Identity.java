package com.sbms.greeting.domain;

public class Identity {
    private final String name;
    private final String version;
    private final int apiLevel;

    public Identity(String name, String version, int apiLevel) {
        this.name = name;
        this.version = version;
        this.apiLevel = apiLevel;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public int getApiLevel() {
        return apiLevel;
    }
}
