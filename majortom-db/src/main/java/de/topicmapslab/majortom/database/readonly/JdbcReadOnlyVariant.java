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

import java.sql.SQLException;
import java.util.Set;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.database.jdbc.model.IQueryProcessor;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;
import de.topicmapslab.majortom.revision.core.ReadOnlyName;
import de.topicmapslab.majortom.revision.core.ReadOnlyVariant;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class JdbcReadOnlyVariant extends ReadOnlyVariant {

	private final ReadOnlyName parent;
	private final IQueryProcessor processor;

	/**
	 * 
	 * @param processor
	 * @param clone
	 */
	public JdbcReadOnlyVariant(IQueryProcessor processor, IVariant clone) {
		super(clone);
		this.parent = new JdbcReadOnlyName(processor, clone.getParent());
		this.processor = processor;
	}

	/**
	 * {@inheritDoc}
	 */
	public IName getParent() {
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator getDatatype() {
		return doReadHistoryValue(TopicMapStoreParameterType.DATATYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object objectValue() {
		return doReadHistoryValue(TopicMapStoreParameterType.VALUE);
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
	public IScope getScopeObject() {
		return doReadHistoryValue(TopicMapStoreParameterType.SCOPE);
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
		try {
			return (T) processor.doReadHistory(this, type).get(type);
		} catch (SQLException e) {
			throw new TopicMapStoreException(e);
		}
	}

}
