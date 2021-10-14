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
package org.apache.isis.viewer.common.model.action;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.core.metamodel.consent.Consent;
import org.apache.isis.core.metamodel.consent.InteractionInitiatedBy;
import org.apache.isis.core.metamodel.interactions.managed.ManagedAction;
import org.apache.isis.core.metamodel.spec.ManagedObject;
import org.apache.isis.core.metamodel.spec.feature.ObjectAction;
import org.apache.isis.core.metamodel.spec.feature.memento.ActionMemento;
import org.apache.isis.core.metamodel.specloader.SpecificationLoader;
import org.apache.isis.viewer.common.model.decorator.disable.DisablingUiModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Deprecated
public final class ActionUiMetaModel
implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter private final ActionMemento actionMemento;
    @Getter private final ActionLayout.Position position;
    @Getter private final Optional<DisablingUiModel> disableUiModel;


    public static <T> ActionUiMetaModel of(
            final ManagedAction managedAction) {
        return new ActionUiMetaModel(managedAction.getOwner(), managedAction.getAction());
    }

    public static <T> ActionUiMetaModel of(
            final ManagedObject owner,
            final ObjectAction objectAction) {
        return new ActionUiMetaModel(owner, objectAction);
    };

    private ActionUiMetaModel(
            final ManagedObject owner,
            final ObjectAction objectAction) {

        this(   objectAction.getMemento(),
                ObjectAction.Util.actionLayoutPositionOf(objectAction),
                disabledUiModelFor(owner, objectAction));
    }

    public static <R> Predicate<R> positioned(
            final ActionLayout.Position position,
            final Function<R, ActionUiMetaModel> posAccessor) {
        return x -> posAccessor.apply(x).getPosition() == position;
    }

    public ObjectAction getObjectAction(final Supplier<SpecificationLoader> specLoader) {
        return actionMemento.getAction(specLoader);
    }

    // -- USABILITY

    private static Optional<DisablingUiModel> disabledUiModelFor(
            final @NonNull ManagedObject actionHolder,
            final @NonNull ObjectAction objectAction) {

        // check usability
        final Consent usability = objectAction.isUsable(
                actionHolder,
                InteractionInitiatedBy.USER,
                Where.ANYWHERE
                );

        val enabled = usability.getReason() == null;
        return DisablingUiModel.of(!enabled, usability.getReason()) ;
    }


}
