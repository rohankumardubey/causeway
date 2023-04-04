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
package demoapp.dom.domain.objects.DomainObject.nature.entity.jpa;

import demoapp.dom.domain.objects.DomainObject.nature.entity.DomainObjectNatureEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.inject.Named;
import jakarta.persistence.*;

import org.apache.causeway.applib.annotation.Bounding;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.persistence.jpa.applib.integration.CausewayEntityListener;
import org.springframework.context.annotation.Profile;

@Profile("demo-jpa")
//tag::class[]
@Entity                                             // <.>
@Table(                                             // <1>
    schema = "demo",
    name = "DomainObjectNatureJpa"
)
@EntityListeners(CausewayEntityListener.class)
@Named("demo.DomainObjectNatureJpa")
@DomainObject(nature = Nature.ENTITY)               // <.>
@NoArgsConstructor
public class DomainObjectNatureJpa
        extends DomainObjectNatureEntity {
    // ...
//end::class[]

    public DomainObjectNatureJpa(final String name) {
        this.name = name;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Getter @Setter
    private String name;

//tag::class[]
}
//end::class[]