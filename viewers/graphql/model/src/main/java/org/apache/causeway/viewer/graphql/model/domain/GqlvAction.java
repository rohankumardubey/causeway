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
package org.apache.causeway.viewer.graphql.model.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;

import org.apache.causeway.applib.services.bookmark.Bookmark;
import org.apache.causeway.applib.services.bookmark.BookmarkService;
import org.apache.causeway.commons.collections.Can;
import org.apache.causeway.core.metamodel.object.ManagedObject;
import org.apache.causeway.core.metamodel.spec.ObjectSpecification;
import org.apache.causeway.core.metamodel.spec.feature.ObjectAction;
import org.apache.causeway.core.metamodel.spec.feature.ObjectActionParameter;
import org.apache.causeway.core.metamodel.spec.feature.OneToManyActionParameter;
import org.apache.causeway.core.metamodel.spec.feature.OneToOneActionParameter;
import org.apache.causeway.viewer.graphql.model.context.Context;
import org.apache.causeway.viewer.graphql.model.fetcher.BookmarkedPojo;
import org.apache.causeway.viewer.graphql.model.types.TypeMapper;

import lombok.val;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GqlvAction
        extends GqlvMember<ObjectAction, GqlvMember.Holder>
        implements GqlvMemberHidden.Holder<ObjectAction>,
                   GqlvMemberDisabled.Holder<ObjectAction>,
                   GqlvActionInvoke.Holder,
                   GqlvActionValidity.Holder,
                   GqlvActionParams.Holder,
                   Parent {

    private final GqlvMemberHidden<ObjectAction> hidden;
    private final GqlvMemberDisabled<ObjectAction> disabled;
    private final GqlvActionValidity validate;
    /**
     * Populated iff the API variant allows for it.
     */
    private final GqlvActionInvoke invoke;
    /**
     * Populated iif there are params for this action.
     */
    private final GqlvActionParams params;

    public GqlvAction(
            final Holder holder,
            final ObjectAction objectAction,
            final Context context) {
        super(holder, objectAction, TypeNames.actionTypeNameFor(holder.getObjectSpecification(), objectAction), context);

        if(isBuilt()) {
            this.hidden = null;
            this.disabled = null;
            this.validate = null;
            this.invoke = null;
            this.params = null;
            return;
        }
        addChildFieldFor(this.hidden = new GqlvMemberHidden<>(this, context));
        addChildFieldFor(this.disabled = new GqlvMemberDisabled<>(this, context));
        addChildFieldFor(this.validate = new GqlvActionValidity(this, context));

        addChildFieldFor(
                this.invoke = isInvokeAllowed(objectAction)
                    ? new GqlvActionInvoke(this, context)
                    : null);
        addChildFieldFor(this.params = new GqlvActionParams(this, context));

        buildObjectTypeAndField(objectAction.getId());
    }

    private boolean isInvokeAllowed(ObjectAction objectAction) {
        val apiVariant = context.causewayConfiguration.getViewer().getGraphql().getApiVariant();
        switch (apiVariant) {
            case QUERY_ONLY:
            case QUERY_AND_MUTATIONS:
                return objectAction.getSemantics().isSafeInNature();
            case QUERY_WITH_MUTATIONS_NON_SPEC_COMPLIANT:
                return true;
            default:
                // shouldn't happen
                throw new IllegalArgumentException("Unknown API variant: " + apiVariant);
        }
    }

    public Can<ManagedObject> argumentManagedObjectsFor(
            final DataFetchingEnvironment dataFetchingEnvironment,
            final ObjectAction objectAction,
            final BookmarkService bookmarkService) {

        return argumentManagedObjectsFor(dataFetchingEnvironment, objectAction, context);
    }

    /**
     *
     * @param environment
     * @param objectAction
     * @param context
     * @return
     */
    public static Can<ManagedObject> argumentManagedObjectsFor(
            final DataFetchingEnvironment environment,
            final ObjectAction objectAction,
            final Context context) {
        Map<String, Object> argumentPojos = environment.getArguments();
        Can<ObjectActionParameter> parameters = objectAction.getParameters();
        return parameters
                .map(oap -> {
                    final ObjectSpecification elementType = oap.getElementType();
                    Object argumentValue = argumentPojos.get(oap.getId());
                    switch (elementType.getBeanSort()) {

                        case VALUE:
                            return adaptValue(oap, argumentValue, context);

                        case ENTITY:
                        case VIEW_MODEL:
                            if (argumentValue == null) {
                                return ManagedObject.empty(elementType);
                            }
                            Object pojoOrPojoList;
                            if (argumentValue instanceof List) {
                                val argumentValueList = (List<Object>) argumentValue;
                                pojoOrPojoList = argumentValueList.stream()
                                        .map(value -> asPojo(oap.getElementType(), value, context.bookmarkService, environment))
                                        .filter(Optional::isPresent)
                                        .map(Optional::get)
                                        .collect(Collectors.toList());
                            } else {
                                pojoOrPojoList = asPojo(oap.getElementType(), argumentValue, context.bookmarkService, environment).orElse(null);
                            }
                            return ManagedObject.adaptParameter(oap, pojoOrPojoList);

                        case ABSTRACT:
                        case COLLECTION:
                        case MANAGED_BEAN_CONTRIBUTING:
                        case VETOED:
                        case MANAGED_BEAN_NOT_CONTRIBUTING:
                        case MIXIN:
                        case UNKNOWN:
                        default:
                            throw new IllegalArgumentException(String.format(
                                    "Cannot handle an input type for %s; beanSort is %s", elementType.getFullIdentifier(), elementType.getBeanSort()));
                    }
                });
    }

    private static ManagedObject adaptValue(
            final ObjectActionParameter oap,
            final Object argumentValue,
            final Context context) {

        val elementType = oap.getElementType();
        if (argumentValue == null) {
            return ManagedObject.empty(elementType);
        }

        val argPojo = context.typeMapper.unmarshal(argumentValue, elementType);
        return ManagedObject.adaptParameter(oap, argPojo);
    }


    public static Optional<Object> asPojo(
            final ObjectSpecification elementType,
            final Object argumentValueObj,
            final BookmarkService bookmarkService,
            final DataFetchingEnvironment environment) {
        val argumentValue = (Map<String, String>) argumentValueObj;
        String idValue = argumentValue.get("id");
        if (idValue != null) {
            Class<?> paramClass = elementType.getCorrespondingClass();
            Optional<Bookmark> bookmarkIfAny = bookmarkService.bookmarkFor(paramClass, idValue);
            return bookmarkIfAny
                    .map(bookmarkService::lookup)
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        }
        String refValue = argumentValue.get("ref");
        if (refValue != null) {
            String key = GqlvMetaSaveAs.keyFor(refValue);
            BookmarkedPojo value = environment.getGraphQlContext().get(key);
            return Optional.of(value).map(BookmarkedPojo::getTargetPojo);
        }
        throw new IllegalArgumentException("Either 'id' or 'ref' must be specified for a DomainObject input type");
    }

    public void addGqlArguments(
            final ObjectAction objectAction,
            final GraphQLFieldDefinition.Builder builder,
            final TypeMapper.InputContext inputContext,
            final int upTo) {

        val parameters = objectAction.getParameters();
        val arguments = parameters.stream()
                .limit(upTo)
                .map(objectActionParameter -> gqlArgumentFor(objectActionParameter, inputContext))
                .collect(Collectors.toList());
        if (!arguments.isEmpty()) {
            builder.arguments(arguments);
        }
    }

    GraphQLArgument gqlArgumentFor(
            final ObjectActionParameter objectActionParameter,
            final TypeMapper.InputContext inputContext) {
        return objectActionParameter.isPlural()
                ? gqlArgumentFor((OneToManyActionParameter) objectActionParameter, inputContext)
                : gqlArgumentFor((OneToOneActionParameter) objectActionParameter, inputContext);
    }

    GraphQLArgument gqlArgumentFor(
            final OneToOneActionParameter oneToOneActionParameter,
            final TypeMapper.InputContext inputContext) {
        return GraphQLArgument.newArgument()
                .name(oneToOneActionParameter.getId())
                .type(context.typeMapper.inputTypeFor(oneToOneActionParameter, inputContext))
                .build();
    }

    GraphQLArgument gqlArgumentFor(
            final OneToManyActionParameter oneToManyActionParameter,
            final TypeMapper.InputContext inputContext) {
        return GraphQLArgument.newArgument()
                .name(oneToManyActionParameter.getId())
                .type(context.typeMapper.inputTypeFor(oneToManyActionParameter))
                .build();
    }

    @Override
    public ObjectSpecification getObjectSpecification() {
        return holder.getObjectSpecification();
    }

    @Override
    public ObjectAction getObjectAction() {
        return getObjectMember();
    }

    @Override
    protected void addDataFetchersForChildren() {
        if(hidden == null) {
            return;
        }
        hidden.addDataFetcher(this);
        disabled.addDataFetcher(this);
        validate.addDataFetcher(this);
        if (invoke != null) {
            invoke.addDataFetcher(this);
        }
        if (params != null) {
            params.addDataFetcher(this);
        }
    }

}
