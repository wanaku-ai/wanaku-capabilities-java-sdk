package ai.wanaku.capabilities.sdk.api.types;

import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NamespaceTest {

    @Test
    void settersAndGetters() {
        Namespace ns = new Namespace();
        ns.setId("ns-1");
        ns.setName("default");
        ns.setPath("/default");

        assertEquals("ns-1", ns.getId());
        assertEquals("default", ns.getName());
        assertEquals("/default", ns.getPath());
    }

    @Test
    void addLabel() {
        Namespace ns = new Namespace();
        ns.addLabel("env", "prod");
        assertEquals("prod", ns.getLabelValue("env"));
        assertTrue(ns.hasLabel("env"));
        assertTrue(ns.hasLabel("env", "prod"));
    }

    @Test
    void hasLabel_notPresent() {
        Namespace ns = new Namespace();
        assertFalse(ns.hasLabel("missing"));
        assertNull(ns.getLabelValue("missing"));
    }

    @Test
    void removeLabel() {
        Namespace ns = new Namespace();
        ns.addLabel("env", "prod");
        assertTrue(ns.removeLabel("env"));
        assertFalse(ns.hasLabel("env"));
    }

    @Test
    void removeLabel_notPresent() {
        Namespace ns = new Namespace();
        assertFalse(ns.removeLabel("missing"));
    }

    @Test
    void addLabels_withNull() {
        Namespace ns = new Namespace();
        ns.addLabels(null);
        assertNotNull(ns.getLabels());
        assertTrue(ns.getLabels().isEmpty());
    }

    @Test
    void addLabels_withMap() {
        Namespace ns = new Namespace();
        ns.addLabels(Map.of("a", "1", "b", "2"));
        assertEquals(2, ns.getLabels().size());
        assertEquals("1", ns.getLabelValue("a"));
    }

    @Test
    void setLabels() {
        Namespace ns = new Namespace();
        ns.setLabels(Map.of("x", "y"));
        assertEquals("y", ns.getLabelValue("x"));
    }
}
