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
package org.apache.isis.viewer.wicket.ui.test.components.widgets.choices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.wicketstuff.select2.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.commons.collections.Can;
import org.apache.isis.core.metamodel.object.ManagedObject;
import org.apache.isis.core.metamodel.objectmanager.memento.ObjectMemento;
import org.apache.isis.viewer.wicket.ui.components.widgets.select2.providers.ChoiceProviderForReferences;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.val;

class ChoiceProviderForReferencesTest extends ChoiceProviderTestAbstract {

    @BeforeEach
    void setup() throws Exception {
        super.setUp();
    }

    @DomainObject(nature = Nature.VIEW_MODEL)
    @Data
    @AllArgsConstructor
    public static class Customer implements ViewModel {

        private String name;

        @Override
        public String viewModelMemento() {
            return name;
        }

    }

  //FIXME[ISIS-3207]
    @DisabledIfSystemProperty(named = "isRunningWithSurefire", matches = "true")
    @Test
    void roundtrip() {

        val a = new Customer("a");
        val b = new Customer("b");
        val c = new Customer("c");

        val choiceValues = Can.of(a, b, c);

        val choices = choiceValues
                .map(mmc.getObjectManager()::adapt);

        val isRequired = true;
        val choiceProvider = new ChoiceProviderForReferences(mockScalarModel(choices, isRequired));

        val response = new Response<ObjectMemento>();
        choiceProvider.query(null, 0, response);
        val mementos = Can.ofCollection(response.getResults()); // throws null if any away

        assertEquals(3, mementos.size());

        mementos
        .forEach(memento->{
            System.err.printf("id: %s%n", choiceProvider.getIdValue(memento));
            System.err.printf("title (un-translated):  %s%n", memento.getTitle());
            System.err.printf("displayValue: %s%n", choiceProvider.getDisplayValue(memento));
        });

        val asIds = mementos.map(choiceProvider::getIdValue);

        val recoveredMementos = Can.ofCollection(choiceProvider.toChoices(asIds.toList()));

        val recoveredChoices = recoveredMementos
                .map(mmc.getObjectManager()::demementify);

        val recoveredChoiceValues = recoveredChoices
                .map(ManagedObject::getPojo);

        assertEquals(choiceValues, recoveredChoiceValues);

    }

}