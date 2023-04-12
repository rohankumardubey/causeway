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
package org.apache.causeway.viewer.commons.applib.services.menu.model;

import org.springframework.lang.Nullable;

import org.apache.causeway.applib.Identifier;
import org.apache.causeway.applib.services.bookmark.Bookmark;
import org.apache.causeway.commons.internal.exceptions._Exceptions;
import org.apache.causeway.core.metamodel.interactions.managed.ManagedAction;

import lombok.NonNull;

public record MenuAction (
        @NonNull Bookmark serviceBookmark,
        @NonNull Identifier actionId,
        @NonNull String name,
        @Nullable String cssClassFa
        ) implements MenuEntry {


    public static MenuAction of(final @NonNull ManagedAction managedAction) {
        // TODO missing cssClass
        return new MenuAction(
                managedAction.getOwner().getBookmark().orElseThrow(),
                managedAction.getIdentifier(),
                managedAction.getFriendlyName(),
                null);
    }

    @Deprecated
    public ManagedAction managedAction(){
        throw _Exceptions.notImplemented();
    }

}