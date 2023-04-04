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
package demoapp.dom.domain.properties.Property.optionality;

import jakarta.inject.Named;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.PropertyLayout;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;
import lombok.Getter;
import lombok.Setter;

//tag::class[]
@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Named("demo.PropertyOptionalityVm")
@DomainObject(
        nature=Nature.VIEW_MODEL,
        editing = Editing.ENABLED)
public class PropertyOptionalityPage implements HasAsciiDocDescription {
    // ...
//end::class[]

    @ObjectSupport public String title() {
        return "Property#optionality";
    }

//tag::annotation[]
    @Property(
        optionality = Optionality.OPTIONAL              // <.>
    )
    @PropertyLayout(
        describedAs =
            "@Property(optionality = OPTIONAL)",
        fieldSetId = "annotation", sequence = "1")
    @XmlElement(required = false)
    @Getter @Setter
    private String propertyUsingAnnotation;
//end::annotation[]

//tag::annotation-mandatory[]
    @Property(
        optionality = Optionality.MANDATORY             // <.>
    )
    @PropertyLayout(
        describedAs =
            "@Property(optionality = MANDATORY)",
        fieldSetId = "annotation", sequence = "2")
    @XmlElement(required = true)
    @Getter @Setter
    private String mandatoryPropertyUsingAnnotation;
//end::annotation-mandatory[]

//tag::meta-annotated[]
    @OptionalityOptionalMetaAnnotation                  // <.>
    @Property()
    @PropertyLayout(
        describedAs = "@OptionalityOptionalMetaAnnotation",
        fieldSetId = "meta-annotated", sequence = "1")
    @XmlElement(required = false)
    @Getter @Setter
    private String propertyUsingMetaAnnotation;
//end::meta-annotated[]

//tag::meta-annotated-overridden[]
    @OptionalityOptionalMetaAnnotation                  // <.>
    @Property(
        optionality = Optionality.MANDATORY             // <.>
    )
    @PropertyLayout(
        describedAs =
            "@OptionalityOptionalMetaAnnotation " +
            "@PropertyLayout(optionality = MANDATORY)",
        fieldSetId = "meta-annotated-overridden", sequence = "1")
    @XmlElement(required = true)
    @Getter @Setter
    private String propertyUsingMetaAnnotationButOverridden;
//end::meta-annotated-overridden[]

//tag::class[]
}
//end::class[]