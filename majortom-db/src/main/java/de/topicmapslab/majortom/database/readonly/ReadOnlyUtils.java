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
package de.topicmapslab.majortom.database.readonly;

import java.sql.SQLException;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.model.ISession;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * @author Sven Krosse
 * 
 */
public class ReadOnlyUtils {

	/**
	 * Internal method to read the history values
	 * 
	 * @param <T>
	 *            the type of returned values
	 * @param provider
	 *            the connection provider
	 * @param construct
	 *            the construct which is calling
	 * @param type
	 *            the argument specifies the value to fetch
	 * @return the value
	 */
	@SuppressWarnings("unchecked")
	static <T extends Object> T doReadHistoryValue(IConnectionProvider provider, IConstruct construct, TopicMapStoreParameterType type) {
		try {
			ISession session = provider.openSession();
			try {
				return (T) session.getProcessor().doReadHistory(construct, type).get(type);
			} finally {
				session.close();
			}
		} catch (SQLException e) {
			throw new TopicMapStoreException(e);
		}
	}

}
