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

    private ServiceCatalogExtractor() {}

    /**
     * Extracts routes, rules, and optionally dependencies files for a given system
     * from a Base64-encoded service catalog ZIP archive.
     *
     * @param base64Data the Base64-encoded ZIP data
     * @param system     the system name to extract files for
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
            throw new WanakuException("Invalid Base64 data: " + e.getMessage());
        }

        Properties props = readIndex(zipBytes);
        String routesEntry = requireProperty(props, PROP_ROUTES_PREFIX + system, system);
        String rulesEntry = requireProperty(props, PROP_RULES_PREFIX + system, system);
        String depsEntry = props.getProperty(PROP_DEPENDENCIES_PREFIX + system);

        Map<String, ResourceType> entriesToExtract = new HashMap<>();
        entriesToExtract.put(routesEntry, ResourceType.ROUTES_REF);
        entriesToExtract.put(rulesEntry, ResourceType.RULES_REF);
        if (depsEntry != null && !depsEntry.isBlank()) {
            entriesToExtract.put(depsEntry.trim(), ResourceType.DEPENDENCY_REF);
        }

        Map<ResourceType, Path> result = extractEntries(zipBytes, entriesToExtract, dataDir);

        if (!result.containsKey(ResourceType.ROUTES_REF)) {
            throw new WanakuException(
                    "Routes file '" + routesEntry + "' for system '" + system + "' not found in catalog ZIP");
        }
        if (!result.containsKey(ResourceType.RULES_REF)) {
            throw new WanakuException(
                    "Rules file '" + rulesEntry + "' for system '" + system + "' not found in catalog ZIP");
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
            throw new WanakuException("Failed to read catalog ZIP: " + e.getMessage());
        }
        throw new WanakuException("Catalog ZIP does not contain " + INDEX_FILE);
    }

    private static Map<ResourceType, Path> extractEntries(
            byte[] zipBytes, Map<String, ResourceType> entriesToExtract, Path dataDir) throws WanakuException {
        Map<ResourceType, Path> result = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                ResourceType type = entriesToExtract.get(entry.getName());
                if (type != null) {
                    String fileName = Path.of(entry.getName()).getFileName().toString();
                    Path targetPath = dataDir.resolve(fileName);
                    Files.createDirectories(targetPath.getParent());

                    try (OutputStream out = Files.newOutputStream(targetPath)) {
                        zis.transferTo(out);
                    }

                    result.put(type, targetPath);
                    LOG.info("Extracted catalog file '{}' to {}", entry.getName(), targetPath);
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            throw new WanakuException("Failed to extract catalog files: " + e.getMessage());
        }

        return result;
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
