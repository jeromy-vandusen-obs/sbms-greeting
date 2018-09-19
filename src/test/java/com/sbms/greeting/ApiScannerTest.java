package com.sbms.greeting;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ApiScannerTest {
    @Test
    public void getApiLevel_always_returnsCurrentHighestApiLevel() {
        ApiScanner apiScanner = new ApiScanner();

        int result = apiScanner.getApiLevel();

        assertThat(result).isEqualTo(1);
    }
}
