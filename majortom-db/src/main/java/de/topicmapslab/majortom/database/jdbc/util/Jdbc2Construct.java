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
package de.topicmapslab.majortom.database.jdbc.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import de.topicmapslab.majortom.core.AssociationImpl;
import de.topicmapslab.majortom.core.AssociationRoleImpl;
import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.NameImpl;
import de.topicmapslab.majortom.core.OccurrenceImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.core.TopicImpl;
import de.topicmapslab.majortom.core.VariantImpl;
import de.topicmapslab.majortom.database.store.JdbcIdentity;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class Jdbc2Construct {

	public static IAssociation toAssociation(ITopicMap topicMap, ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return new AssociationImpl(new JdbcIdentity(result.getString(column)), topicMap);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static ITopic toTopic(ITopicMap topicMap, ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return new TopicImpl(new JdbcIdentity(result.getString(column)), topicMap);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IName toName(ITopic topic, ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return new NameImpl(new JdbcIdentity(result.getString(column)), topic);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IOccurrence toOccurrence(ITopic topic, ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return new OccurrenceImpl(new JdbcIdentity(result.getString(column)), topic);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IVariant toVariant(IName name, ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return new VariantImpl(new JdbcIdentity(result.getString(column)), name);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IAssociationRole toRole(IAssociation association, ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return new AssociationRoleImpl(new JdbcIdentity(result.getString(column)), association);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IScope toScope(ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return new ScopeImpl(result.getString(column));
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static ILocator toLocator(ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return new LocatorImpl(result.getString(column));
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static Set<IAssociation> toAssociations(ITopicMap topicMap, ResultSet result, String column) throws SQLException {
		Set<IAssociation> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new AssociationImpl(new JdbcIdentity(result.getString(column)), topicMap));
		}
		result.close();
		return set;
	}

	public static Set<ITopic> toTopics(ITopicMap topicMap, ResultSet result, String column) throws SQLException {
		Set<ITopic> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new TopicImpl(new JdbcIdentity(result.getString(column)), topicMap));
		}
		result.close();
		return set;
	}

	public static Set<IName> toNames(ITopic topic, ResultSet result, String column) throws SQLException {
		Set<IName> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new NameImpl(new JdbcIdentity(result.getString(column)), topic));
		}
		result.close();
		return set;
	}

	public static Set<IName> toNames(ITopicMap topicMap, ResultSet result, String column, String parentColumn) throws SQLException {
		Set<IName> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new NameImpl(new JdbcIdentity(result.getString(column)), new TopicImpl(new JdbcIdentity(result.getString(parentColumn)), topicMap)));
		}
		result.close();
		return set;
	}

	public static Set<IOccurrence> toOccurrences(ITopic topic, ResultSet result, String column) throws SQLException {
		Set<IOccurrence> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new OccurrenceImpl(new JdbcIdentity(result.getString(column)), topic));
		}
		result.close();
		return set;
	}

	public static Set<IOccurrence> toOccurrences(ITopicMap topicMap, ResultSet result, String column, String parentColumn) throws SQLException {
		Set<IOccurrence> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new OccurrenceImpl(new JdbcIdentity(result.getString(column)), new TopicImpl(new JdbcIdentity(result.getString(parentColumn)), topicMap)));
		}
		result.close();
		return set;
	}

	public static Set<IVariant> toVariants(IName name, ResultSet result, String column) throws SQLException {
		Set<IVariant> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new VariantImpl(new JdbcIdentity(result.getString(column)), name));
		}
		result.close();
		return set;
	}

	public static Set<IAssociationRole> toRoles(IAssociation association, ResultSet result, String column) throws SQLException {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new AssociationRoleImpl(new JdbcIdentity(result.getString(column)), association));
		}
		result.close();
		return set;
	}

	public static Set<IAssociationRole> toRoles(ITopicMap topicMap, ResultSet result, String column, String parentIdColumn) throws SQLException {
		Set<IAssociationRole> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new AssociationRoleImpl(new JdbcIdentity(result.getString(column)), new AssociationImpl(new JdbcIdentity(result.getString(parentIdColumn)),
					topicMap)));
		}
		result.close();
		return set;
	}

	public static Set<ILocator> toLocators(ResultSet result, String column) throws SQLException {
		Set<ILocator> set = HashUtil.getHashSet();
		while (result.next()) {
			set.add(new LocatorImpl(result.getString(column)));
		}
		result.close();
		return set;
	}

}
