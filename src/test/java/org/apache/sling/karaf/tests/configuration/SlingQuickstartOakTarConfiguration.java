/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.karaf.tests.configuration;

import org.apache.sling.karaf.testing.KarafTestSupport;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.OptionUtils;

import static org.ops4j.pax.exam.CoreOptions.cleanCaches;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.overrideJUnitBundles;

public class SlingQuickstartOakTarConfiguration extends KarafTestSupport {

    @Configuration
    public Option[] configuration() throws Exception {
        final int httpPort = Integer.getInteger("http.port");
        final String[] scripts = new String[]{
            "create path /repoinit/provisioningModelTest\ncreate service user provisioningModelUser",
            "create path (sling:OrderedFolder) /ANON_CAN_READ",
            "set ACL for everyone\nallow jcr:read on /ANON_CAN_READ\nend"
        };
        return OptionUtils.combine(baseConfiguration(),
            cleanCaches(true),
            // configurations for tests
            editConfigurationFilePut("etc/custom.properties", "sling.run.modes", "oak_tar"),
            editConfigurationFilePut("etc/users.properties", "admin", "admin,_g_:admingroup"), // Sling’s default admin credentials used in tests
            editConfigurationFilePut("etc/org.apache.felix.http.cfg", "org.osgi.service.http.port", Integer.toString(httpPort)),
            editConfigurationFilePut("etc/integrationTestsConfig.config", "message", "This test config should be loaded at startup"),
            editConfigurationFilePut("etc/org.apache.sling.servlets.resolver.SlingServletResolver.config", "servletresolver.cacheSize", "0"),
            editConfigurationFilePut("etc/org.apache.sling.jcr.webdav.impl.servlets.SimpleWebDavServlet.config", "dav.root", "/dav"),
            editConfigurationFilePut("etc/org.apache.sling.jcr.davex.impl.servlets.SlingDavExServlet.config", "alias", "/server"),
            editConfigurationFilePut("etc/org.apache.sling.jcr.repoinit.RepositoryInitializer~test.config", "scripts", scripts),
            addSlingFeatures(
                "sling-quickstart-oak-tar",
                "sling-auth-form",
                "sling-models",
                "sling-installer",
                "sling-installer-factory-configuration",
                "sling-installer-provider-jcr",
                "sling-jcr-jackrabbit-security",
                "sling-scripting-groovy",
                "sling-scripting-javascript",
                "sling-scripting-jsp",
                "sling-scripting-htl"
            ),
            addFelixHttpFeature(),
            mavenBundle().groupId("org.ops4j.pax.url").artifactId("pax-url-classpath").versionAsInProject(),
            // test support
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.junit.core").versionAsInProject(),
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.junit.remote").versionAsInProject(),
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.junit.scriptable").versionAsInProject(),
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.launchpad.test-services").versionAsInProject(),
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.launchpad.test-fragment").versionAsInProject(),
            mavenBundle().groupId("org.apache.sling").artifactId("org.apache.sling.testing.tools").versionAsInProject(),
            overrideJUnitBundles()
        );
    }

}
