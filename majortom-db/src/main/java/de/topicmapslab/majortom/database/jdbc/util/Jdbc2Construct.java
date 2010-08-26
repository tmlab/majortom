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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.topicmapslab.majortom.core.LocatorImpl;
import de.topicmapslab.majortom.core.ScopeImpl;
import de.topicmapslab.majortom.database.jdbc.model.IQueryProcessor;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyAssociation;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyAssociationRole;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyName;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyOccurrence;
import de.topicmapslab.majortom.database.readonly.JdbcReadOnlyTopic;
import de.topicmapslab.majortom.database.store.JdbcIdentity;
import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.revision.RevisionChangeImpl;
import de.topicmapslab.majortom.revision.RevisionImpl;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class Jdbc2Construct {

	public static IAssociation toAssociation(ITopicMap topicMap,
			ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return topicMap.getStore().getConstructFactory()
						.newAssociation(
								new JdbcIdentity(result.getString(column)),
								topicMap);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static ITopic toTopic(ITopicMap topicMap, ResultSet result,
			String column) throws SQLException {
		try {
			if (result.next()) {
				// if id is null the topic does not exists e.g. in case no reifier 
				String id = result.getString(column);
				if (id==null)
					return null;
				
				return topicMap.getStore().getConstructFactory().newTopic(
						new JdbcIdentity(id), topicMap);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IName toName(ITopic topic, ResultSet result, String column)
			throws SQLException {
		try {
			if (result.next()) {
				return topic.getTopicMap().getStore().getConstructFactory()
						.newName(new JdbcIdentity(result.getString(column)),
								topic);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IOccurrence toOccurrence(ITopic topic, ResultSet result,
			String column) throws SQLException {
		try {
			if (result.next()) {
				return topic.getTopicMap().getStore().getConstructFactory()
						.newOccurrence(
								new JdbcIdentity(result.getString(column)),
								topic);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IVariant toVariant(IName name, ResultSet result, String column)
			throws SQLException {
		try {
			if (result.next()) {
				return name.getTopicMap().getStore().getConstructFactory()
						.newVariant(new JdbcIdentity(result.getString(column)),
								name);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IAssociationRole toRole(IAssociation association,
			ResultSet result, String column) throws SQLException {
		try {
			if (result.next()) {
				return association.getTopicMap().getStore()
						.getConstructFactory().newAssociationRole(
								new JdbcIdentity(result.getString(column)),
								association);
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static IScope toScope(ResultSet result, String column)
			throws SQLException {
		try {
			if (result.next()) {
				return new ScopeImpl(result.getString(column));
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static ILocator toLocator(ResultSet result, String column)
			throws SQLException {
		try {
			if (result.next()) {
				return new LocatorImpl(result.getString(column));
			}
			return null;
		} finally {
			result.close();
		}
	}

	public static List<IConstruct> toConstructs(ITopicMap topicMap,
			ResultSet result) throws SQLException {
		List<IConstruct> list = HashUtil.getList();
		while (result.next()) {
			if ("t".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory().newTopic(
						new JdbcIdentity(result.getString("id")), topicMap));
			} else if ("o".equalsIgnoreCase(result.getString("type"))) {
				list
						.add(topicMap
								.getStore()
								.getConstructFactory()
								.newOccurrence(
										new JdbcIdentity(result.getString("id")),
										topicMap
												.getStore()
												.getConstructFactory()
												.newTopic(
														new JdbcIdentity(
																result
																		.getString("id_parent")),
														topicMap)));
			} else if ("n".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory().newName(
						new JdbcIdentity(result.getString("id")),
						topicMap.getStore().getConstructFactory()
								.newTopic(
										new JdbcIdentity(result
												.getString("id_parent")),
										topicMap)));
			} else if ("v".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory().newVariant(
						new JdbcIdentity(result.getString(1)),
						topicMap.getStore().getConstructFactory().newName(
								new JdbcIdentity(result.getString(2)),
								topicMap.getStore().getConstructFactory()
										.newTopic(
												new JdbcIdentity(result
														.getString(3)),
												topicMap))));
			} else if ("a".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory()
						.newAssociation(
								new JdbcIdentity(result.getString("id")),
								topicMap));
			} else if ("r".equalsIgnoreCase(result.getString("type"))) {
				list
						.add(topicMap
								.getStore()
								.getConstructFactory()
								.newAssociationRole(
										new JdbcIdentity(result.getString("id")),
										topicMap
												.getStore()
												.getConstructFactory()
												.newAssociation(
														new JdbcIdentity(
																result
																		.getString("id_parent")),
														topicMap)));
			} else if ("tm".equalsIgnoreCase(result.getString("type"))) {
				String id = result.getString("id");
				if (id.equalsIgnoreCase(topicMap.getId())) {
					list.add(topicMap);
				}
			} else {
				throw new TopicMapStoreException(
						"Unknown characteristics type '"
								+ result.getString("type") + "'.");
			}
		}
		result.close();
		return list;
	}

	public static List<IAssociation> toAssociations(ITopicMap topicMap,
			ResultSet result, String column) throws SQLException {
		List<IAssociation> set = HashUtil.getList();
		while (result.next()) {
			set.add(topicMap.getStore().getConstructFactory().newAssociation(
					new JdbcIdentity(result.getString(column)), topicMap));
		}
		result.close();
		return set;
	}

	public static List<ITopic> toTopics(ITopicMap topicMap, ResultSet result,
			String column) throws SQLException {
		List<ITopic> list = HashUtil.getList();
		while (result.next()) {
			list.add(topicMap.getStore().getConstructFactory().newTopic(
					new JdbcIdentity(result.getString(column)), topicMap));
		}
		result.close();
		return list;
	}

	public static List<ICharacteristics> toCharacteristics(ITopicMap topicMap,
			ResultSet result) throws SQLException {
		List<ICharacteristics> list = HashUtil.getList();
		while (result.next()) {
			if ("n".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory().newName(
						new JdbcIdentity(result.getString("id")),
						topicMap.getStore().getConstructFactory()
								.newTopic(
										new JdbcIdentity(result
												.getString("id_parent")),
										topicMap)));
			} else if ("o".equalsIgnoreCase(result.getString("type"))) {
				list
						.add(topicMap
								.getStore()
								.getConstructFactory()
								.newOccurrence(
										new JdbcIdentity(result.getString("id")),
										topicMap
												.getStore()
												.getConstructFactory()
												.newTopic(
														new JdbcIdentity(
																result
																		.getString("id_parent")),
														topicMap)));
			} else {
				throw new TopicMapStoreException(
						"Unknown characteristics type '"
								+ result.getString("type") + "'.");
			}
		}
		result.close();
		return list;
	}

	public static List<IDatatypeAware> toDatatypeAwares(ITopicMap topicMap,
			ResultSet result) throws SQLException {
		List<IDatatypeAware> list = HashUtil.getList();
		while (result.next()) {
			if ("v".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory().newVariant(
						new JdbcIdentity(result.getString(1)),
						topicMap.getStore().getConstructFactory().newName(
								new JdbcIdentity(result.getString(2)),
								topicMap.getStore().getConstructFactory()
										.newTopic(
												new JdbcIdentity(result
														.getString(3)),
												topicMap))));
			} else if ("o".equalsIgnoreCase(result.getString("type"))) {
				list
						.add(topicMap
								.getStore()
								.getConstructFactory()
								.newOccurrence(
										new JdbcIdentity(result.getString("id")),
										topicMap
												.getStore()
												.getConstructFactory()
												.newTopic(
														new JdbcIdentity(
																result
																		.getString("id_parent")),
														topicMap)));
			} else {
				throw new TopicMapStoreException(
						"Unknown characteristics type '"
								+ result.getString("type") + "'.");
			}
		}
		result.close();
		return list;
	}

	public static List<IName> toNames(ITopic topic, ResultSet result,
			String column) throws SQLException {
		List<IName> list = HashUtil.getList();
		while (result.next()) {
			list
					.add(topic.getTopicMap().getStore().getConstructFactory()
							.newName(
									new JdbcIdentity(result.getString(column)),
									topic));
		}
		result.close();
		return list;
	}

	public static List<IName> toNames(ITopicMap topicMap, ResultSet result,
			String column, String parentColumn) throws SQLException {
		List<IName> list = HashUtil.getList();
		while (result.next()) {
			list.add(topicMap.getStore().getConstructFactory().newName(
					new JdbcIdentity(result.getString(column)),
					topicMap.getStore().getConstructFactory().newTopic(
							new JdbcIdentity(result.getString(parentColumn)),
							topicMap)));
		}
		result.close();
		return list;
	}

	public static List<IOccurrence> toOccurrences(ITopic topic,
			ResultSet result, String column) throws SQLException {
		List<IOccurrence> list = HashUtil.getList();
		while (result.next()) {
			list.add(topic.getParent().getStore().getConstructFactory()
					.newOccurrence(new JdbcIdentity(result.getString(column)),
							topic));
		}
		result.close();
		return list;
	}

	public static List<IOccurrence> toOccurrences(ITopicMap topicMap,
			ResultSet result, String column, String parentColumn)
			throws SQLException {
		List<IOccurrence> list = HashUtil.getList();
		while (result.next()) {
			list.add(topicMap.getStore().getConstructFactory().newOccurrence(
					new JdbcIdentity(result.getString(column)),
					topicMap.getStore().getConstructFactory().newTopic(
							new JdbcIdentity(result.getString(parentColumn)),
							topicMap)));
		}
		result.close();
		return list;
	}

	public static List<IVariant> toVariants(IName name, ResultSet result,
			String column) throws SQLException {
		List<IVariant> list = HashUtil.getList();
		while (result.next()) {
			list.add(name.getTopicMap().getStore().getConstructFactory()
					.newVariant(new JdbcIdentity(result.getString(column)),
							name));
		}
		result.close();
		return list;
	}

	public static List<IVariant> toVariants(ITopicMap topicMap, ResultSet result)
			throws SQLException {
		List<IVariant> list = HashUtil.getList();
		while (result.next()) {
			list.add(topicMap.getStore().getConstructFactory().newVariant(
					new JdbcIdentity(result.getString(1)),
					topicMap.getStore().getConstructFactory().newName(
							new JdbcIdentity(result.getString(2)),
							topicMap.getStore().getConstructFactory().newTopic(
									new JdbcIdentity(result.getString(3)),
									topicMap))));
		}
		result.close();
		return list;
	}

	public static List<IVariant> toVariants(ITopicMap topicMap,
			ResultSet result, String column, String nameIdColumn,
			String topicIdColumn) throws SQLException {
		List<IVariant> list = HashUtil.getList();
		while (result.next()) {
			list.add(topicMap.getStore().getConstructFactory().newVariant(
					new JdbcIdentity(result.getString(column)),
					topicMap.getStore().getConstructFactory().newName(
							new JdbcIdentity(result.getString(nameIdColumn)),
							topicMap.getStore().getConstructFactory().newTopic(
									new JdbcIdentity(result
											.getString(topicIdColumn)),
									topicMap))));
		}
		result.close();
		return list;
	}

	public static List<IAssociationRole> toRoles(IAssociation association,
			ResultSet result, String column) throws SQLException {
		List<IAssociationRole> list = HashUtil.getList();
		while (result.next()) {
			list.add(association.getTopicMap().getStore().getConstructFactory()
					.newAssociationRole(
							new JdbcIdentity(result.getString(column)),
							association));
		}
		result.close();
		return list;
	}

	public static List<IAssociationRole> toRoles(ITopicMap topicMap,
			ResultSet result, String column, String parentIdColumn)
			throws SQLException {
		List<IAssociationRole> list = HashUtil.getList();
		while (result.next()) {
			list
					.add(topicMap
							.getStore()
							.getConstructFactory()
							.newAssociationRole(
									new JdbcIdentity(result.getString(column)),
									topicMap
											.getStore()
											.getConstructFactory()
											.newAssociation(
													new JdbcIdentity(
															result
																	.getString(parentIdColumn)),
													topicMap)));
		}
		result.close();
		return list;
	}

	public static List<IScopable> toScopables(ITopicMap topicMap,
			ResultSet result) throws SQLException {
		List<IScopable> list = HashUtil.getList();
		while (result.next()) {
			if ("v".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory().newVariant(
						new JdbcIdentity(result.getString(1)),
						topicMap.getStore().getConstructFactory().newName(
								new JdbcIdentity(result.getString(2)),
								topicMap.getStore().getConstructFactory()
										.newTopic(
												new JdbcIdentity(result
														.getString(3)),
												topicMap))));
			} else if ("o".equalsIgnoreCase(result.getString("type"))) {
				list
						.add(topicMap
								.getStore()
								.getConstructFactory()
								.newOccurrence(
										new JdbcIdentity(result.getString("id")),
										topicMap
												.getStore()
												.getConstructFactory()
												.newTopic(
														new JdbcIdentity(
																result
																		.getString("id_parent")),
														topicMap)));
			} else if ("n".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory().newName(
						new JdbcIdentity(result.getString("id")),
						topicMap.getStore().getConstructFactory()
								.newTopic(
										new JdbcIdentity(result
												.getString("id_parent")),
										topicMap)));
			} else if ("a".equalsIgnoreCase(result.getString("type"))) {
				list.add(topicMap.getStore().getConstructFactory()
						.newAssociation(
								new JdbcIdentity(result.getString("id")),
								topicMap));
			} else {
				throw new TopicMapStoreException(
						"Unknown characteristics type '"
								+ result.getString("type") + "'.");
			}
		}
		result.close();
		return list;
	}

	public static List<ILocator> toLocators(ResultSet result, String column)
			throws SQLException {
		List<ILocator> list = HashUtil.getList();
		while (result.next()) {
			list.add(new LocatorImpl(result.getString(column)));
		}
		result.close();
		return list;
	}

	public static List<IScope> toScopes(ITopicMap topicMap, ResultSet result)
			throws SQLException {
		List<IScope> list = HashUtil.getList();
		while (result.next()) {
			String id = result.getString("id");
			Long[] themeIds = (Long[]) result.getArray("themes").getArray();
			Set<ITopic> themes = HashUtil.getHashSet();
			for (Long themeId : themeIds) {
				themes.add(topicMap.getStore().getConstructFactory().newTopic(
						new JdbcIdentity(Long.toString(themeId)), topicMap));
			}
			list.add(new ScopeImpl(id, themes));
		}
		result.close();
		return list;
	}

	public static Changeset toChangeSet(IQueryProcessor processor,
			ITopicMap topicMap, ResultSet rs, IRevision parent)
			throws SQLException {
		List<RevisionChangeData> list = new LinkedList<RevisionChangeData>();
		while (rs.next()) {
			RevisionChangeData data = new RevisionChangeData();
			data.revision = parent;
			data.idNotifier = rs.getLong("id_notifier");
			data.type = TopicMapEventType.valueOf(rs.getString("type"));
			data.newValue = rs.getString("newValue");
			data.oldValue = rs.getString("oldValue");
			list.add(data);
		}
		rs.close();
		return toChangeSet(processor, topicMap, list);
	}

	public static Changeset toChangeSet(IQueryProcessor processor,
			ITopicMap topicMap, ResultSet rs) throws SQLException {
		List<RevisionChangeData> list = new LinkedList<RevisionChangeData>();
		while (rs.next()) {
			RevisionChangeData data = new RevisionChangeData();
			data.revision = new RevisionImpl(processor.getConnectionProvider()
					.getTopicMapStore(), rs.getLong("id_revision")) {
			};
			data.idNotifier = rs.getLong("id_notifier");
			data.type = TopicMapEventType.valueOf(rs.getString("type"));
			data.newValue = rs.getString("newValue");
			data.oldValue = rs.getString("oldValue");
			list.add(data);
		}
		rs.close();
		return toChangeSet(processor, topicMap, list);
	}

	private static Changeset toChangeSet(IQueryProcessor processor,
			ITopicMap topicMap, List<RevisionChangeData> list)
			throws SQLException {

		Changeset changeset = new Changeset();
		for (RevisionChangeData data : list) {
			IConstruct notifier = null;
			Object oldValue = null, newValue = null;
			switch (data.type) {
			case ASSOCIATION_ADDED: {
				notifier = topicMap;
				newValue = new JdbcReadOnlyAssociation(processor, topicMap
						.getStore().getConstructFactory().newAssociation(
								new JdbcIdentity(data.newValue), topicMap));
			}
				break;
			case ASSOCIATION_REMOVED: {
				notifier = topicMap;
				oldValue = new JdbcReadOnlyAssociation(processor, topicMap
						.getStore().getConstructFactory().newAssociation(
								new JdbcIdentity(data.oldValue), topicMap));
			}
				break;
			case DATATYPE_SET: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				newValue = data.newValue;
				oldValue = data.oldValue;
			}
				break;
			case ITEM_IDENTIFIER_ADDED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				newValue = data.newValue;
			}
				break;
			case ITEM_IDENTIFIER_REMOVED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				oldValue = data.oldValue;
			}
				break;
			case MERGE: {
				notifier = topicMap;
				newValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.newValue), topicMap));
				oldValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.oldValue), topicMap));
			}
				break;
			case NAME_ADDED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				newValue = new JdbcReadOnlyName(processor, topicMap.getStore()
						.getConstructFactory().newName(
								new JdbcIdentity(data.newValue),
								(ITopic) notifier));
			}
				break;
			case NAME_REMOVED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				oldValue = new JdbcReadOnlyName(processor, topicMap.getStore()
						.getConstructFactory().newName(
								new JdbcIdentity(data.oldValue),
								(ITopic) notifier));
			}
				break;
			case OCCURRENCE_ADDED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				newValue = new JdbcReadOnlyOccurrence(processor, topicMap
						.getStore().getConstructFactory().newOccurrence(
								new JdbcIdentity(data.newValue),
								(ITopic) notifier));
			}
				break;
			case OCCURRENCE_REMOVED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				oldValue = new JdbcReadOnlyOccurrence(processor, topicMap
						.getStore().getConstructFactory().newOccurrence(
								new JdbcIdentity(data.oldValue),
								(ITopic) notifier));
			}
				break;
			case PLAYER_MODIFIED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				newValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.newValue), topicMap));
				if (data.oldValue != null) {
					oldValue = new JdbcReadOnlyTopic(processor, topicMap
							.getStore().getConstructFactory().newTopic(
									new JdbcIdentity(data.oldValue), topicMap));
				}
			}
				break;
			case REIFIER_SET: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				newValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.newValue), topicMap));
				if (data.oldValue != null) {
					oldValue = new JdbcReadOnlyTopic(processor, topicMap
							.getStore().getConstructFactory().newTopic(
									new JdbcIdentity(data.oldValue), topicMap));
				}
			}
				break;
			case ROLE_ADDED: {
				notifier = new JdbcReadOnlyAssociation(processor,
						topicMap.getStore().getConstructFactory()
								.newAssociation(
										new JdbcIdentity(Long
												.toString(data.idNotifier)),
										topicMap));
				newValue = new JdbcReadOnlyAssociationRole(processor, topicMap
						.getStore().getConstructFactory().newAssociationRole(
								new JdbcIdentity(data.newValue),
								(IAssociation) notifier));
			}
				break;
			case ROLE_REMOVED: {
				notifier = new JdbcReadOnlyAssociation(processor,
						topicMap.getStore().getConstructFactory()
								.newAssociation(
										new JdbcIdentity(Long
												.toString(data.idNotifier)),
										topicMap));
				oldValue = new JdbcReadOnlyAssociationRole(processor, topicMap
						.getStore().getConstructFactory().newAssociationRole(
								new JdbcIdentity(data.oldValue),
								(IAssociation) notifier));
			}
				break;
			case SCOPE_MODIFIED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				newValue = new ScopeImpl(data.newValue, processor.doReadThemes(
						topicMap, Long.parseLong(data.newValue)));
				if (data.oldValue != null) {
					oldValue = new ScopeImpl(data.oldValue, processor
							.doReadThemes(topicMap, Long
									.parseLong(data.oldValue)));
				}
			}
				break;
			case SUBJECT_LOCATOR_ADDED:
			case SUBJECT_IDENTIFIER_ADDED: {
				notifier = new JdbcReadOnlyTopic(processor,
						topicMap.getStore().getConstructFactory()
								.newTopic(
										new JdbcIdentity(Long
												.toString(data.idNotifier)),
										topicMap));
				newValue = data.newValue;
			}
				break;
			case SUBJECT_IDENTIFIER_REMOVED:
			case SUBJECT_LOCATOR_REMOVED: {
				notifier = new JdbcReadOnlyTopic(processor,
						topicMap.getStore().getConstructFactory()
								.newTopic(
										new JdbcIdentity(Long
												.toString(data.idNotifier)),
										topicMap));
				oldValue = data.oldValue;
			}
				break;
			case TYPE_ADDED:
			case SUPERTYPE_ADDED: {
				notifier = new JdbcReadOnlyTopic(processor,
						topicMap.getStore().getConstructFactory()
								.newTopic(
										new JdbcIdentity(Long
												.toString(data.idNotifier)),
										topicMap));
				newValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.newValue), topicMap));
			}
				break;
			case TYPE_REMOVED:
			case SUPERTYPE_REMOVED: {
				notifier = new JdbcReadOnlyTopic(processor,
						topicMap.getStore().getConstructFactory()
								.newTopic(
										new JdbcIdentity(Long
												.toString(data.idNotifier)),
										topicMap));
				oldValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.oldValue), topicMap));
			}
				break;
			case TOPIC_ADDED: {
				notifier = topicMap;
				newValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.newValue), topicMap));
			}
				break;
			case TOPIC_REMOVED: {
				notifier = topicMap;
				oldValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.oldValue), topicMap));
			}
				break;
			case TYPE_SET: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				if (data.oldValue != null) {
					oldValue = new JdbcReadOnlyTopic(processor, topicMap
							.getStore().getConstructFactory().newTopic(
									new JdbcIdentity(data.oldValue), topicMap));
				}
				newValue = new JdbcReadOnlyTopic(processor, topicMap.getStore()
						.getConstructFactory().newTopic(
								new JdbcIdentity(data.newValue), topicMap));
			}
				break;
			case VALUE_MODIFIED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				oldValue = data.oldValue;
				newValue = data.newValue;
			}
				break;
			case VARIANT_ADDED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				newValue = processor.doReadConstruct(topicMap, data.newValue,
						true);
			}
				break;
			case VARIANT_REMOVED: {
				notifier = processor.doReadConstruct(topicMap, Long
						.toString(data.idNotifier), true);
				oldValue = processor.doReadConstruct(topicMap, data.oldValue,
						true);
			}
				break;
			}
			changeset.add(new RevisionChangeImpl(data.revision, data.type,
					notifier, newValue, oldValue));
		}
		return changeset;
	}

}

class RevisionChangeData {
	IRevision revision;
	long idNotifier;
	TopicMapEventType type;
	String newValue;
	String oldValue;
}
