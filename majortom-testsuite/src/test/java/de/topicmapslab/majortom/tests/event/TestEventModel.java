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
package de.topicmapslab.majortom.tests.event;

import static de.topicmapslab.majortom.model.event.TopicMapEventType.ASSOCIATION_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.CONSTRUCT_REMOVED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.ITEM_IDENTIFIER_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.ITEM_IDENTIFIER_REMOVED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.MERGE;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.NAME_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.OCCURRENCE_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.PLAYER_MODIFIED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.REIFIER_SET;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.ROLE_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.SCOPE_MODIFIED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.SUBJECT_IDENTIFIER_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.SUBJECT_IDENTIFIER_REMOVED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.SUBJECT_LOCATOR_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.SUBJECT_LOCATOR_REMOVED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.SUPERTYPE_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.SUPERTYPE_REMOVED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.TOPIC_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.TYPE_ADDED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.TYPE_REMOVED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.TYPE_SET;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.VALUE_MODIFIED;
import static de.topicmapslab.majortom.model.event.TopicMapEventType.VARIANT_ADDED;

import java.util.Set;

import org.tmapi.core.Association;
import org.tmapi.core.Construct;
import org.tmapi.core.DatatypeAware;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Reifiable;
import org.tmapi.core.Role;
import org.tmapi.core.Scoped;
import org.tmapi.core.Topic;
import org.tmapi.core.Typed;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.event.ITopicMapListener;
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.FeatureStrings;
import de.topicmapslab.majortom.util.HashUtil;

/**
 * @author Sven Krosse
 * 
 */
public class TestEventModel extends MaJorToMTestCase {

