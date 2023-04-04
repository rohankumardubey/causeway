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
package org.apache.causeway.viewer.thymeflux.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import org.apache.causeway.core.runtimeservices.CausewayModuleCoreRuntimeServices;
import org.apache.causeway.security.bypass.CausewayModuleSecurityBypass;
import org.apache.causeway.viewer.thymeflux.model.root.ThymefluxRootController;
import org.apache.causeway.viewer.thymeflux.viewer.CausewayModuleIncViewerThymefluxViewer;

@SpringBootTest(
    classes = {
            //DemoModuleJpa.class,
            CausewayModuleCoreRuntimeServices.class,
            CausewayModuleSecurityBypass.class,
            CausewayModuleIncViewerThymefluxViewer.class
    }
    //,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles(profiles = "demo-jpa")
@AutoConfigureWebTestClient
class ThymefluxViewerTests {

    //@Autowired private WebTestClient client;
    @Autowired private ThymefluxRootController rootController;

    @Test
    void contextLoads() {
    }

    @Test @Disabled
    void rootController() {
        Model model = new BindingAwareModelMap();
        rootController.root(model);
    }


//    @Test
//    void responseOkOnRoot() {
////        Mockito
////            .when(repository.getIsDataReady(ArgumentMatchers.any()))
////            .thenReturn(Mono.just(true));
//
//        client.get()
//            .uri("/tflux/")
//            //.accept(MediaType.APPLICATION_JSON_UTF8)
//            .exchange()
//            .expectStatus().isOk();
//    }
}
