package com.sbms.greeting;

import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiScannerTest {
    @Test
    public void getApiLevel_always_returnsCurrentHighestApiLevel() {
        ApiScanner apiScanner = new ApiScanner();

        int result = apiScanner.getApiLevel();

        assertThat(result).isEqualTo(1);
    }

    @Test
    public void getApi_always_returnsApiGroupedByApiLevel() {
        ApiScanner apiScanner = new ApiScanner();

        List<String> result = apiScanner.getApi();

        assertThat(result).isNotNull().isNotEmpty().containsExactly(
                "/api (GET)",
                "/v1/identity (GET)",
                "/v1/messages (GET)",
                "/v1/messages/{language} (DELETE)",
                "/v1/messages/{language} (GET)",
                "/v1/messages/{language} (PUT)");
    }
}
