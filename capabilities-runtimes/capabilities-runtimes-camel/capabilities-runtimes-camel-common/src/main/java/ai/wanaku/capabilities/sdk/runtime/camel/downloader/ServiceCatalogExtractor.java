package ai.wanaku.capabilities.sdk.runtime.camel.downloader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ai.wanaku.capabilities.sdk.api.exceptions.WanakuException;

/**
 * Extracts files from a service catalog ZIP archive for a given system.
 * <p>
 * The ZIP must contain an {@code index.properties} file with entries like:
 * <pre>
 * catalog.name=my-catalog
 * catalog.services=system1,system2
 * catalog.routes.system1=system1/routes.camel.yaml
 * catalog.rules.system1=system1/rules.wanaku-rules.yaml
 * catalog.dependencies.system1=system1/dependencies.txt
 * </pre>
 */
public final class ServiceCatalogExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(ServiceCatalogExtractor.class);

    private static final String INDEX_FILE = "index.properties";
    private static final String PROP_ROUTES_PREFIX = "catalog.routes.";
    private static final String PROP_RULES_PREFIX = "catalog.rules.";
    private static final String PROP_DEPENDENCIES_PREFIX = "catalog.dependencies.";
    private static final String PROP_PROPERTIES_PREFIX = "catalog.properties.";

    private ServiceCatalogExtractor() {}

    /**
     * Extracts all files from a Base64-encoded service catalog ZIP archive,
     * then maps the known resource types for the given system.
     *
     * @param base64Data the Base64-encoded ZIP data
     * @param system     the system name to resolve resource references for
     * @param dataDir    the directory where extracted files will be written
     * @return a map of resource types to their extracted file paths
     * @throws WanakuException if the ZIP is invalid or required files are missing
     */
    public static Map<ResourceType, Path> extract(String base64Data, String system, Path dataDir)
            throws WanakuException {
        byte[] zipBytes;
        try {
            zipBytes = Base64.getDecoder().decode(base64Data);
        } catch (IllegalArgumentException e) {
            throw new WanakuException("Invalid Base64 data: " + e.getMessage(), e);
        }

        Properties props = readIndex(zipBytes);
        extractAll(zipBytes, dataDir);

        Map<ResourceType, Path> result = new HashMap<>();

        String routesEntry = requireProperty(props, PROP_ROUTES_PREFIX + system, system);
        result.put(ResourceType.ROUTES_REF, dataDir.resolve(routesEntry));

        String rulesEntry = requireProperty(props, PROP_RULES_PREFIX + system, system);
        result.put(ResourceType.RULES_REF, dataDir.resolve(rulesEntry));

        String depsEntry = props.getProperty(PROP_DEPENDENCIES_PREFIX + system);
        if (depsEntry != null && !depsEntry.isBlank()) {
            result.put(ResourceType.DEPENDENCY_REF, dataDir.resolve(depsEntry.trim()));
        }

        String propsEntry = props.getProperty(PROP_PROPERTIES_PREFIX + system);
        if (propsEntry != null && !propsEntry.isBlank()) {
            result.put(ResourceType.PROPERTIES_REF, dataDir.resolve(propsEntry.trim()));
        } else {
            Path routesDir = dataDir.resolve(routesEntry).getParent();
            if (routesDir != null) {
                Path conventional = routesDir.resolve("service.properties");
                if (Files.exists(conventional)) {
                    result.put(ResourceType.PROPERTIES_REF, conventional);
                }
            }
        }

        return result;
    }

    private static Properties readIndex(byte[] zipBytes) throws WanakuException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (INDEX_FILE.equals(entry.getName())) {
                    Properties props = new Properties();
                    props.load(zis);
                    return props;
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            throw new WanakuException("Failed to read catalog ZIP: " + e.getMessage(), e);
        }
        throw new WanakuException("Catalog ZIP does not contain " + INDEX_FILE);
    }

    private static void extractAll(byte[] zipBytes, Path dataDir) throws WanakuException {
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    zis.closeEntry();
                    continue;
                }

                String name = entry.getName();
                if (name.contains("..")) {
                    zis.closeEntry();
                    continue;
                }

                Path targetPath = dataDir.resolve(name);
                Files.createDirectories(targetPath.getParent());

                try (OutputStream out = Files.newOutputStream(targetPath)) {
                    zis.transferTo(out);
                }

                LOG.info("Extracted catalog file '{}' to {}", name, targetPath);
                zis.closeEntry();
            }
        } catch (IOException e) {
            throw new WanakuException("Failed to extract catalog files: " + e.getMessage(), e);
        }
    }

    private static String requireProperty(Properties props, String key, String system) throws WanakuException {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new WanakuException(
                    "System '" + system + "' not found in catalog index (missing property: " + key + ")");
        }
        return value.trim();
    }
}
