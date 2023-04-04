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
package demoapp.dom.domain.properties.Property.projecting;

import java.util.ArrayList;
import java.util.List;

import jakarta.inject.Named;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.causeway.applib.annotation.Collection;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.annotation.PropertyLayout;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;
import demoapp.dom.domain.properties.Property.projecting.child.PropertyProjectingChildVm;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//tag::class[]
@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Named("demo.PropertyProjectingVm")
@DomainObject(
        nature=Nature.VIEW_MODEL,
        editing = Editing.ENABLED)
@NoArgsConstructor
public class PropertyProjectingPage implements HasAsciiDocDescription {

    @ObjectSupport public String title() {
        return "Property#projecting";
    }

//tag::property[]
    @PropertyLayout(fieldSetId = "properties", sequence = "1")
    public PropertyProjectingChildVm getFirstChild() {
        return getChildren().get(0);
    }
//end::property[]

//tag::children[]
    @Collection
    @XmlElementWrapper(name = "children")
    @XmlElement(name = "child")
    @Getter @Setter
    private List<PropertyProjectingChildVm> children = new ArrayList<>();
//end::children[]

}
//end::class[]