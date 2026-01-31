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
        // Should return an IP address format
        assertTrue(result.matches("^[0-9.]+$") || result.contains(":"),
            "Expected IP address format but got: " + result);
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
    void resolveRegistrationAddressReturnsIpv4OrIpv6Format() {
        String result = DiscoveryHelper.resolveRegistrationAddress();

        // IPv4: digits and dots, IPv6: contains colons
        boolean isIpv4 = result.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
        boolean isIpv6 = result.contains(":");

        assertTrue(isIpv4 || isIpv6, "Expected valid IP format but got: " + result);
    }
}
