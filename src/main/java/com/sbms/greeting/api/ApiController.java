package com.sbms.greeting.api;

import com.sbms.greeting.ApiScanner;
import com.sbms.greeting.domain.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    private Api api;

    public ApiController(ApiScanner apiScanner) {
        this.api = new Api(apiScanner.getApi());
    }

    @GetMapping("/api")
    public Api api() {
        return api;
    }
}
