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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.revision.Changeset;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.store.TopicMapStoreParameterType;

/**
 * @author Sven Krosse
 * 
 */
public interface IQueryProcessor {

	public Connection getConnection();

	public void close();

	public void openTransaction() throws SQLException;

	public void commit() throws SQLException;

	/**
	 * Returns the session of the query processor instance
	 * 
	 * @return the session
	 */
	public <T extends ISession> T getSession();

	public Long doReadExistingTopicMapIdentity(ILocator baseLocator) throws SQLException;
	
	public Long doCreateTopicMapIdentity(ILocator baseLocator) throws SQLException;

	public IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type) throws SQLException;

	public IAssociation doCreateAssociation(ITopicMap topicMap, ITopic type, Collection<ITopic> themes)
			throws SQLException;

	public IName doCreateName(ITopic topic, String value) throws SQLException;

	public IName doCreateName(ITopic topic, String value, Collection<ITopic> themes) throws SQLException;

	public IName doCreateName(ITopic topic, ITopic type, String value) throws SQLException;

	public IName doCreateName(ITopic topic, ITopic type, String value, Collection<ITopic> themes) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, Collection<ITopic> themes)
			throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value) throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, ILocator value, Collection<ITopic> themes)
			throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype)
			throws SQLException;

	public IOccurrence doCreateOccurrence(ITopic topic, ITopic type, String value, ILocator datatype,
			Collection<ITopic> themes) throws SQLException;

	public IAssociationRole doCreateRole(IAssociation association, ITopic type, ITopic player) throws SQLException;

	public IScope doCreateScope(ITopicMap topicMap, Collection<ITopic> themes) throws SQLException;

	public ITopic doCreateTopicWithoutIdentifier(ITopicMap topicMap) throws SQLException;

	public ITopic doCreateTopicByItemIdentifier(ITopicMap topicMap, ILocator itemIdentifier) throws SQLException;

	public ITopic doCreateTopicBySubjectIdentifier(ITopicMap topicMap, ILocator subjectIdentifier) throws SQLException;

	public ITopic doCreateTopicBySubjectLocator(ITopicMap topicMap, ILocator subjectLocator) throws SQLException;

	public ILocator doCreateLocator(ITopicMap topicMap, String reference) throws SQLException;

	public IVariant doCreateVariant(IName name, String value, Collection<ITopic> themes) throws SQLException;

	public IVariant doCreateVariant(IName name, ILocator datatype, Collection<ITopic> themes) throws SQLException;

	public IVariant doCreateVariant(IName name, String value, ILocator datatype, Collection<ITopic> themes)
			throws SQLException;

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

	public void doModifyValue(IDatatypeAware t, String value, ILocator datatype) throws SQLException;

	public void doMergeTopics(ITopic context, ITopic other) throws SQLException;

	public void doMergeTopicMaps(TopicMap context, TopicMap other) throws SQLException;

	public Collection<IAssociation> doReadAssociation(ITopic t, long offset, long limit) throws SQLException;

	public Collection<IAssociation> doReadAssociation(ITopic t, ITopic type) throws SQLException;

	public Collection<IAssociation> doReadAssociation(ITopic t, ITopic type, IScope scope) throws SQLException;

	public Collection<IAssociation> doReadAssociation(ITopic t, IScope scope) throws SQLException;

	public Collection<IAssociation> doReadAssociation(ITopicMap tm) throws SQLException;

	public Collection<IAssociation> doReadAssociation(ITopicMap tm, ITopic type) throws SQLException;

	public Collection<IAssociation> doReadAssociation(ITopicMap tm, ITopic type, IScope scope) throws SQLException;

	public Collection<IAssociation> doReadAssociation(ITopicMap tm, IScope scope) throws SQLException;

	public Collection<ICharacteristics> doReadCharacteristics(ITopic t) throws SQLException;

	public Collection<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type) throws SQLException;

	public Collection<ICharacteristics> doReadCharacteristics(ITopic t, ITopic type, IScope scope) throws SQLException;

	public Collection<ICharacteristics> doReadCharacteristics(ITopic t, IScope scope) throws SQLException;

	public IConstruct doReadConstruct(ITopicMap t, Long id, boolean lookupHistory) throws SQLException;

	public IConstruct doReadConstruct(ITopicMap t, ILocator itemIdentifier) throws SQLException;

	public ILocator doReadDataType(IDatatypeAware d) throws SQLException;

	public Collection<ILocator> doReadItemIdentifiers(IConstruct c) throws SQLException;

	public ILocator doReadLocator(ITopicMap t) throws SQLException;

	public Collection<IName> doReadNames(ITopic t, long offset, long limit) throws SQLException;

	public Collection<IName> doReadNames(ITopic t, ITopic type) throws SQLException;

	public Collection<IName> doReadNames(ITopic t, ITopic type, IScope scope) throws SQLException;

	public Collection<IName> doReadNames(ITopic t, IScope scope) throws SQLException;

	public Collection<IOccurrence> doReadOccurrences(ITopic t, long offset, long limit) throws SQLException;

	public Collection<IOccurrence> doReadOccurrences(ITopic t, ITopic type) throws SQLException;

	public Collection<IOccurrence> doReadOccurrences(ITopic t, ITopic type, IScope scope) throws SQLException;

	public Collection<IOccurrence> doReadOccurrences(ITopic t, IScope scope) throws SQLException;

	public ITopic doReadPlayer(IAssociationRole role) throws SQLException;

	public IReifiable doReadReification(ITopic t) throws SQLException;

	public ITopic doReadReification(IReifiable r) throws SQLException;

	public Collection<IAssociationRole> doReadRoles(IAssociation association, long offset, long limit)
			throws SQLException;

	public Collection<IAssociationRole> doReadRoles(IAssociation association, ITopic type) throws SQLException;

	public Collection<IAssociationRole> doReadRoles(ITopic player, long offset, long limit) throws SQLException;

	public Collection<IAssociationRole> doReadRoles(ITopic player, ITopic type) throws SQLException;

	public Collection<IAssociationRole> doReadRoles(ITopic player, ITopic type, ITopic assocType) throws SQLException;

	public Collection<ITopic> doReadRoleTypes(IAssociation association) throws SQLException;

	public Collection<ILocator> doReadSubjectIdentifiers(ITopic t) throws SQLException;

	public Collection<ILocator> doReadSubjectLocators(ITopic t) throws SQLException;

	public Collection<ITopic> doReadSuptertypes(ITopic t, long offset, long limit) throws SQLException;

	public ITopic doReadTopicBySubjectIdentifier(ITopicMap t, ILocator subjectIdentifier) throws SQLException;

	public ITopic doReadTopicBySubjectLocator(ITopicMap t, ILocator subjectLocator) throws SQLException;

	/**
	 * Internal method to read all themes of a scope
	 * 
	 * @param topicMap
	 *            the topic map
	 * @param scopeId
	 *            the scope id
	 * @return a collection of all themes
	 * @throws SQLException
	 *             thrown if a database error occurrs
	 */
	public Collection<ITopic> doReadThemes(ITopicMap topicMap, long scopeId) throws SQLException;

	public Collection<ITopic> doReadTopics(ITopicMap t) throws SQLException;

	public Collection<ITopic> doReadTopics(ITopicMap t, ITopic type) throws SQLException;

	public ITopic doReadType(ITypeable typed) throws SQLException;

	public Collection<ITopic> doReadTypes(ITopic t, long offset, long limit) throws SQLException;

	public IScope doReadScope(IScopable s) throws SQLException;

	public Object doReadValue(IName n) throws SQLException;

	public Object doReadValue(IDatatypeAware t) throws SQLException;

	public Collection<IVariant> doReadVariants(IName n, long offset, long limit) throws SQLException;

	public Collection<IVariant> doReadVariants(IName n, IScope scope) throws SQLException;

	/**
	 * Returns the best label for the current topic instance. The best label will be identified satisfying the following
	 * rules in the given order.
	 * <p>
	 * 1. Names of the default name type are weighted higher than names of other types.
	 * </p>
	 * <p>
	 * 2. Names with the unconstrained scope are weighted higher than other scoped names.
	 * </p>
	 * <p>
	 * 3. Names with a smaller number of scoping themes are weighted higher than others.
	 * </p>
	 * <p>
	 * 4. Names with a lexicographically smaller value are weighted higher than others.
	 * </p>
	 * <p>
	 * 5. If no names are existing, the subject-identifier with the lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 6. If no subject-identifiers are existing, the subject-locators with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 7. If no subject-locators are existing, the item-identifier with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 8. At least the ID of the topic will be returned.
	 * </p>
	 * 
	 * @since 1.1.2
	 */
	public String doReadBestLabel(ITopic topic) throws SQLException;

	/**
	 * Returns the best label for the current topic instance. The best label will be identified satisfying the following
	 * rules in the given order.
	 * <p>
	 * 1. Names of the default name type are weighted higher than names of other types.
	 * </p>
	 * <p>
	 * 2. Names with the unconstrained scope are weighted higher than other scoped names.
	 * </p>
	 * <p>
	 * 3. Names with a smaller number of scoping themes are weighted higher than others.
	 * </p>
	 * <p>
	 * 4. Names with a lexicographically smaller value are weighted higher than others.
	 * </p>
	 * <p>
	 * 5. If no names are existing, the subject-identifier with the lexicographically smallest reference are returned.
	 * </p>
	 * <p>
	 * 6. If no subject-identifiers are existing, the subject-locators with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 7. If no subject-locators are existing, the item-identifier with the lexicographically smallest reference are
	 * returned.
	 * </p>
	 * <p>
	 * 8. At least the ID of the topic will be returned.
	 * </p>
	 * 
	 * @param strict
	 *            if there is no name with the given theme and strict is <code>true</code>, then <code>null</code> will
	 *            be returned.
	 * @since 1.1.2
	 */
	public String doReadBestLabel(ITopic topic, ITopic theme, boolean strict) throws SQLException;

	public void doRemoveItemIdentifier(IConstruct c, ILocator itemIdentifier) throws SQLException;

	public void doRemoveScope(IScopable s, ITopic theme) throws SQLException;

	public void doRemoveSubjectIdentifier(ITopic t, ILocator subjectIdentifier) throws SQLException;

	public void doRemoveSubjectLocator(ITopic t, ILocator subjectLocator) throws SQLException;

	public void doRemoveSupertype(ITopic t, ITopic type) throws SQLException;

	public void doRemoveType(ITopic t, ITopic type) throws SQLException;

	public void doRemoveTopicMap(ITopicMap topicMap, boolean cascade) throws SQLException;

	public boolean doRemoveTopic(ITopic topic, boolean cascade) throws SQLException;

	public boolean doRemoveName(IName name, boolean cascade) throws SQLException;

	public boolean doRemoveOccurrence(IOccurrence occurrence, boolean cascade) throws SQLException;

	public void doClearTopicMap(ITopicMap topicMap) throws SQLException;

	/**
	 * Using JDBC to remove an association instance and all roles.
	 * 
	 * @param association
	 *            the association
	 * @param cascade
	 *            cascading deletion
	 * @param revision
	 *            the revision to store changes
	 * @throws SQLException
	 *             thrown if JDBC command fails
	 */
	public void doRemoveAssociation(IAssociation association, boolean cascade, IRevision revision) throws SQLException;

	public boolean doRemoveAssociation(IAssociation association, boolean cascade) throws SQLException;

	public boolean doRemoveRole(IAssociationRole role, boolean cascade) throws SQLException;

	public boolean doRemoveVariant(IVariant variant, boolean cascade) throws SQLException;

	// ****************
	// * INDEX METHOD *
	// ****************

	// PagedConstructIndex

	public long doReadNumberOfNames(ITopic topic) throws SQLException;

	public long doReadNumberOfOccurrences(ITopic topic) throws SQLException;

	public long doReadNumberOfTypes(ITopic topic) throws SQLException;

	public long doReadNumberOfSupertypes(ITopic topic) throws SQLException;

	public long doReadNumberOfAssociationsPlayed(ITopic topic) throws SQLException;

	public long doReadNumberOfRolesPlayed(ITopic topic) throws SQLException;

	public long doReadNumberOfVariants(IName name) throws SQLException;

	public long doReadNumberOfRoles(IAssociation association) throws SQLException;

	// TypeInstanceIndex

	public Collection<ITopic> getAssociationTypes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getCharacteristicsTypes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getNameTypes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getOccurrenceTypes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getRoleTypes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getTopicTypes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<IAssociation> getAssociationsByType(ITopic type, long offset, long limit) throws SQLException;

	public <T extends Topic> Collection<IAssociation> getAssociationsByTypes(Collection<T> types, long offset,
			long limit) throws SQLException;

	public Collection<ICharacteristics> getCharacteristicsByType(ITopic type, long offset, long limit)
			throws SQLException;

	public <T extends Topic> Collection<ICharacteristics> getCharacteristicsByTypes(Collection<T> types, long offset,
			long limit) throws SQLException;

	public Collection<IName> getNamesByType(ITopic type, long offset, long limit) throws SQLException;

	public <T extends Topic> Collection<IName> getNamesByTypes(Collection<T> types, long offset, long limit)
			throws SQLException;

	public Collection<IOccurrence> getOccurrencesByType(ITopic type, long offset, long limit) throws SQLException;

	public <T extends Topic> Collection<IOccurrence> getOccurrencesByTypes(Collection<T> types, long offset, long limit)
			throws SQLException;

	public Collection<IAssociationRole> getRolesByType(ITopic type, long offset, long limit) throws SQLException;

	public <T extends Topic> Collection<IAssociationRole> getRolesByTypes(Collection<T> types, long offset, long limit)
			throws SQLException;

	public <T extends Topic> Collection<ITopic> getTopicsByType(ITopicMap topicMap, T type, long offset, long limit)
			throws SQLException;

	public <T extends Topic> Collection<ITopic> getTopicsByTypes(Collection<T> types, boolean all, long offset,
			long limit) throws SQLException;

	// TransitiveTypeInstanceIndex

	public Collection<IAssociation> getAssociationsByTypeTransitive(ITopic type, long offset, long limit)
			throws SQLException;

	public <T extends Topic> Collection<IAssociation> getAssociationsByTypeTransitive(ITopicMap topicMap,
			Collection<T> types, long offset, long limit) throws SQLException;

	public Collection<ICharacteristics> getCharacteristicsByTypeTransitive(ITopic type, long offset, long limit)
			throws SQLException;

	public <T extends Topic> Collection<ICharacteristics> getCharacteristicsByTypesTransitive(Collection<T> types,
			long offset, long limit) throws SQLException;

	public Collection<IName> getNamesByTypeTransitive(ITopic type, long offset, long limit) throws SQLException;

	public <T extends Topic> Collection<IName> getNamesByTypeTransitive(ITopicMap topicMap, Collection<T> types,
			long offset, long limit) throws SQLException;

	public Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopic type, long offset, long limit)
			throws SQLException;

	public <T extends Topic> Collection<IOccurrence> getOccurrencesByTypeTransitive(ITopicMap topicMap,
			Collection<T> types, long offset, long limit) throws SQLException;

	public Collection<IAssociationRole> getRolesByTypeTransitive(ITopic type, long offset, long limit)
			throws SQLException;

	public <T extends Topic> Collection<IAssociationRole> getRolesByTypeTransitive(ITopicMap topicMap,
			Collection<T> types, long offset, long limit) throws SQLException;

	public Collection<ITopic> getTopicsByTypeTransitive(ITopic type, long offset, long limit) throws SQLException;

	public <T extends Topic> Collection<ITopic> getTopicsByTypesTransitive(ITopicMap topicMap, Collection<T> type,
			boolean all, long offset, long limit) throws SQLException;

	// ScopeIndex

	public <T extends Topic> Collection<IScope> getScopesByThemes(ITopicMap topicMap, Collection<T> themes, boolean all)
			throws SQLException;

	public Collection<IAssociation> getAssociationsByScope(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException;

	public Collection<IAssociation> getAssociationsByScopes(ITopicMap topicMap, Collection<IScope> scopes, long offset,
			long limit) throws SQLException;

	public Collection<IAssociation> getAssociationsByTheme(ITopicMap topicMap, Topic theme, long offset, long limit)
			throws SQLException;

	public Collection<IAssociation> getAssociationsByThemes(ITopicMap topicMap, Topic[] themes, boolean all,
			long offset, long limit) throws SQLException;

	public Collection<IScope> getAssociationScopes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getAssociationThemes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ICharacteristics> getCharacteristicsByScope(ITopicMap topicMap, IScope scope, long offset,
			long limit) throws SQLException;

	public Collection<IName> getNamesByScope(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException;

	public Collection<IName> getNamesByScopes(ITopicMap topicMap, Collection<IScope> scopes, long offset, long limit)
			throws SQLException;

	public Collection<IName> getNamesByTheme(ITopicMap topicMap, Topic theme, long offset, long limit)
			throws SQLException;

	public Collection<IName> getNamesByThemes(ITopicMap topicMap, Topic[] themes, boolean all, long offset, long limit)
			throws SQLException;

	public Collection<IScope> getNameScopes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getNameThemes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<IOccurrence> getOccurrencesByScope(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException;

	public Collection<IOccurrence> getOccurrencesByScopes(ITopicMap topicMap, Collection<IScope> scopes, long offset,
			long limit) throws SQLException;

	public Collection<IOccurrence> getOccurrencesByTheme(ITopicMap topicMap, Topic theme, long offset, long limit)
			throws SQLException;

	public Collection<IOccurrence> getOccurrencesByThemes(ITopicMap topicMap, Topic[] themes, boolean all, long offset,
			long limit) throws SQLException;

	public Collection<IScope> getOccurrenceScopes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getOccurrenceThemes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<IScopable> getScopables(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException;

	public Collection<IVariant> getVariantsByScope(ITopicMap topicMap, IScope scope, long offset, long limit)
			throws SQLException;

	public Collection<IVariant> getVariantsByScopes(ITopicMap topicMap, Collection<IScope> scopes, long offset,
			long limit) throws SQLException;

	public Collection<IVariant> getVariantsByTheme(ITopicMap topicMap, Topic theme, long offset, long limit)
			throws SQLException;

	public Collection<IVariant> getVariantsByThemes(ITopicMap topicMap, Topic[] themes, boolean all, long offset,
			long limit) throws SQLException;

	public Collection<IScope> getVariantScopes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<ITopic> getVariantThemes(ITopicMap topicMap, long offset, long limit) throws SQLException;

	// LiteralIndex

	public Collection<ICharacteristics> getCharacteristics(final ITopicMap topicMap, final String value, long offset,
			long limit) throws SQLException;

	public Collection<ICharacteristics> getCharacteristics(final ITopicMap topicMap, final String value,
			final String reference, long offset, long limit) throws SQLException;

	public Collection<ICharacteristics> getCharacteristicsByDatatype(final ITopicMap topicMap, final String reference,
			long offset, long limit) throws SQLException;

	public Collection<ICharacteristics> getCharacteristicsByPattern(final ITopicMap topicMap, final String value,
			long offset, long limit) throws SQLException;

	public Collection<ICharacteristics> getCharacteristicsByPattern(final ITopicMap topicMap, final String value,
			final String reference, long offset, long limit) throws SQLException;

	public Collection<IDatatypeAware> getDatatypeAwaresByDatatype(final ITopicMap topicMap, final String reference,
			long offset, long limit) throws SQLException;

	public Collection<IName> getNames(final ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<IName> getNames(final ITopicMap topicMap, final String value) throws SQLException;

	public Collection<IName> getNamesByPattern(final ITopicMap topicMap, final String pattern) throws SQLException;

	public Collection<IOccurrence> getOccurrences(final ITopicMap topicMap, long offset, long limit)
			throws SQLException;

	public Collection<IOccurrence> getOccurrences(final ITopicMap topicMap, Calendar lower, Calendar upper,
			long offset, long limit) throws SQLException;

	public Collection<IOccurrence> getOccurrences(final ITopicMap topicMap, double value, double deviance,
			final String reference, long offset, long limit) throws SQLException;

	public Collection<IOccurrence> getOccurrences(final ITopicMap topicMap, final String value) throws SQLException;

	public Collection<IOccurrence> getOccurrences(final ITopicMap topicMap, final String value, final String reference,
			long offset, long limit) throws SQLException;

	public Collection<IOccurrence> getOccurrencesByDatatype(final ITopicMap topicMap, final String reference,
			long offset, long limit) throws SQLException;

	public Collection<IOccurrence> getOccurrencesByPattern(final ITopicMap topicMap, final String pattern)
			throws SQLException;

	public Collection<IOccurrence> getOccurrencesByPattern(final ITopicMap topicMap, final String pattern,
			final String reference) throws SQLException;

	public Collection<IVariant> getVariants(final ITopicMap topicMap, long offset, long limit) throws SQLException;

	public Collection<IVariant> getVariants(final ITopicMap topicMap, final String value) throws SQLException;

	public Collection<IVariant> getVariants(final ITopicMap topicMap, final String value, final String reference)
			throws SQLException;

	public Collection<IVariant> getVariantsByDatatype(final ITopicMap topicMap, final String reference)
			throws SQLException;

	public Collection<IVariant> getVariantByPattern(final ITopicMap topicMap, final String pattern) throws SQLException;

	public Collection<IVariant> getVariantsByPattern(final ITopicMap topicMap, final String pattern,
			final String reference) throws SQLException;

	// IdentityIndex

	public Collection<ILocator> getItemIdentifiers(final ITopicMap topicMap, long offset, long limit)
			throws SQLException;

	public Collection<ILocator> getSubjectIdentifiers(final ITopicMap topicMap, long offset, long limit)
			throws SQLException;

	public Collection<ILocator> getSubjectLocators(final ITopicMap topicMap, long offset, long limit)
			throws SQLException;

	public Collection<IConstruct> getConstructsByIdentitifer(final ITopicMap topicMap, final String regExp,
			long offset, long limit) throws SQLException;

	public Collection<IConstruct> getConstructsByItemIdentitifer(final ITopicMap topicMap, final String regExp,
			long offset, long limit) throws SQLException;

	public Collection<ITopic> getTopicsBySubjectIdentitifer(final ITopicMap topicMap, final String regExp, long offset,
			long limit) throws SQLException;

	public Collection<ITopic> getTopicsBySubjectLocator(final ITopicMap topicMap, final String regExp, long offset,
			long limit) throws SQLException;

	// SupertypeSubtypeIndex

	public Collection<ITopic> getDirectSubtypes(final ITopicMap topicMap, final ITopic type, long offset, long limit)
			throws SQLException;

	public Collection<ITopic> getSubtypes(final ITopicMap topicMap, final ITopic type, long offset, long limit)
			throws SQLException;

	public Collection<ITopic> getSubtypes(final ITopicMap topicMap, long offset, long limit) throws SQLException;

	public <T extends Topic> Collection<ITopic> getSubtypes(final ITopicMap topicMap, final Collection<T> types,
			final boolean matchAll, long offset, long limit) throws SQLException;

	public Collection<ITopic> getDirectSupertypes(final ITopicMap topicMap, final ITopic type, long offset, long limit)
			throws SQLException;

	public Collection<ITopic> getSupertypes(final ITopicMap topicMap, final ITopic type, long offset, long limit)
			throws SQLException;

	public Collection<ITopic> getSupertypes(final ITopicMap topicMap, long offset, long limit) throws SQLException;

	public <T extends Topic> Collection<ITopic> getSupertypes(final ITopicMap topicMap, final Collection<T> types,
			final boolean matchAll, long offset, long limit) throws SQLException;

	/*
	 * revision management
	 */

	public IRevision doCreateRevision(ITopicMap topicMap, TopicMapEventType type) throws SQLException;

	public void doCreateChangeSet(IRevision revision, TopicMapEventType type, IConstruct notifier, Object newValue,
			Object oldValue) throws SQLException;

	public void doCreateTag(final String tag, final Calendar time) throws SQLException;

	public void doCreateMetadata(final IRevision revision, final String key, final String value) throws SQLException;

	public IRevision doReadFirstRevision(ITopicMap topicMap) throws SQLException;

	public IRevision doReadLastRevision(ITopicMap topicMap) throws SQLException;

	public IRevision doReadPastRevision(ITopicMap topicMap, IRevision revision) throws SQLException;

	public IRevision doReadFutureRevision(ITopicMap topicMap, IRevision revision) throws SQLException;

	public Changeset doReadChangeset(ITopicMap topicMap, IRevision revision) throws SQLException;

	public TopicMapEventType doReadChangesetType(ITopicMap topicMap, IRevision revision) throws SQLException;

	public Calendar doReadLastModification(ITopicMap topicMap) throws SQLException;

	public Calendar doReadLastModificationOfTopic(ITopic topic) throws SQLException;

	public Calendar doReadTimestamp(IRevision revision) throws SQLException;

	public List<IRevision> doReadRevisionsByTopic(ITopic topic) throws SQLException;

	public List<IRevision> doReadRevisionsByAssociationType(ITopic type) throws SQLException;

	public Changeset doReadChangesetsByTopic(ITopic topic) throws SQLException;

	public Changeset doReadChangesetsByAssociationType(ITopic type) throws SQLException;

	public IRevision doReadRevisionByTag(final ITopicMap topicMap, final String tag) throws SQLException;

	public IRevision doReadRevisionByTimestamp(final ITopicMap topicMap, final Calendar time) throws SQLException;

	public Map<String, String> doReadMetadata(final IRevision revision) throws SQLException;

	public String doReadMetadataByKey(final IRevision revision, final String key) throws SQLException;

	public void dump(final IRevision revision, final IAssociationRole role) throws SQLException;

	public void dump(final IRevision revision, final IAssociation association) throws SQLException;

	public void dump(final IRevision revision, final IVariant variant) throws SQLException;

	public void dump(final IRevision revision, final IName name) throws SQLException;

	public void dump(final IRevision revision, final IOccurrence occurrence) throws SQLException;

	public void dump(final IRevision revision, final ITopic topic) throws SQLException;

	public Map<TopicMapStoreParameterType, Object> doReadHistory(IConstruct c, TopicMapStoreParameterType... arguments)
			throws SQLException;

	/**
	 * Method to load all topic map locators from database
	 * 
	 * @return
	 */
	public Set<ILocator> getLocators() throws SQLException;
}
