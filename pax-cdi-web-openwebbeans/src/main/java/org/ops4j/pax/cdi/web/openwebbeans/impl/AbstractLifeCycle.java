/*
 * Copyright 2012 Harald Wellmann.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Derived from org.apache.webbeans.lifecycle.AbstractLifeCycle.
 */
package org.ops4j.pax.cdi.web.openwebbeans.impl;

import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.BeanManager;

import org.apache.webbeans.config.BeansDeployer;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.config.WebBeansFinder;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.container.InjectionResolver;
import org.apache.webbeans.portable.events.discovery.BeforeShutdownImpl;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.apache.webbeans.spi.ContextsService;
import org.apache.webbeans.spi.JNDIService;
import org.apache.webbeans.spi.ScannerService;
import org.apache.webbeans.util.WebBeansConstants;
import org.apache.webbeans.xml.WebBeansXMLConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLifeCycle implements ContainerLifecycle {

    // Logger instance
    private static Logger log = LoggerFactory.getLogger(AbstractLifeCycle.class);

    /** Discover bean classes */
    protected ScannerService scannerService;

    protected ContextsService contextsService;

    protected WebBeansContext webBeansContext;

    /** Deploy discovered beans */
    private BeansDeployer deployer;

    /** XML discovery. */
    // XML discovery is removed from the specification. It is here for next revisions of spec.
    private WebBeansXMLConfigurator xmlDeployer;

    /** Using for lookup operations */
    private JNDIService jndiService;

    /** Root container. */
    private BeanManagerImpl beanManager;

    protected AbstractLifeCycle() {
        this(null, null);
    }

    protected AbstractLifeCycle(Properties properties, WebBeansContext webBeansContext) {
        beforeInitApplication(properties);
        if (webBeansContext != null) {
            setWebBeansContext(webBeansContext);
        }
        initApplication(properties);
    }

    protected void setWebBeansContext(WebBeansContext webBeansContext) {
        this.webBeansContext = webBeansContext;
        beanManager = this.webBeansContext.getBeanManagerImpl();
        xmlDeployer = new WebBeansXMLConfigurator();
        deployer = new BeansDeployer(xmlDeployer, this.webBeansContext);
        jndiService = this.webBeansContext.getService(JNDIService.class);
        beanManager.setXMLConfigurator(xmlDeployer);
        scannerService = this.webBeansContext.getScannerService();
        contextsService = this.webBeansContext.getService(ContextsService.class);
    }

    public WebBeansContext getWebBeansContext() {
        return webBeansContext;
    }

    public BeanManager getBeanManager() {
        return beanManager;
    }

    public void startApplication(Object startupObject) {
        // Initalize Application Context
        log.debug("OpenWebBeans Container is starting.");

        long begin = System.currentTimeMillis();

        // Before Start
        beforeStartApplication(startupObject);

        // Load all plugins
        webBeansContext.getPluginLoader().startUp();

        // Initialize contexts
        contextsService.init(startupObject);

        // Scanning process
        log.debug("Scanning classpaths for beans artifacts.");

        // Scan
        scannerService.scan();

        // Deploy beans
        log.debug("Deploying scanned beans.");

        // Deploy
        deployer.deploy(scannerService);

        // Start actual starting on sub-classes
        afterStartApplication(startupObject);

        log.debug("startup took {} ms", System.currentTimeMillis() - begin);
    }

    public void stopApplication(Object endObject) {
        log.debug("OpenWebBeans Container is stopping.");

        try {
            // Sub-classes operations
            beforeStopApplication(endObject);

            // Set up the thread local for Application scoped as listeners will be App scoped.
            contextsService.startContext(ApplicationScoped.class, endObject);

            // Fire shut down
            beanManager.fireEvent(new BeforeShutdownImpl());

            // Destroys context
            contextsService.destroy(endObject);

            // Unbind BeanManager
            jndiService.unbind(WebBeansConstants.WEB_BEANS_MANAGER_JNDI_NAME);

            // Free all plugin resources
            webBeansContext.getPluginLoader().shutDown();

            // Clear extensions
            webBeansContext.getExtensionLoader().clear();

            // Delete Resolutions Cache
            InjectionResolver injectionResolver = webBeansContext.getBeanManagerImpl()
                .getInjectionResolver();

            injectionResolver.clearCaches();

            // Delete AnnotateTypeCache
            webBeansContext.getAnnotatedElementFactory().clear();

            // After Stop
            afterStopApplication(endObject);

            // Clear BeanManager
            beanManager.clear();

            // Clear singleton list
            WebBeansFinder.clearInstances(endObject /* WebBeansUtil.getCurrentClassLoader() */);

        }
        // CHECKSTYLE:SKIP
        catch (Exception e) {
            log.error("error shutting down OpenWebBeans context", e);
        }

    }

    /**
     * @return the contextsService
     */
    public ContextsService getContextService() {
        return contextsService;
    }

    public void initApplication(Properties properties) {
        afterInitApplication(properties);
    }

    protected void beforeInitApplication(Properties properties) {
        // Do nothing as default
    }

    protected void afterInitApplication(Properties properties) {
        // Do nothing as default
    }

    protected void afterStartApplication(Object startupObject) {
        // Do nothing as default
    }

    protected void afterStopApplication(Object stopObject) {
        // Do nothing as default
    }

    protected void beforeStartApplication(Object startupObject) {
        // Do nothing as default
    }

    protected void beforeStopApplication(Object stopObject) {
        // Do nothing as default
    }
}
