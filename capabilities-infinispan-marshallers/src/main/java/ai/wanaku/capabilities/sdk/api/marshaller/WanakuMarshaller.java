package ai.wanaku.capabilities.sdk.api.marshaller;

/**
 * Marker interface for Wanaku Infinispan marshaller implementations.
 * <p>
 * This interface serves as a type identifier for marshaller classes that
 * integrate with Infinispan's ProtoStream. It does not define any methods;
 * implementations should also implement {@link org.infinispan.protostream.MessageMarshaller}.
 * </p>
 */
public interface WanakuMarshaller {
    // marker interface, no methods needed
}
