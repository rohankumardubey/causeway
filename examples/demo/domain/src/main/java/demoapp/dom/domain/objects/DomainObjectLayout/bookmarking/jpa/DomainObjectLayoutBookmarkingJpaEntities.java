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
package demoapp.dom.domain.objects.DomainObjectLayout.bookmarking.jpa;

import demoapp.dom._infra.values.ValueHolderRepository;
import demoapp.dom.domain.objects.DomainObject.aliased.DomainObjectAliasedRepository;
import demoapp.dom.domain.objects.DomainObjectLayout.bookmarking.DomainObjectLayoutBookmarking;
import demoapp.dom.domain.objects.DomainObjectLayout.bookmarking.DomainObjectLayoutBookmarkingRepository;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("demo-jpa")
@Service
public class DomainObjectLayoutBookmarkingJpaEntities
extends ValueHolderRepository<String, DomainObjectLayoutBookmarkingJpa> implements DomainObjectLayoutBookmarkingRepository {

    protected DomainObjectLayoutBookmarkingJpaEntities() {
        super(DomainObjectLayoutBookmarkingJpa.class);
    }

    @Override
    protected DomainObjectLayoutBookmarkingJpa newDetachedEntity(String value) {
        return new DomainObjectLayoutBookmarkingJpa(value);
    }

    @Override
    public List<? extends DomainObjectLayoutBookmarking> allInstances() {
        return all();
    }

    public List<? extends DomainObjectLayoutBookmarking> allMatches(final String s) {
        return all();
    }
    public List<? extends DomainObjectLayoutBookmarking> allMatches() {
        return all();
    }
}
