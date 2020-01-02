/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.isis.extensions.h2console.dom.webmodule;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.apache.isis.applib.annotation.OrderPrecedence;
import org.apache.isis.applib.services.inject.ServiceInjector;
import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import org.apache.isis.applib.value.LocalResourcePath;
import org.apache.isis.commons.internal.base._Strings;
import org.apache.isis.commons.internal.environment.IsisSystemEnvironment;
import org.apache.isis.config.IsisConfiguration;
import org.apache.isis.webapp.modules.WebModule;
import org.apache.isis.webapp.modules.WebModuleContext;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import lombok.val;

@Service
@Named("isisExtH2Console.WebModuleH2Console")
@Order(OrderPrecedence.MIDPOINT)
@Qualifier("H2Console")
@Log4j2
public class WebModuleH2Console implements WebModule  {

    private final static String SERVLET_NAME = "H2Console";
    private final static String CONSOLE_PATH = "/db";

    private final IsisSystemEnvironment isisSystemEnvironment;
    private final IsisConfiguration isisConfiguration;
    private final ServiceInjector serviceInjector;

    @Getter
    private final LocalResourcePath localResourcePathIfEnabled;
    private final boolean applicable;

    @Inject
    public WebModuleH2Console(
            final IsisSystemEnvironment isisSystemEnvironment,
            final IsisConfiguration isisConfiguration,
            final ServiceInjector serviceInjector) {
        this.isisSystemEnvironment = isisSystemEnvironment;
        this.isisConfiguration = isisConfiguration;
        this.serviceInjector = serviceInjector;
        this.applicable = isPrototyping() && isUsesH2MemConnection();
        this.localResourcePathIfEnabled = applicable ? new LocalResourcePath(CONSOLE_PATH) : null;
    }

    @Getter
    private final String name = "H2Console";

    @Override
    public void prepare(WebModuleContext ctx) {
        // nothing special required
    }

    @Override
    public ServletContextListener init(final ServletContext ctx) throws ServletException {

        val servlet = ctx.addServlet(SERVLET_NAME, WebServlet.class);
        if(servlet != null) {
            serviceInjector.injectServicesInto(servlet);
            servlet.addMapping(CONSOLE_PATH + "/*");
            servlet.setInitParameter("webAllowOthers", "true");
        } else {
            // was already registered, eg in web.xml.
        }

        return null; // does not provide a listener
    }

    @Override
    public boolean isApplicable(WebModuleContext ctx) {
        return applicable;
    }

    // -- HELPER

    private boolean isPrototyping() {
        return isisSystemEnvironment.getDeploymentType().isPrototyping();
    }

    private boolean isUsesH2MemConnection() {
        val connectionUrl = isisConfiguration.getPersistor().getDatanucleus().getImpl().getJavax().getJdo().getOption().getConnectionUrl();
        return !_Strings.isNullOrEmpty(connectionUrl) && connectionUrl.contains(":h2:mem:");
    }

}
