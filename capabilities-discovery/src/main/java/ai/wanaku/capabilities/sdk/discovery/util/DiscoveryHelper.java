package ai.wanaku.capabilities.sdk.discovery.util;

import ai.wanaku.api.exceptions.WanakuException;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Utilities to help with target discovery
 */
public final class DiscoveryHelper {

    private DiscoveryHelper() {}

    /**
     * Resolve the address that will be registered in the service registry
     * @return the address as a string
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
     * Resolve the address that will be registered in the service registry
     * @param address The address to use or "auto" for auto resolution
     * @return the address as a string
     */
    public static String resolveRegistrationAddress(String address) {
        if ("auto".equals(address)) {
            address = resolveRegistrationAddress();
        }
        return address;
    }
}
