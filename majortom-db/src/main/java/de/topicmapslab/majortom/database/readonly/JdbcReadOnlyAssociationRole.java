/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.topicmapslab.majortom.database.readonly;

import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.revision.core.ReadOnlyAssociation;
import de.topicmapslab.majortom.revision.core.ReadOnlyAssociationRole;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcReadOnlyAssociationRole extends ReadOnlyAssociationRole {

	private static final long serialVersionUID = 1L;
	private final ReadOnlyAssociation parent;
	private final IConnectionProvider provider;

	/**
	 * constructor
	 * 
	 * @param provider
	 *            the connection provider
	 * @param clone
	 *            the non-read-only clone
	 */
	public JdbcReadOnlyAssociationRole(IConnectionProvider provider, IAssociationRole clone) {
		super(clone);
		this.parent = new JdbcReadOnlyAssociation(provider, clone.getParent());
		this.provider = provider;
	}

	/**
	 * {@inheritDoc}
	 */
	public IAssociation getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getPlayer() {
		return doReadHistoryValue(TopicMapStoreParameterType.PLAYER);
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getReifier() {
		return doReadHistoryValue(TopicMapStoreParameterType.REIFICATION);
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getItemIdentifiers() {
		Set<ILocator> set = doReadHistoryValue(TopicMapStoreParameterType.ITEM_IDENTIFIER);
		Set<Locator> r = HashUtil.getHashSet();
		r.addAll(set);
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getType() {
		return (Topic) doReadHistoryValue(TopicMapStoreParameterType.TYPE);
	}

	/**
	 * Internal method to read the history values
	 * 
	 * @param <T>
	 *            the type of returned values
	 * @param type
	 *            the argument specifies the value to fetch
	 * @return the value
	 */
	@SuppressWarnings("unchecked")
	private <T extends Object> T doReadHistoryValue(TopicMapStoreParameterType type) {
		Object value = ReadOnlyUtils.doReadHistoryValue(provider, this, type);
		return (T)value;
	}

}
