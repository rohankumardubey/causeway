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
package org.apache.causeway.persistence.jdo.spring.support;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * Proxy that implements the {@link javax.jdo.PersistenceManager} interface,
 * delegating to a thread-bound PersistenceManager on each invocation -
 * as defined by the JDO 3.0 specification. This class makes such a standard
 * JDO PersistenceManager proxy available for bean references.
 *
 * <p>The main advantage of this proxy is that it allows DAOs to work with a
 * plain JDO PersistenceManager reference in JDO 3.0 style
 * (see {@link javax.jdo.PersistenceManagerFactory#getPersistenceManagerProxy()}),
 * exposing the exact behavior that the target JDO provider implements.
 *
 * @see SpringPersistenceManagerProxyBean
 * @see javax.jdo.PersistenceManagerFactory#getPersistenceManagerProxy()
 */
public class StandardPersistenceManagerProxyBean implements FactoryBean<PersistenceManager> {

	private PersistenceManager proxy;

	/**
	 * Set the target JDO PersistenceManagerFactory that this proxy should
	 * delegate to. This should be the raw PersistenceManagerFactory, as
	 * accessed by JdoTransactionManager.
	 * @see org.apache.causeway.persistence.jdo.spring.integration.JdoTransactionManager
	 */
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf) {
		Assert.notNull(pmf, "PersistenceManagerFactory must not be null");
		this.proxy = pmf.getPersistenceManagerProxy();
	}

	@Override
	public PersistenceManager getObject() {
		return this.proxy;
	}

	@Override
	public Class<? extends PersistenceManager> getObjectType() {
		return (this.proxy != null ? this.proxy.getClass() : PersistenceManager.class);
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
