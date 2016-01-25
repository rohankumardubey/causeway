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

package org.apache.isis.viewer.wicket.model.models;

import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.wicket.Component;
import org.apache.wicket.model.LoadableDetachableModel;

import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.metamodel.adapter.mgr.AdapterManager;
import org.apache.isis.core.runtime.system.context.IsisContext;
import org.apache.isis.core.runtime.system.persistence.PersistenceSession;
import org.apache.isis.viewer.wicket.model.hints.UiHintContainer;
import org.apache.isis.viewer.wicket.model.util.ScopedSessionAttribute;

/**
 * Adapter for {@link LoadableDetachableModel}s, providing access to some of the
 * Isis' dependencies.
 */
public abstract class ModelAbstract<T> extends LoadableDetachableModel<T> implements UiHintContainer {

    private static final long serialVersionUID = 1L;

    public ModelAbstract() {
    }

    public ModelAbstract(final T t) {
        super(t);
    }


    // //////////////////////////////////////////////////////////
    // Hint support
    // //////////////////////////////////////////////////////////

    protected final Map<String, ScopedSessionAttribute> scopedSessionAttributeByName = Maps.newHashMap();
    private final Map<String, String> hints = Maps.newTreeMap();

    public String getHint(final Component component, final String key) {
        if(component == null) {
            return null;
        }
        String hintKey = hintPathFor(component) + "-" + key;
        final ScopedSessionAttribute<String> scopedSessionAttribute = scopedSessionAttributeByName.get(hintKey);
        if(scopedSessionAttribute != null) {
            final String sessionHint = scopedSessionAttribute.get();
            if(sessionHint != null) {
                return sessionHint;
            }
        }
        return hints.get(hintKey);
    }

    @Override
    public void setHint(Component component, String key, String value) {
        if(component == null) {
            return;
        }
        final String scopeKey = hintPathFor(component);
        String hintKey = scopeKey + "-" + key;
        if(value != null) {
            hints.put(hintKey, value);
        } else {
            hints.remove(hintKey);
        }
        doSetHint(scopeKey, key, value);
    }

    /**
     * Optional hook, eg to also store the hint in the session.
     */
    protected void doSetHint(final String scopeKey, final String attribute, final String value) {

    }

    @Override
    public void clearHint(Component component, String key) {
        setHint(component, key, null);
    }

    private static String hintPathFor(Component component)
    {
        return Util.hintPathFor(component);
    }

    protected Map<String, String> getHints() {
        return hints;
    }

    // //////////////////////////////////////////////////////////////
    // Dependencies
    // //////////////////////////////////////////////////////////////

    protected AuthenticationSession getAuthenticationSession() {
        return IsisContext.getAuthenticationSession();
    }

    protected PersistenceSession getPersistenceSession() {
        return IsisContext.getPersistenceSession();
    }

    protected AdapterManager getAdapterManager() {
        return getPersistenceSession();
    }

}
