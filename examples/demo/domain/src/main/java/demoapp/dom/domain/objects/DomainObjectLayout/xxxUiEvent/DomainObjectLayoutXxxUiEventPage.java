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
package demoapp.dom.domain.objects.DomainObjectLayout.xxxUiEvent;

import demoapp.dom._infra.asciidocdesc.HasAsciiDocDescription;
import lombok.Getter;
import lombok.Setter;

import jakarta.inject.Named;
import jakarta.xml.bind.annotation.*;

import org.apache.causeway.applib.annotation.*;

//tag::class[]
@XmlRootElement(name = "root")
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
@Named("demo.DomainObjectLayoutXxxUiEventVm")
@DomainObject(
        nature=Nature.VIEW_MODEL)
@DomainObjectLayout(
        titleUiEvent = DomainObjectLayoutXxxUiEventPage.TitleUiEvent.class,
        iconUiEvent = DomainObjectLayoutXxxUiEventPage.IconUiEvent.class,
        cssClassUiEvent = DomainObjectLayoutXxxUiEventPage.CssClassUiEvent.class,
        layoutUiEvent = DomainObjectLayoutXxxUiEventPage.LayoutUiEvent.class
        )
public class DomainObjectLayoutXxxUiEventPage implements HasAsciiDocDescription {

    public static class TitleUiEvent extends org.apache.causeway.applib.events.ui.TitleUiEvent<DomainObjectLayoutXxxUiEventPage> { }
    public static class IconUiEvent extends org.apache.causeway.applib.events.ui.IconUiEvent<DomainObjectLayoutXxxUiEventPage> { }
    public static class CssClassUiEvent extends org.apache.causeway.applib.events.ui.CssClassUiEvent<DomainObjectLayoutXxxUiEventPage> { }
    public static class LayoutUiEvent extends org.apache.causeway.applib.events.ui.LayoutUiEvent<DomainObjectLayoutXxxUiEventPage> { }

    @ObjectSupport public String title() {
        return "DomainObjectLayout#xxxUiEvent (should be overwritten by ui-title-event-listener)";
    }

    @ObjectSupport public String layout() {
        return "alternative1"; // should be overwritten by ui-layout-event-listener
    }

    //TODO[CAUSEWAY-3309]
    @Property(optionality = Optionality.OPTIONAL)
    @XmlElement(required = false)
    @Getter @Setter
    private String dummy;

}
//end::class[]