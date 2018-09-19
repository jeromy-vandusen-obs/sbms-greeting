package com.sbms.greeting.domain;

import java.util.List;

public class Api {
    private final List<String> methods;

    public Api(List<String> methods) {
        this.methods = methods;
    }

    public List<String> getMethods() {
        return methods;
    }
}
