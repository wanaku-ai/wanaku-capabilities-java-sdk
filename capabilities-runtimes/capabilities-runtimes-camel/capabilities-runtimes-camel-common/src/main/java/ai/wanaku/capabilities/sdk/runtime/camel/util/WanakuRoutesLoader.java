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
import org.apache.camel.spi.ContextServicePlugin;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.ResourceLoader;
import org.apache.camel.spi.RoutesLoader;
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

    /**
     * Creates a new loader.
     */
    public WanakuRoutesLoader() {}

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

        loadServiceProperties(context, path);
        discoverAndLoadPlugins(context);

        final ResourceLoader resourceLoader = PluginHelper.getResourceLoader(context);
        final Resource resource = resourceLoader.resolveResource(path);

        try {
            camelContextExtension.getContextPlugin(RoutesLoader.class).loadRoutes(resource);
        } catch (Exception e) {
            throw new RouteLoadingException(path, e);
        }

        context.build();
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
        ServiceLoader<ContextServicePlugin> plugins =
                ServiceLoader.load(ContextServicePlugin.class, context.getApplicationContextClassLoader());

        for (ContextServicePlugin plugin : plugins) {
            if (plugin.getClass().getClassLoader() != WanakuRoutesLoader.class.getClassLoader()) {
                LOG.info("Loading discovered plugin: {}", plugin.getClass().getName());
                plugin.load(context);
            }
        }
    }
}
