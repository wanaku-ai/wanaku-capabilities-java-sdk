package ai.wanaku.capabilities.sdk.api.types;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ResourceReferenceTest {

    @Test
    void defaultConstructor() {
        ResourceReference ref = new ResourceReference();
        assertNull(ref.getId());
        assertNull(ref.getLocation());
        assertNull(ref.getType());
        assertNull(ref.getName());
        assertNull(ref.getDescription());
        assertNull(ref.getMimeType());
        assertNull(ref.getParams());
        assertNull(ref.getConfigurationURI());
        assertNull(ref.getSecretsURI());
    }

    @Test
    void settersAndGetters() {
        ResourceReference ref = new ResourceReference();
        ref.setId("res-1");
        ref.setLocation("http://data");
        ref.setType("file");
        ref.setName("myResource");
        ref.setDescription("A resource");
        ref.setMimeType("text/plain");
        ref.setNamespace("default");
        ref.setConfigurationURI("file:///config");
        ref.setSecretsURI("file:///secret");

        assertEquals("res-1", ref.getId());
        assertEquals("http://data", ref.getLocation());
        assertEquals("file", ref.getType());
        assertEquals("myResource", ref.getName());
        assertEquals("A resource", ref.getDescription());
        assertEquals("text/plain", ref.getMimeType());
        assertEquals("default", ref.getNamespace());
    }

    @Test
    void paramNestedClass() {
        ResourceReference.Param param = new ResourceReference.Param();
        param.setName("key");
        param.setValue("value");

        assertEquals("key", param.getName());
        assertEquals("value", param.getValue());
    }

    @Test
    void paramEqualsAndHashCode() {
        ResourceReference.Param a = new ResourceReference.Param();
        a.setName("key");
        a.setValue("value");

        ResourceReference.Param b = new ResourceReference.Param();
        b.setName("key");
        b.setValue("value");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void setParams() {
        ResourceReference ref = new ResourceReference();
        ResourceReference.Param param = new ResourceReference.Param();
        param.setName("k");
        param.setValue("v");
        ref.setParams(List.of(param));

        assertEquals(1, ref.getParams().size());
        assertEquals("k", ref.getParams().get(0).getName());
    }

    @Test
    void equalsAndHashCode() {
        ResourceReference a = new ResourceReference();
        a.setId("1");
        a.setName("res");
        a.setLocation("loc");

        ResourceReference b = new ResourceReference();
        b.setId("1");
        b.setName("res");
        b.setLocation("loc");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void notEquals_differentId() {
        ResourceReference a = new ResourceReference();
        a.setId("1");

        ResourceReference b = new ResourceReference();
        b.setId("2");

        assertNotEquals(a, b);
    }
}
