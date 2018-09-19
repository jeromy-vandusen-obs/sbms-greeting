package com.sbms.greeting.api;

import com.sbms.greeting.ApiScanner;
import com.sbms.greeting.domain.Identity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IdentityController {
    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${build.version}")
    private String buildVersion;

    private int apiLevel;

    public IdentityController(ApiScanner apiScanner) {
        this.apiLevel = apiScanner.getApiLevel();
    }

    @GetMapping("/identity")
    public Identity identity() {
        return new Identity(applicationName, buildVersion, apiLevel);
    }
}
