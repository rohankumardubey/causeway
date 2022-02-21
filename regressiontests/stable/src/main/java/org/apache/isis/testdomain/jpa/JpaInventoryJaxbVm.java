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
package org.apache.isis.testdomain.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.Collection;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberSupport;
import org.apache.isis.applib.annotation.Nature;
import org.apache.isis.applib.annotation.ObjectSupport;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.services.repository.RepositoryService;
import org.apache.isis.testdomain.jpa.entities.JpaBook;
import org.apache.isis.testdomain.jpa.entities.JpaProduct;
import org.apache.isis.testdomain.util.dto.IBook;

import lombok.Getter;
import lombok.Setter;

@XmlRootElement(name = "root")
@XmlType(
        propOrder = {"name", "favoriteBook", "bookForTab1", "bookForTab2", "books"}
)
@XmlAccessorType(XmlAccessType.FIELD)
@DomainObject(
        nature=Nature.VIEW_MODEL
        , logicalTypeName = "testdomain.jpa.JpaInventoryJaxbVm"
)
public class JpaInventoryJaxbVm {

    @XmlTransient @Inject
    private RepositoryService repository;

    @ObjectSupport public String title() {
        return String.format("%s; %s; %d products",
                this.getClass().getSimpleName(), getName(), listProducts().size());
    }

    @Property(editing = Editing.ENABLED)
    @Getter @Setter
    @XmlElement
    private String name;

    @Action
    public List<JpaProduct> listProducts() {
        return repository.allInstances(JpaProduct.class);
    }

    @Action
    public List<JpaBook> listBooks() {
        return repository.allInstances(JpaBook.class);
    }

    @Property(editing = Editing.ENABLED, optionality = Optionality.OPTIONAL)
    @XmlElement(required = false)
    @Getter @Setter
    private JpaBook favoriteBook = null;

    @MemberSupport public List<JpaBook> choicesFavoriteBook() {
        return listBooks();
    }

    @Collection
    @XmlElement(name = "book")
    @Getter @Setter
    private java.util.Collection<JpaBook> books = new ArrayList<>();

    // -- TAB TEST

    @Getter @Setter
    @Property(editing = Editing.ENABLED, optionality = Optionality.OPTIONAL)
    @XmlElement(required = false)
    private JpaBook bookForTab1 = null;

    @MemberSupport public List<JpaBook> choicesBookForTab1() {
        return listBooks();
    }

    @Property
    @XmlTransient
    public String getBookNameForTab1() {
        return Optional.ofNullable(getBookForTab1())
                .map(IBook::getName)
                .orElse("none selected");
    }


    @Getter @Setter
    @Property(editing = Editing.ENABLED, optionality = Optionality.OPTIONAL)
    @XmlElement(required = false)
    private JpaBook bookForTab2 = null;

    @MemberSupport public List<JpaBook> choicesBookForTab2() {
        return listBooks();
    }

    @Property
    @XmlTransient
    public String getBookNameForTab2() {
        return Optional.ofNullable(getBookForTab2())
                .map(IBook::getName)
                .orElse("none selected");
    }

}