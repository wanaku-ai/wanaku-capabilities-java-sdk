package ai.wanaku.capabilities.sdk.api.types.providers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ServiceTargetTest {

    @Test
    void newEmptyTarget_withSubType() {
        ServiceTarget target = ServiceTarget.newEmptyTarget(
                "myService", "localhost", 9190, "tool-invoker", "jvm", "Java", "compiled", "jvm");
        assertNull(target.getId());
        assertEquals("myService", target.getServiceName());
        assertEquals("localhost", target.getHost());
        assertEquals(9190, target.getPort());
        assertEquals("tool-invoker", target.getServiceType());
        assertEquals("jvm", target.getServiceSubType());
        assertEquals("Java", target.getLanguageName());
        assertEquals("compiled", target.getLanguageType());
        assertEquals("jvm", target.getLanguageSubType());
    }

    @Test
    void newEmptyTarget_minimal() {
        ServiceTarget target = ServiceTarget.newEmptyTarget("svc", "host", 8080, "resource-provider");
        assertNull(target.getId());
        assertEquals("svc", target.getServiceName());
        assertNull(target.getServiceSubType());
        assertNull(target.getLanguageName());
    }

    @Test
    void toAddress() {
        ServiceTarget target = ServiceTarget.newEmptyTarget("svc", "192.168.1.1", 9090, "tool-invoker");
        assertEquals("192.168.1.1:9090", target.toAddress());
    }

    @Test
    void setId() {
        ServiceTarget target = new ServiceTarget();
        target.setId("id-1");
        assertEquals("id-1", target.getId());
    }

    @Test
    void equalsAndHashCode() {
        ServiceTarget a = ServiceTarget.newEmptyTarget("svc", "host", 8080, "tool-invoker");
        ServiceTarget b = ServiceTarget.newEmptyTarget("svc", "host", 8080, "tool-invoker");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentPort() {
        ServiceTarget a = ServiceTarget.newEmptyTarget("svc", "host", 8080, "tool-invoker");
        ServiceTarget b = ServiceTarget.newEmptyTarget("svc", "host", 9090, "tool-invoker");
        assertNotEquals(a, b);
    }
}
