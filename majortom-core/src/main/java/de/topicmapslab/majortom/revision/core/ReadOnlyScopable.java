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
package de.topicmapslab.majortom.revision.core;

import java.util.Set;

import org.tmapi.core.ModelConstraintException;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * Read only implementation of {@link IScopable}
 * 
 * @author Sven Krosse
 * 
 */
public abstract class ReadOnlyScopable extends ReadOnlyReifiable implements IScopable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7905827035691626710L;

	/**
	 * constructor
	 * 
	 * @param clone
	 *            the construct to clone
	 */
	public ReadOnlyScopable(IScopable clone) {
		super((IReifiable) clone);
	}

	/**
	 * {@inheritDoc}
	 */
	public void addTheme(Topic arg0) throws ModelConstraintException {
		throw new UnsupportedOperationException("Construct is read only.");
	}

	/**
	 * {@inheritDoc}
	 */
	public Set<Topic> getScope() {
		Set<Topic> themes = HashUtil.getHashSet();
		themes.addAll(getScopeObject().getThemes());
		return themes;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeTheme(Topic arg0) {
		throw new UnsupportedOperationException("Construct is read only.");
	}

}
