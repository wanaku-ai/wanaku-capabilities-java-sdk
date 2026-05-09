/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.wanaku.capabilities.sdk.runtime.camel.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.ServiceLoader;
import org.apache.camel.CamelContext;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.main.download.DependencyDownloader;
import org.apache.camel.main.download.DependencyDownloaderClassLoader;
import org.apache.camel.main.download.DependencyDownloaderComponentResolver;
import org.apache.camel.main.download.DependencyDownloaderDataFormatResolver;
import org.apache.camel.main.download.DependencyDownloaderLanguageResolver;
import org.apache.camel.main.download.DependencyDownloaderRoutesLoader;
import org.apache.camel.main.download.DependencyDownloaderTransformerResolver;
import org.apache.camel.main.download.DependencyDownloaderUriFactoryResolver;
import org.apache.camel.main.download.MavenDependencyDownloader;
import org.apache.camel.spi.ComponentResolver;
import org.apache.camel.spi.ContextServicePlugin;
import org.apache.camel.spi.DataFormatResolver;
import org.apache.camel.spi.LanguageResolver;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.ResourceLoader;
import org.apache.camel.spi.RoutesLoader;
import org.apache.camel.spi.TransformerResolver;
import org.apache.camel.spi.UriFactoryResolver;
import org.apache.camel.support.PluginHelper;
import org.slf4j.Logger;
import ai.wanaku.capabilities.sdk.runtime.camel.exceptions.RouteLoadingException;

/**
 * Loads Camel route definitions from resource paths, automatically downloading any required Maven
 * dependencies before the routes are parsed. This wires dependency-aware resolvers into the
 * {@link CamelContext} so that components, data formats, languages, and transformers referenced
 * in the route file are fetched on-the-fly from Maven repositories.
 *
 * @see RouteLoadingException
 */
public class WanakuRoutesLoader {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(WanakuRoutesLoader.class);

    private final String dependenciesList;
    private final String repositoriesList;
    private final DependencyDownloaderClassLoader cl;
    private final MavenDependencyDownloader downloader;

    /**
     * Creates a new loader.
     *
     * @param dependenciesList comma-separated Maven coordinates ({@code group:artifact:version}) to
     *                         download before loading routes, or {@code null} for none
     * @param repositoriesList comma-separated Maven repository URLs to resolve dependencies from,
     *                         or {@code null} to use the default repositories
     */
    public WanakuRoutesLoader(String dependenciesList, String repositoriesList) {
        this.dependenciesList = dependenciesList;
        this.repositoriesList = repositoriesList;
        this.cl = createClassLoader();
        this.downloader = createDownloader(cl);
    }

    /**
     * Downloads required dependencies, registers dependency-aware resolvers on the given context,
     * loads the route definition from {@code path}, and builds the context.
     *
     * @param context the Camel context to load the route into
     * @param path    the resource path to the route definition (e.g. a YAML or XML route file)
     * @throws RouteLoadingException if the route cannot be parsed or loaded
     */
    public void loadRoute(CamelContext context, String path) {
        final ExtendedCamelContext camelContextExtension = context.getCamelContextExtension();

        try {
            context.addService(downloader);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        camelContextExtension.addContextPlugin(
                ComponentResolver.class, new DependencyDownloaderComponentResolver(context, null, false, false));
        camelContextExtension.addContextPlugin(
                DataFormatResolver.class, new DependencyDownloaderDataFormatResolver(context, null, false));
        camelContextExtension.addContextPlugin(
                LanguageResolver.class, new DependencyDownloaderLanguageResolver(context, null, false));
        camelContextExtension.addContextPlugin(
                TransformerResolver.class, new DependencyDownloaderTransformerResolver(context, null, false));
        camelContextExtension.addContextPlugin(
                UriFactoryResolver.class, new DependencyDownloaderUriFactoryResolver(context));

        downloadDependencies(context);

        DependencyDownloaderRoutesLoader loader = new DependencyDownloaderRoutesLoader(context);
        camelContextExtension.addContextPlugin(RoutesLoader.class, loader);

        loadServiceProperties(context, path);
        discoverAndLoadPlugins(context);

        final ResourceLoader resourceLoader = PluginHelper.getResourceLoader(context);
        final Resource resource = resourceLoader.resolveResource(path);

        try {
            loader.loadRoutes(resource);
        } catch (Exception e) {
            throw new RouteLoadingException(path, e);
        }

        context.build();
    }

    /**
     * Downloads each dependency from {@link #dependenciesList} using the Maven downloader and
     * registers the downloader as a context plugin.
     */
    private void downloadDependencies(CamelContext camelContext) {
        ExtendedCamelContext camelContextExtension = camelContext.getCamelContextExtension();

        if (dependenciesList != null) {
            final String[] dependencies = dependenciesList.split(",");
            for (String dependency : dependencies) {
                // In case of empty file
                if (!dependency.isEmpty()) {
                    dependency = dependency.trim();
                    downloader.downloadDependency(
                            GavUtil.group(dependency), GavUtil.artifact(dependency), GavUtil.version(dependency));
                }
            }

            cl.getDownloaded().forEach(d -> LOG.debug("Downloaded {}", d));
        }

        Thread.currentThread().setContextClassLoader(cl);
        camelContextExtension.addContextPlugin(DependencyDownloader.class, downloader);
    }

    /**
     * Creates and starts a {@link MavenDependencyDownloader} configured with the given class loader
     * and optional custom repositories.
     */
    private MavenDependencyDownloader createDownloader(DependencyDownloaderClassLoader cl) {
        MavenDependencyDownloader downloader = new MavenDependencyDownloader();
        downloader.setClassLoader(cl);

        if (repositoriesList != null) {
            downloader.setRepositories(repositoriesList);
        }

        downloader.start();
        return downloader;
    }

    private void loadServiceProperties(CamelContext context, String routePath) {
        Path routeFile = Path.of(URI.create(routePath));
        Path serviceProps = routeFile.getParent().resolve("service.properties");

        if (!Files.exists(serviceProps)) {
            return;
        }

        context.getPropertiesComponent().addLocation("file:" + serviceProps.toAbsolutePath());
        LOG.info("Registered service.properties as Camel property source: {}", serviceProps);

        try (InputStream is = Files.newInputStream(serviceProps)) {
            Properties props = new Properties();
            props.load(is);

            for (String key : props.stringPropertyNames()) {
                if (key.startsWith("forage.")) {
                    System.setProperty(key, props.getProperty(key));
                    LOG.debug("Set system property from service.properties: {}", key);
                }
            }
        } catch (IOException e) {
            LOG.warn("Failed to load service.properties: {}", e.getMessage());
        }
    }

    private void discoverAndLoadPlugins(CamelContext context) {
        ClassLoader depClassLoader = Thread.currentThread().getContextClassLoader();
        ServiceLoader<ContextServicePlugin> plugins = ServiceLoader.load(ContextServicePlugin.class, depClassLoader);

        for (ContextServicePlugin plugin : plugins) {
            if (plugin.getClass().getClassLoader() != WanakuRoutesLoader.class.getClassLoader()) {
                LOG.info("Loading discovered plugin: {}", plugin.getClass().getName());
                plugin.load(context);
            }
        }
    }

    /** Creates a child class loader that the dependency downloader will add downloaded JARs to. */
    private static DependencyDownloaderClassLoader createClassLoader() {
        final ClassLoader parentCL = WanakuRoutesLoader.class.getClassLoader();

        return new DependencyDownloaderClassLoader(parentCL);
    }
}
