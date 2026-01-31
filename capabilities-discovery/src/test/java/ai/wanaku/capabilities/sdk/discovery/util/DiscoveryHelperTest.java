package ai.wanaku.capabilities.sdk.discovery.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DiscoveryHelperTest {

    @Test
    void resolveRegistrationAddressReturnsExplicitAddress() {
        String explicitAddress = "192.168.1.100";

        String result = DiscoveryHelper.resolveRegistrationAddress(explicitAddress);

        assertEquals(explicitAddress, result);
    }

    @Test
    void resolveRegistrationAddressReturnsHostname() {
        String hostname = "my-service.example.com";

        String result = DiscoveryHelper.resolveRegistrationAddress(hostname);

        assertEquals(hostname, result);
    }

    @Test
    void resolveRegistrationAddressResolvesAutoKeyword() {
        String result = DiscoveryHelper.resolveRegistrationAddress("auto");

        assertNotNull(result);
        assertNotEquals("auto", result);
        // Should return a non-empty address (could be IP or hostname depending on environment)
        assertFalse(result.isEmpty(), "Expected non-empty address");
    }

    @Test
    void resolveRegistrationAddressIsCaseSensitiveForAuto() {
        // "AUTO" (uppercase) should not trigger auto-resolution
        String result = DiscoveryHelper.resolveRegistrationAddress("AUTO");

        assertEquals("AUTO", result);
    }

    @Test
    void resolveRegistrationAddressPassesThroughNull() {
        String result = DiscoveryHelper.resolveRegistrationAddress(null);

        assertNull(result);
    }

    @Test
    void resolveRegistrationAddressPassesThroughEmptyString() {
        String result = DiscoveryHelper.resolveRegistrationAddress("");

        assertEquals("", result);
    }

    @Test
    void resolveRegistrationAddressNoArgReturnsValidIp() {
        String result = DiscoveryHelper.resolveRegistrationAddress();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void resolveRegistrationAddressReturnsNonEmptyValue() {
        String result = DiscoveryHelper.resolveRegistrationAddress();

        // The result could be an IP address or hostname depending on the environment
        assertNotNull(result);
        assertFalse(result.isEmpty(), "Expected non-empty address");
    }
}
