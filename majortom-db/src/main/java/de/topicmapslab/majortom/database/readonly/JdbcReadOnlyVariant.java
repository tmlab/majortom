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

import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.revision.core.ReadOnlyVariant;

/**
 * @author Sven Krosse
 *
 */
public class JdbcReadOnlyVariant extends ReadOnlyVariant {

	/**
	 * @param clone
	 */
	public JdbcReadOnlyVariant(IDatatypeAware clone) {
		super(clone);
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	public IName getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator getDatatype() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	protected Object objectValue() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Topic getReifier() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Locator> getItemIdentifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScope getScopeObject() {
		// TODO Auto-generated method stub
		return null;
	}

}
