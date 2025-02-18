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
package org.apache.causeway.core.metamodel.spec.impl;

import org.apache.causeway.applib.id.LogicalType;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;

interface ObjectSpecificationMutable extends ObjectSpecification {
    
    public enum IntrospectionState implements Comparable<IntrospectionState> {
        /**
         * At this stage, {@link LogicalType} only.
         */
        NOT_INTROSPECTED,

        /**
         * Interim stage, to avoid infinite loops while on way to being {@link #TYPE_INTROSPECTED}
         */
        TYPE_BEING_INTROSPECTED,

        /**
         * Type has been introspected (but not its members).
         */
        TYPE_INTROSPECTED,

        /**
         * Interim stage, to avoid infinite loops while on way to being {@link #FULLY_INTROSPECTED}
         */
        MEMBERS_BEING_INTROSPECTED,

        /**
         * Fully introspected... class and also its members.
         */
        FULLY_INTROSPECTED

    }
    
    /**
     * Introspecting up to the level required.
     * @since 2.0
     */
    void introspectUpTo(IntrospectionState upTo);
    
}
