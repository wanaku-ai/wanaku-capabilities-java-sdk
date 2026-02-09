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
import org.apache.camel.spi.DataFormatResolver;
import org.apache.camel.spi.LanguageResolver;
import org.apache.camel.spi.Resource;
import org.apache.camel.spi.ResourceLoader;
import org.apache.camel.spi.RoutesLoader;
import org.apache.camel.spi.TransformerResolver;
import org.apache.camel.spi.UriFactoryResolver;
import org.apache.camel.support.PluginHelper;
import org.slf4j.Logger;

public class WanakuRoutesLoader {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(WanakuRoutesLoader.class);

    private final String dependenciesList;
    private final String repositoriesList;
    private final DependencyDownloaderClassLoader cl;
    private final MavenDependencyDownloader downloader;

    public WanakuRoutesLoader(String dependenciesList, String repositoriesList) {
        this.dependenciesList = dependenciesList;
        this.repositoriesList = repositoriesList;
        this.cl = createClassLoader();
        this.downloader = createDownloader(cl);
    }

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

        final ResourceLoader resourceLoader = PluginHelper.getResourceLoader(context);
        final Resource resource = resourceLoader.resolveResource(path);

        try {
            loader.loadRoutes(resource);
        } catch (Exception e) {
            LOG.error("Failed to load routes from {}", path, e);
            return;
        }

        context.build();
    }

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

    private MavenDependencyDownloader createDownloader(DependencyDownloaderClassLoader cl) {
        MavenDependencyDownloader downloader = new MavenDependencyDownloader();
        downloader.setClassLoader(cl);

        if (repositoriesList != null) {
            downloader.setRepositories(repositoriesList);
        }

        downloader.start();
        return downloader;
    }

    private static DependencyDownloaderClassLoader createClassLoader() {
        final ClassLoader parentCL = WanakuRoutesLoader.class.getClassLoader();

        return new DependencyDownloaderClassLoader(parentCL);
    }
}
