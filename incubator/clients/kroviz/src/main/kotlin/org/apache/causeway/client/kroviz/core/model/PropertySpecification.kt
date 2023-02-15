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
package org.apache.causeway.client.kroviz.core.model

import org.apache.causeway.client.kroviz.to.Member
import org.apache.causeway.client.kroviz.to.bs.PropertyBs

/**
 * Properties have multiple aspects:
 *
 * - Member of a DomainObject
 * - Description (friendlyName, etc.)
 * - Layout (disabledReason, labelPosition, etc.)
 * - Visibility (hidden)
 *
 * All are required in order to be correctly displayed (in a table).
 */
class PropertySpecification(member: Member) {
    var id = member.id
    var name = "" // aka: columnName, named, label, title
    var hidden = true
    var disabled = member.disabledReason.isNotEmpty()
    var isAmendedFromBs = false
    var typicalLength: Int = 10


    fun amendWith(pbs: PropertyBs) {
        console.log("[PS_amendWith] PropertyBs")
        name = pbs.named
        hidden = !(pbs.hidden != null && pbs.hidden.isNotEmpty())
        if (pbs.typicalLength != null && pbs.typicalLength > 0) {
            typicalLength = pbs.typicalLength.toInt()
        }
        isAmendedFromBs = true
        console.log(this)
    }

    fun readyToRender(): Boolean {
        return when (id) {
            "logicalTypeName" -> true
            "objectIdentifier" -> true
            else -> isAmendedFromBs
        }
    }

}