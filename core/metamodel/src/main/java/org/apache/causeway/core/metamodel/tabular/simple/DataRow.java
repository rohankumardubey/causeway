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
package org.apache.causeway.core.metamodel.tabular.simple;

import org.apache.causeway.applib.annotation.Where;
import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.object.ManagedObjects;

import org.jspecify.annotations.NonNull;

/**
 * Represents a single domain object (typically an entity instance)
 * and it's associated values as cell elements.
 *
 * @since 2.0 {@index}
 */
public record DataRow(
    @NonNull ManagedObject rowElement) {

    /**
     * Can be none, one or many per table cell.
     */
    public Can<ManagedObject> getCellElements(
            final @NonNull DataColumn column,
            final InteractionInitiatedBy interactionInitiatedBy) {
        var assoc = column.metamodel();
        return assoc.getSpecialization().fold(
                property-> Can.of(
                        // similar to ManagedProperty#reassessPropertyValue
                        property.isVisible(rowElement(), interactionInitiatedBy, Where.ALL_TABLES).isAllowed()
                                ? property.get(rowElement(), interactionInitiatedBy)
                                : ManagedObject.empty(property.getElementType())),
                collection-> ManagedObjects.unpack(
                        collection.isVisible(rowElement(), interactionInitiatedBy, Where.ALL_TABLES).isAllowed()
                                ? collection.get(rowElement(), interactionInitiatedBy)
                                : null
                ));
    }

}
