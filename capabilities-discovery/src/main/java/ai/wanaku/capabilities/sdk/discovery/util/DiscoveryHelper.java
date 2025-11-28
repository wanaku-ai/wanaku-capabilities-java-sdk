package ai.wanaku.capabilities.sdk.discovery.util;

import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utility class providing helper methods related to service discovery, particularly for resolving network addresses.
 */
public final class DiscoveryHelper {

    private DiscoveryHelper() {}

    /**
     * Resolves and returns the local host's IP address that will be used for service registration.
     *
     * @return The IP address of the local host as a string.
     * @throws WanakuException If the local host address cannot be determined.
     */
    public static String resolveRegistrationAddress() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            throw new WanakuException(e);
        }
    }

    /**
     * Resolves the address that will be registered in the service registry.
     * If the provided address is "auto", it resolves the local host's IP address.
     * Otherwise, it returns the provided address.
     *
     * @param address The address to use for registration, or "auto" for automatic resolution.
     * @return The resolved address as a string.
     * @throws WanakuException If "auto" is specified and the local host address cannot be determined.
     */
    public static String resolveRegistrationAddress(String address) {
        if ("auto".equals(address)) {
            address = resolveRegistrationAddress();
        }
        return address;
    }
}
