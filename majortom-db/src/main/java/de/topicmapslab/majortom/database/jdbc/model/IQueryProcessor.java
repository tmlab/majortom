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
package de.topicmapslab.majortom.database.jdbc.model;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import org.tmapi.core.Topic;
import org.tmapi.core.TopicMap;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ICharacteristics;
import de.topicmapslab.majortom.model.core.IConstruct;
import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IReifiable;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.model.core.ITypeable;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;

/**
 * @author Sven Krosse
 * 
 */
public interface IQueryProcessor {

	public String doCreateTopicMap(ILocator baseLocator) throws SQLException;

	public IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type) throws SQLException;

	public IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes) throws SQLException;

	public IName doCreateName(ITopic topic, String value) throws SQLException;

	public IName doCreateName(ITopic topic, String value, Collection<ITopic> themes) throws SQLException;

	public IName doCreateName(ITopic topic, ITopic type, String value) throws SQLException;

	public IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype, Collection<ITopic> themes) throws SQLException;

	public IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws SQLException;

	public IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes) throws SQLException;

	public ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap, ILocator itemIdentifier) throws SQLException;

	public ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap, ILocator subjectIdentifier) throws SQLException;

	public ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap, ILocator subjectLocator) throws SQLException;

	public ILocator doCreateLocator(ITopicMap topicMap, String reference) throws SQLException;

	public IVariant doCreateVariant(IName name, String value, Collection<ITopic> themes) throws SQLException;

	public IVariant doCreateVariant(IName name, ILocator datatype, Collection<ITopic> themes) throws SQLException;

	public IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes) throws SQLException;

	public void doModifyItemIdentifier(IConstruct c, ILocator itemIdentifier) throws SQLException;

	public void doModifyPlayer(IAssociationRole role, ITopic player) throws SQLException;

	public void doModifyReifier(IReifiable r, ITopic reifier) throws SQLException;

	public void doModifyScope(IScopable s, ITopic theme) throws SQLException;

	public void doModifySubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws SQLException;

	public void doModifySubjectLocator(ITopic t, ILocator subjectLocator) throws SQLException;

	public void doModifySupertype(ITopic t, ITopic type) throws SQLException;

	public void doModifyTag(ITopicMap tm, String tag) throws SQLException;

	public void doModifyTag(ITopicMap tm, String tag, Calendar timestamp) throws SQLException;

	public void doModifyType(ITypeable t, ITopic type) throws SQLException;

	public void doModifyType(ITopic t, ITopic type) throws SQLException;

	public void doModifyValue(IName n, String value) throws SQLException;

	public void doModifyValue(IDatatypeAware t, String value) throws SQLException;

	public void doModifyValue(IDatatypeAware t, String value, ILocator datatype) throws SQLException;

	public void doMergeTopics(ITopic context, ITopic other) throws SQLException;

	public void doMergeTopicMaps(TopicMap context, TopicMap other) throws SQLException;

	public Set<IAssociation> doReadAssociation(ITopic t) throws SQLException;

	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type) throws SQLException;

	public Set<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws SQLException;

	public Set<IAssociation> doReadAssociation(ITopic t, IScope scope) throws SQLException;

	public Set<IAssociation> doReadAssociation(ITopicMap tm) throws SQLException;

	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws SQLException;

	public Set<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws SQLException;

	public Set<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws SQLException;

	public Set<ICharacteristics> doReadCharacteristics(ITopic t) throws SQLException;

	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws SQLException;

	public Set<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws SQLException;

	public Set<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws SQLException;

	public IConstruct doReadConstruct(ITopicMap t, String id) throws SQLException;

	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier) throws SQLException;

	public ILocator doReadDataType(IDatatypeAware d) throws SQLException;

	public Set<ILocator> doReadItemIdentifiers(IConstruct c) throws SQLException;

	public ILocator doReadLocator(ITopicMap t) throws SQLException;

	public Set<IName> doReadNames(ITopic t) throws SQLException;

	public Set<IName> doReadNames(ITopic t, ITopic type) throws SQLException;

	public Set<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws SQLException;

	public Set<IName> doReadNames(ITopic t, IScope scope) throws SQLException;

	public IRevision doReadFutureRevision(IRevision r) throws SQLException;

	public Set<IOccurrence> doReadOccurrences(ITopic t) throws SQLException;

	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws SQLException;

	public Set<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws SQLException;

	public Set<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws SQLException;

	public ITopic doReadPlayer(IAssociationRole role) throws SQLException;

	public IRevision doReadPreviousRevision(IRevision r) throws SQLException;

	public IReifiable doReadReification(ITopic t) throws SQLException;

	public ITopic doReadReification(IReifiable r) throws SQLException;

	public Calendar doReadRevisionBegin(IRevision r) throws SQLException;

	public Calendar doReadRevisionEnd(IRevision r) throws SQLException;

	public Changeset doReadChangeSet(IRevision r) throws SQLException;

	public Set<IAssociationRole> doReadRoles(IAssociation association) throws SQLException;

	public Set<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws SQLException;

	public Set<IAssociationRole> doReadRoles(ITopic player) throws SQLException;

	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws SQLException;

	public Set<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws SQLException;

	public Set<ITopic> doReadRoleTypes(IAssociation association) throws SQLException;

	public Set<ILocator> doReadSubjectIdentifiers(ITopic t) throws SQLException;

	public Set<ILocator> doReadSubjectLocators(ITopic t) throws SQLException;

	public Set<ITopic> doReadSuptertypes(ITopic t) throws SQLException;

	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws SQLException;

	public ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws SQLException;

	public Set<ITopic> doReadTopics(ITopicMap t) throws SQLException;

	public Set<ITopic> doReadTopics(ITopicMap t, ITopic type) throws SQLException;

	public ITopic doReadType(ITypeable typed) throws SQLException;

	public Set<ITopic> doReadTypes(ITopic t) throws SQLException;

	public IScope doReadScope(IScopable s) throws SQLException;

	public Object doReadValue(IName n) throws SQLException;

	public Object doReadValue(IDatatypeAware t) throws SQLException;

	public Set<IVariant> doReadVariants(IName n) throws SQLException;

	public Set<IVariant> doReadVariants(IName n, IScope scope) throws SQLException;

	public void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws SQLException;

	public void doRemoveScope(IScopable s, ITopic theme) throws SQLException;

	public void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws SQLException;

	public void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws SQLException;

	public void doRemoveSupertype(ITopic t, ITopic type) throws SQLException;

	public void doRemoveType(ITopic t, ITopic type) throws SQLException;

	public void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws SQLException;

	public void doRemoveTopic(ITopic topic, boolean cascade) throws SQLException;

	public void doRemoveName(IName name, boolean cascade) throws SQLException;

	public void doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws SQLException;

	public void doRemoveAssociation(IAssociation association, boolean cascade) throws SQLException;

	public void doRemoveRole(IAssociationRole role, boolean cascade) throws SQLException;

	public void doRemoveVariant(IVariant variant, boolean cascade) throws SQLException;

	// ****************
	// * INDEX METHOD *
	// ****************

	// TypeInstanceIndex
	
	public Collection<ITopic> getAssociationTypes(ITopicMap topicMap) throws SQLException;
	
	public Collection<ITopic> getNameTypes(ITopicMap topicMap) throws SQLException;
	
	public Collection<ITopic> getOccurrenceTypes(ITopicMap topicMap) throws SQLException;
	
	public Collection<ITopic> getRoleTypes(ITopicMap topicMap) throws SQLException;
	
	public Collection<ITopic> getTopicTypes(ITopicMap topicMap) throws SQLException;
	
	public Collection<IAssociation> getAssociationsByType(ITopic type) throws SQLException;

	public Collection<IName> getNamesByType(ITopic type) throws SQLException;

	public Collection<IOccurrence> getOccurrencesByType(ITopic type) throws SQLException;

	public Collection<IAssociationRole> getRolesByType(ITopic type) throws SQLException;

	public <T extends Topic> Collection<ITopic> getTopicsByType(ITopicMap topicMap, T type) throws SQLException;

	public <T extends Topic> Collection<ITopic> getTopicsByTypes(Collection<T> type, boolean all) throws SQLException;
	
	// ScopeIndex
	
	public <T extends Topic> Collection<IScope> getScopesByThemes(ITopicMap topicMap, Collection<T> themes, boolean all) throws SQLException;


}