	public void testEventTopicAdded() throws Exception {

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(TOPIC_ADDED, event);
					assertEquals(topicMap, notifier);
					assertTrue(newValue instanceof ITopic);
					assertEquals(topicMap.getTopics().iterator().next(), newValue);
					assertNull(oldValue);
				}
				checked = true;
			}
		});

		createTopic();
	}

	public void testEventTypeAdded() throws Exception {

		final ITopic topic = createTopic();
		final ITopic type = createTopic();

		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TYPEHIERARCHY_AS_ASSOCIATION, false);

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(TYPE_ADDED, event);
					assertEquals(topic, notifier);
					assertTrue(newValue instanceof ITopic);
					assertEquals(type, newValue);
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		topic.addType(type);
	}

	public void testEventSupertypeAdded() throws Exception {
		final ITopic topic = createTopic();
		final ITopic type = createTopic();

		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TYPEHIERARCHY_AS_ASSOCIATION, false);

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(SUPERTYPE_ADDED, event);
					assertEquals(topic, notifier);
					assertTrue(newValue instanceof ITopic);
					assertEquals(type, newValue);
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		topic.addSupertype(type);
	}

	public void testEventSubjectIdentifierAdded() throws Exception {
		final ITopic topic = createTopic();
		final Locator locator = createLoctor("http://psi.example.org/locator");

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(SUBJECT_IDENTIFIER_ADDED, event);
					assertEquals(topic, notifier);
					assertTrue(newValue instanceof ILocator);
					assertEquals(locator, newValue);
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		topic.addSubjectIdentifier(locator);
	}

	public void testEventSubjectLocatorAdded() throws Exception {
		final ITopic topic = createTopic();
		final Locator locator = createLoctor("http://psi.example.org/locator");

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(SUBJECT_LOCATOR_ADDED, event);
					assertEquals(topic, notifier);
					assertTrue(newValue instanceof ILocator);
					assertEquals(locator, newValue);
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		topic.addSubjectLocator(locator);
	}

	public void testEventItemIdentifierAdded() throws Exception {
		Locator itemIdentifier = createLoctor("http://psi.example.org/locator");

		// for topic
		ITopic topic = createTopic();
		_testEventItemIdentifierAdded(topic, itemIdentifier);
		// for name
		Name name = topic.createName("Name", new Topic[0]);
		_testEventItemIdentifierAdded(name, itemIdentifier);
		// for variant
		Variant variant = name.createVariant("Variant", createTopic());
		_testEventItemIdentifierAdded(variant, itemIdentifier);
		// for occurrence
		Occurrence occurrence = topic.createOccurrence(createTopic(), "Occurrence", new Topic[0]);
		_testEventItemIdentifierAdded(occurrence, itemIdentifier);
		// for association
		Association association = createAssociation(createTopic());
		_testEventItemIdentifierAdded(association, itemIdentifier);
		// for role
		Role role = association.createRole(createTopic(), createTopic());
		_testEventItemIdentifierAdded(role, itemIdentifier);
		// for topic map
		_testEventItemIdentifierAdded(topicMap, itemIdentifier);
	}

	public void _testEventItemIdentifierAdded(final Construct construct, final Locator itemIdentifier) throws Exception {
		ITopicMapListener listener = new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(ITEM_IDENTIFIER_ADDED, event);
					assertEquals(construct, notifier);
					assertTrue(newValue instanceof ILocator);
					assertEquals(itemIdentifier, newValue);
					assertNull(oldValue);
				}
				checked = true;
			}
		};
		topicMap.addTopicMapListener(listener);
		construct.addItemIdentifier(itemIdentifier);
		topicMap.removeTopicMapListener(listener);
		construct.removeItemIdentifier(itemIdentifier);
	}

	public void testEventScopeModified() throws Exception {
		ITopic type = createTopic();

		final Set<Topic> themes = HashUtil.getHashSet();
		themes.add(createTopic());
		// for association
		_testEventScopeModified(topicMap.createAssociation(type, themes), themes);
		// for name
		_testEventScopeModified(createTopic().createName("Name", themes), themes);
		// for occurrence
		_testEventScopeModified(createTopic().createOccurrence(type, "Occurrence", themes), themes);
		// for variant
		_testEventScopeModified(createTopic().createName("Name", new Topic[0]).createVariant("Variant", themes), themes);
	}

	public void _testEventScopeModified(final Scoped scoped, final Set<Topic> themes) throws Exception {
		final ITopic theme = createTopic();

		ITopicMapListener listener = new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(SCOPE_MODIFIED, event);
					assertEquals(scoped, notifier);
					assertTrue(newValue instanceof IScope);
					assertTrue(((IScope) newValue).getThemes().containsAll(themes));
					assertTrue(((IScope) newValue).getThemes().contains(theme));
					assertEquals(themes.size() + 1, ((IScope) newValue).getThemes().size());
					assertNotNull(oldValue);
					assertTrue(oldValue instanceof IScope);
					assertTrue(((IScope) oldValue).getThemes().containsAll(themes));
					assertEquals(themes.size(), ((IScope) oldValue).getThemes().size());
				}
				checked = true;
			}
		};
		topicMap.addTopicMapListener(listener);
		scoped.addTheme(theme);
		topicMap.removeTopicMapListener(listener);

		listener = new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(SCOPE_MODIFIED, event);
					assertEquals(scoped, notifier);
					assertNotNull(newValue);
					assertTrue(newValue instanceof IScope);
					assertTrue(((IScope) newValue).getThemes().containsAll(themes));
					assertEquals(themes.size(), ((IScope) newValue).getThemes().size());
					assertTrue(oldValue instanceof IScope);
					assertTrue(((IScope) oldValue).getThemes().containsAll(themes));
					assertTrue(((IScope) oldValue).getThemes().contains(theme));
					assertEquals(themes.size() + 1, ((IScope) oldValue).getThemes().size());
				}
				checked = true;
			}
		};

		topicMap.addTopicMapListener(listener);
		scoped.removeTheme(theme);

	}

	public void testEventRoleAdded() throws Exception {
		final IAssociation association = createAssociation(createTopic());
		final ITopic type = createTopic();
		final ITopic player = createTopic();

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(ROLE_ADDED, event);
					assertEquals(association, notifier);
					assertTrue(newValue instanceof IAssociationRole);
					assertEquals(type, ((IAssociationRole) newValue).getType());
					assertEquals(player, ((IAssociationRole) newValue).getPlayer());
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		association.createRole(type, player);
	}

	public void testEventAssociationAdded() throws Exception {
		final ITopic type = createTopic();
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(ASSOCIATION_ADDED, event);
					assertEquals(topicMap, notifier);
					assertTrue(newValue instanceof IAssociation);
					assertTrue(topicMap.getAssociations().contains(newValue));
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		createAssociation(type);
	}

	public void testEventNameAdded() throws Exception {
		final ITopic topic = createTopic();
		final ITopic type = createTopic();
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(NAME_ADDED, event);
					assertEquals(topic, notifier);
					assertTrue(newValue instanceof IName);
					assertTrue(topic.getNames().contains(newValue));
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		topic.createName(type, "name", new Topic[0]);
	}

	public void testEventOccurrenceAdded() throws Exception {
		final ITopic topic = createTopic();
		final ITopic type = createTopic();
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(OCCURRENCE_ADDED, event);
					assertEquals(topic, notifier);
					assertTrue(newValue instanceof IOccurrence);
					assertTrue(topic.getOccurrences().contains(newValue));
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		topic.createOccurrence(type, "Occurrence", new Topic[0]);
	}

	public void testEventVariantAdded() throws Exception {
		final ITopic topic = createTopic();
		final ITopic theme = createTopic();
		final Name name = topic.createName("name", new Topic[0]);
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(VARIANT_ADDED, event);
					assertEquals(name, notifier);
					assertTrue(newValue instanceof IVariant);
					assertTrue(name.getVariants().contains(newValue));
					assertNull(oldValue);
				}
				checked = true;
			}
		});
		name.createVariant("Variant", theme);
	}

	public void testEventPlayerModified() throws Exception {
		final IAssociation association = createAssociation(createTopic());
		final ITopic type = createTopic();
		final ITopic player = createTopic();
		final ITopic newPlayer = createTopic();
		final Role role = association.createRole(type, player);

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(PLAYER_MODIFIED, event);
					assertEquals(role, notifier);
					assertTrue(newValue instanceof ITopic);
					assertEquals(newPlayer, newValue);
					assertTrue(oldValue instanceof ITopic);
					assertEquals(player, oldValue);
				}
				checked = true;
			}
		});
		role.setPlayer(newPlayer);
	}

	public void testEventSubjectIdentifierRemoved() throws Exception {
		final ITopic topic = createTopic();
		final Locator locator = createLoctor("http://psi.example.org/locator");
		topic.addSubjectIdentifier(locator);

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(SUBJECT_IDENTIFIER_REMOVED, event);
					assertEquals(topic, notifier);
					assertTrue(oldValue instanceof ILocator);
					assertEquals(locator, oldValue);
					assertNull(newValue);
				}
				checked = true;
			}
		});
		topic.removeSubjectIdentifier(locator);

	}

	public void testEventSubjectLocatorRemoved() throws Exception {
		final ITopic topic = createTopic();
		final Locator locator = createLoctor("http://psi.example.org/locator");
		topic.addSubjectLocator(locator);

		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(SUBJECT_LOCATOR_REMOVED, event);
					assertEquals(topic, notifier);
					assertTrue(oldValue instanceof ILocator);
					assertEquals(locator, oldValue);
					assertNull(newValue);
				}
				checked = true;
			}
		});
		topic.removeSubjectLocator(locator);
	}

	public void testEventItemIdentifierRemoved() throws Exception {
		Locator itemIdentifier = createLoctor("http://psi.example.org/locator");
		// for topic
		ITopic topic = createTopic();
		_testEventItemIdentifierRemoved(topic, itemIdentifier);
		// for name
		Name name = topic.createName("Name", new Topic[0]);
		_testEventItemIdentifierRemoved(name, itemIdentifier);
		// for variant
		Variant variant = name.createVariant("Variant", createTopic());
		_testEventItemIdentifierRemoved(variant, itemIdentifier);
		// for occurrence
		Occurrence occurrence = topic.createOccurrence(createTopic(), "Occurrence", new Topic[0]);
		_testEventItemIdentifierRemoved(occurrence, itemIdentifier);
		// for association
		Association association = createAssociation(createTopic());
		_testEventItemIdentifierRemoved(association, itemIdentifier);
		// for role
		Role role = association.createRole(createTopic(), createTopic());
		_testEventItemIdentifierRemoved(role, itemIdentifier);
		// for topic map
		_testEventItemIdentifierRemoved(topicMap, itemIdentifier);
	}

	public void _testEventItemIdentifierRemoved(final Construct construct, final Locator locator) throws Exception {
		construct.addItemIdentifier(locator);
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(ITEM_IDENTIFIER_REMOVED, event);
					assertEquals(construct, notifier);
					assertTrue(oldValue instanceof ILocator);
					assertEquals(locator, oldValue);
					assertNull(newValue);
				}
				checked = true;
			}
		});
		construct.removeItemIdentifier(locator);
	}

	public void testEventTypeRemoved() throws Exception {
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TYPEHIERARCHY_AS_ASSOCIATION, false);

		final ITopic topic = createTopic();
		final ITopic type = createTopic();
		topic.addType(type);
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(TYPE_REMOVED, event);
					assertEquals(topic, notifier);
					assertTrue(oldValue instanceof ITopic);
					assertEquals(type, oldValue);
					assertNull(newValue);
				}
				checked = true;
			}
		});
		topic.removeType(type);
	}

	public void testEventSupertypeRemoved() throws Exception {
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TYPEHIERARCHY_AS_ASSOCIATION, false);

		final ITopic topic = createTopic();
		final ITopic type = createTopic();
		topic.addSupertype(type);
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(SUPERTYPE_REMOVED, event);
					assertEquals(topic, notifier);
					assertTrue(oldValue instanceof ITopic);
					assertEquals(type, oldValue);
					assertNull(newValue);
				}
				checked = true;
			}
		});
		topic.removeSupertype(type);
	}

	public void testEventConstructRemoved() throws Exception {
		// for topic
		_testEventConstructRemoved(topicMap, createTopic());
		// for name
		ITopic parent = createTopic();
		_testEventConstructRemoved(parent, parent.createName("Name", new Topic[0]));
		// for variant
		Name name = parent.createName("Name", new Topic[0]);
		_testEventConstructRemoved(name, name.createVariant("Variant", createTopic()));
		// for occurrence
		_testEventConstructRemoved(parent, parent.createOccurrence(createTopic(), "Occurrence", new Topic[0]));
		// for association
		_testEventConstructRemoved(topicMap, createAssociation(createTopic()));
		// for role
		IAssociation association = createAssociation(createTopic());
		_testEventConstructRemoved(association, association.createRole(createTopic(), createTopic()));
	}

	public void _testEventConstructRemoved(final Construct parent, final Construct construct) throws Exception {
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TYPEHIERARCHY_AS_ASSOCIATION, false);
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(CONSTRUCT_REMOVED, event);
					assertEquals(parent, notifier);
					assertTrue(oldValue instanceof IConstruct);
					assertEquals(construct, oldValue);
					assertNull(newValue);
				}
				checked = true;
			}
		});
		construct.remove();
	}

	public void testEventTypeSet() throws Exception {
		ITopic type = createTopic();
		ITopic otherType = createTopic();
		// for name
		Name name = createTopic().createName(otherType, "Name", createTopic());
		_testEventTypeSet(name, type, otherType);
		// for occurrence
		Occurrence occurrence = createTopic().createOccurrence(otherType, "Occurrence", createTopic());
		_testEventTypeSet(occurrence, type, otherType);
		// for association
		Association association = createAssociation(otherType);
		_testEventTypeSet(association, type, otherType);
		// for role
		Role role = association.createRole(otherType, createTopic());
		_testEventTypeSet(role, type, otherType);
	}

	public void _testEventTypeSet(final Typed typeable, final ITopic newType, final ITopic oldType) throws Exception {
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TYPEHIERARCHY_AS_ASSOCIATION, false);
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(TYPE_SET, event);
					assertEquals(typeable, notifier);
					assertEquals("Old Type for " + typeable.getClass().getSimpleName() + " is wrong!", oldType, oldValue);
					assertEquals("New Type for " + typeable.getClass().getSimpleName() + " is wrong!", newType, newValue);
				}
				checked = true;
			}
		});
		typeable.setType(newType);
	}

	public void testEventReifierSet() throws Exception {
		ITopic reifier = createTopic();
		ITopic otherReifier = createTopic();
		// for name
		Name name = createTopic().createName("Name", new Topic[0]);
		name.setReifier(otherReifier);
		_testEventReifierSet(name, reifier, otherReifier);
		// for variant
		Variant variant = name.createVariant("Variant", createTopic());
		variant.setReifier(otherReifier);
		_testEventReifierSet(variant, reifier, otherReifier);
		// for occurrence
		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "Occurrence", createTopic());
		occurrence.setReifier(otherReifier);
		_testEventReifierSet(occurrence, reifier, otherReifier);
		// for association
		Association association = createAssociation(createTopic());
		association.setReifier(otherReifier);
		_testEventReifierSet(association, reifier, otherReifier);
		// for role
		Role role = association.createRole(createTopic(), createTopic());
		role.setReifier(otherReifier);
		_testEventReifierSet(role, reifier, otherReifier);
	}

	public void _testEventReifierSet(final Reifiable reifiable, final ITopic newReifier, final ITopic oldReifier) throws Exception {
		factory.setFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION, false);
		factory.setFeature(FeatureStrings.TYPEHIERARCHY_AS_ASSOCIATION, false);
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(REIFIER_SET, event);
					assertEquals(reifiable, notifier);
					assertEquals("Old Reifier for " + reifiable.getClass().getSimpleName() + " is wrong!", oldReifier, oldValue);
					assertEquals("New Reifier for " + reifiable.getClass().getSimpleName() + " is wrong!", newReifier, newValue);
				}
				checked = true;
			}
		});
		reifiable.setReifier(newReifier);
		reifiable.setReifier(null);
	}

	public void testEventValueModified() throws Exception {
		String value = "value";
		String otherValue = "other";
		// for name
		Name name = createTopic().createName(otherValue, new Topic[0]);
		_testEventValueModified(name, value, otherValue);
		// for variant
		Variant variant = name.createVariant(otherValue, createTopic());
		_testEventValueModified(variant, value, otherValue);
		// for occurrence
		Occurrence occurrence = createTopic().createOccurrence(createTopic(), otherValue, new Topic[0]);
		_testEventValueModified(occurrence, value, otherValue);
	}

	public void _testEventValueModified(final Construct construct, final String newValue_, final String oldValue_) throws Exception {
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(VALUE_MODIFIED, event);
					assertEquals(construct, notifier);
					assertEquals("Old Value for " + construct.getClass().getSimpleName() + " is wrong!", oldValue_, oldValue);
					assertEquals("New Reifier for " + construct.getClass().getSimpleName() + " is wrong!", newValue_, newValue);
				}
				checked = true;
			}
		});

		if (construct instanceof Name) {
			((Name) construct).setValue(newValue_);
		} else if (construct instanceof DatatypeAware) {
			((DatatypeAware) construct).setValue(newValue_);
		} else {
			fail("Invalid construct calling!");
		}
	}

	public void testEventMerge() throws Exception {
		final ITopic topic = createTopic();
		final ITopic otherTopic = createTopic();
		topicMap.addTopicMapListener(new ITopicMapListener() {

			boolean checked = false;

			@Override
			public void topicMapChanged(String id, TopicMapEventType event, Construct notifier, Object newValue, Object oldValue) {
				if (!checked) {
					assertEquals(MERGE, event);
					assertEquals(topicMap, notifier);
					assertEquals(topic, newValue);
					assertEquals(otherTopic, oldValue);
				}
				checked = true;
			}
		});
		topic.mergeIn(otherTopic);
	}

}
