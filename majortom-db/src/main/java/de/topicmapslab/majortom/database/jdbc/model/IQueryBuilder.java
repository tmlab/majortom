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

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Sven Krosse
 * 
 */
public interface IQueryBuilder {

	public void close();

	// ****************
	// * INSERT QUERY *
	// ****************

	public PreparedStatement getQueryCreateTopicMap() throws SQLException;

	public PreparedStatement getQueryCreateAssociation() throws SQLException;

	public PreparedStatement getQueryCreateAssociationWithScope() throws SQLException;

	public PreparedStatement getQueryCreateName() throws SQLException;

	public PreparedStatement getQueryCreateNameWithScope() throws SQLException;

	public PreparedStatement getQueryCreateOccurrence() throws SQLException;

	public PreparedStatement getQueryCreateOccurrenceWithScope() throws SQLException;

	public PreparedStatement getQueryCreateRole() throws SQLException;

	public PreparedStatement getQueryCreateScope() throws SQLException;

	public PreparedStatement getQueryCreateTopic() throws SQLException;

	public PreparedStatement getQueryCreateLocator() throws SQLException;

	public PreparedStatement getQueryCreateVariant() throws SQLException;

	// ****************
	// * SELECT QUERY *
	// ****************

	public PreparedStatement getQueryReadPlayedAssociation(boolean paged) throws SQLException;

	public PreparedStatement getQueryReadPlayedAssociationWithType() throws SQLException;

	public PreparedStatement getQueryReadPlayedAssociationWithTypeAndScope() throws SQLException;

	public PreparedStatement getQueryReadPlayedAssociationWithScope() throws SQLException;

	public PreparedStatement getQueryReadAssociation() throws SQLException;

	public PreparedStatement getQueryReadAssociationWithType() throws SQLException;

	public PreparedStatement getQueryReadAssociationWithTypeAndScope() throws SQLException;

//	public PreparedStatement getQueryReadAssociationWithScope() throws SQLException;

	public PreparedStatement getQueryReadConstructById() throws SQLException;

	public PreparedStatement getQueryReadConstructByItemIdentifier() throws SQLException;

	public PreparedStatement getQueryReadDataType() throws SQLException;

	public PreparedStatement getQueryReadItemIdentifiers() throws SQLException;

	public PreparedStatement getQueryReadNames(boolean paged) throws SQLException;

	public PreparedStatement getQueryReadNamesWithType() throws SQLException;

	public PreparedStatement getQueryReadNamesWithTypeAndScope() throws SQLException;

	public PreparedStatement getQueryReadNamesWithScope() throws SQLException;

	public PreparedStatement getQueryReadOccurrences(boolean paged) throws SQLException;

	public PreparedStatement getQueryReadOccurrencesWithType() throws SQLException;

	public PreparedStatement getQueryReadOccurrencesWithTypeAndScope() throws SQLException;

	public PreparedStatement getQueryReadOccurrencesWithScope() throws SQLException;

	public PreparedStatement getQueryReadPlayer() throws SQLException;

	public PreparedStatement getQueryReadReifier() throws SQLException;

	public PreparedStatement getQueryReadReified() throws SQLException;

	public PreparedStatement getQueryReadRoles(boolean paged) throws SQLException;

	public PreparedStatement getQueryReadRolesWithType() throws SQLException;

	public PreparedStatement getQueryReadPlayedRoles(boolean paged) throws SQLException;

	public PreparedStatement getQueryReadPlayedRolesWithType() throws SQLException;

	public PreparedStatement getQueryReadPlayedRolesWithTypeAndAssociationType() throws SQLException;

	public PreparedStatement getQueryReadRoleTypes() throws SQLException;

	public PreparedStatement getQueryReadSubjectIdentifiers() throws SQLException;

	public PreparedStatement getQueryReadSubjectLocators() throws SQLException;

	public PreparedStatement getQueryReadSupertypes() throws SQLException;

	public PreparedStatement getQueryReadThemes() throws SQLException;

	public PreparedStatement getQueryReadTopicBySubjectIdentifier() throws SQLException;

	public PreparedStatement getQueryReadTopicBySubjectLocator() throws SQLException;

	public PreparedStatement getQueryReadTopicMap() throws SQLException;

	public PreparedStatement getQueryReadTopics() throws SQLException;

	public PreparedStatement getQueryReadTopicsWithType() throws SQLException;

	public PreparedStatement getQueryReadType() throws SQLException;

	public PreparedStatement getQueryReadTypes(boolean paged) throws SQLException;

	public PreparedStatement getQueryReadScope() throws SQLException;

	public PreparedStatement getQueryReadScopeByThemes() throws SQLException;

	public PreparedStatement getQueryReadValue() throws SQLException;

	public PreparedStatement getQueryReadVariants(boolean paged) throws SQLException;

	public PreparedStatement getQueryReadVariantsWithScope() throws SQLException;
	
	// ***************
	// * MERGE QUERY *
	// ***************

	// public PreparedStatement getQueryMergeTopic() throws SQLException;

	// ****************
	// * UPDATE QUERY *
	// ****************

	public PreparedStatement getQueryAddItemIdentifier() throws SQLException;

	public PreparedStatement getQueryAddSubjectIdentifier() throws SQLException;

	public PreparedStatement getQueryAddSubjectLocator() throws SQLException;

	public PreparedStatement getQueryAddThemes(long themeNumber) throws SQLException;

	public PreparedStatement getQueryModifyType() throws SQLException;

	public PreparedStatement getQueryModifyTypes() throws SQLException;

	public PreparedStatement getQueryModifyPlayer() throws SQLException;

	public PreparedStatement getQueryModifyReifier() throws SQLException;

	public PreparedStatement getQueryModifyScope() throws SQLException;

	public PreparedStatement getQueryModifySupertypes() throws SQLException;

	public PreparedStatement getQueryModifyValue() throws SQLException;

	public PreparedStatement getQueryModifyValueWithDatatype() throws SQLException;

	// ****************
	// * REMOVE QUERY *
	// ****************

	public PreparedStatement getQueryDeleteTopicMap() throws SQLException;

	public PreparedStatement getQueryDeleteTopic() throws SQLException;

	public PreparedStatement getQueryDeleteName() throws SQLException;

	public PreparedStatement getQueryDeleteOccurrence() throws SQLException;

	public PreparedStatement getQueryDeleteVariant() throws SQLException;

	public PreparedStatement getQueryDeleteAssociation() throws SQLException;

	public PreparedStatement getQueryDeleteRole() throws SQLException;

	public PreparedStatement getQueryDeleteType() throws SQLException;

	public PreparedStatement getQueryDeleteSupertype() throws SQLException;

	public PreparedStatement getQueryDeleteItemIdentifier() throws SQLException;

	public PreparedStatement getQueryDeleteSubjectIdentifier() throws SQLException;

	public PreparedStatement getQueryDeleteSubjectLocator() throws SQLException;
	
	public PreparedStatement getQueryClearTopicMap() throws SQLException;

	// ***************
	// * INDEX QUERY *
	// ***************

	// PagedConstructIndex

	public PreparedStatement getQueryReadNumberOfNames() throws SQLException;

	public PreparedStatement getQueryReadNumberOfOccurrences() throws SQLException;

	public PreparedStatement getQueryReadNumberOfTypes() throws SQLException;

	public PreparedStatement getQueryReadNumberOfTopicsWithoutSupertypes() throws SQLException;

	public PreparedStatement getQueryReadNumberOfAssociationsPlayed() throws SQLException;

	public PreparedStatement getQueryReadNumberOfRolesPlayed() throws SQLException;

	public PreparedStatement getQueryReadNumberOfVariants() throws SQLException;

	public PreparedStatement getQueryReadNumberOfRoles() throws SQLException;

	// TypeInstanceIndex

	public PreparedStatement getQuerySelectAssociationTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectNameTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectOccurrenceTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectCharacteristicTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectRoleTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectTopicTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectAssociationsByType(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectAssociationsByTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectCharacteristicsByType(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectCharacteristicsByTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectNamesByType(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectNamesByTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByType(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectRolesByType(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectRolesByTypes(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectTopicsByTypes(long typeCount, boolean all, boolean withLimit) throws SQLException;

	// TransitiveTypeInstanceIndex

	public PreparedStatement getQuerySelectAssociationsByTypeTransitive(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectNamesByTypeTransitive(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByTypeTransitive(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectRolesByTypeTransitive(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectTopicsByTypeTransitive(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectTopicsByTypesTransitive(boolean paged) throws SQLException;

	// ScopedIndex

	public PreparedStatement getQueryScopesByThemesUsed() throws SQLException;

	public PreparedStatement getQueryAssociationsByScope(boolean paged) throws SQLException;

	public PreparedStatement getQueryAssociationsByScopes(boolean paged) throws SQLException;

	public PreparedStatement getQueryAssociationsByTheme(boolean paged) throws SQLException;

	public PreparedStatement getQueryAssociationsByThemes(boolean all, boolean paged) throws SQLException;

	public PreparedStatement getQueryAssociationScopes(boolean paged) throws SQLException;

	public PreparedStatement getQueryAssociationThemes(boolean paged) throws SQLException;

	public PreparedStatement getQueryCharacteristicsByScope(boolean paged) throws SQLException;

	public PreparedStatement getQueryNamesByScope(boolean paged) throws SQLException;

	public PreparedStatement getQueryNamesByScopes(boolean paged) throws SQLException;

	public PreparedStatement getQueryNamesByTheme(boolean paged) throws SQLException;

	public PreparedStatement getQueryNamesByThemes(boolean all, boolean paged) throws SQLException;

	public PreparedStatement getQueryNameScopes(boolean paged) throws SQLException;

	public PreparedStatement getQueryNameThemes(boolean paged) throws SQLException;

	public PreparedStatement getQueryOccurrencesByScope(boolean paged) throws SQLException;

	public PreparedStatement getQueryOccurrencesByScopes(boolean paged) throws SQLException;

	public PreparedStatement getQueryOccurrencesByTheme(boolean paged) throws SQLException;

	public PreparedStatement getQueryOccurrencesByThemes(boolean all, boolean paged) throws SQLException;

	public PreparedStatement getQueryOccurrenceScopes(boolean paged) throws SQLException;

	public PreparedStatement getQueryOccurrenceThemes(boolean paged) throws SQLException;

	public PreparedStatement getQueryScopables(boolean paged) throws SQLException;

	public PreparedStatement getQueryVariantsByScope(boolean paged) throws SQLException;

	public PreparedStatement getQueryVariantsByScopes(boolean paged) throws SQLException;

	public PreparedStatement getQueryVariantsByTheme(boolean paged) throws SQLException;

	public PreparedStatement getQueryVariantsByThemes(boolean all, boolean paged) throws SQLException;

	public PreparedStatement getQueryVariantScopes(boolean paged) throws SQLException;

	public PreparedStatement getQueryVariantThemes(boolean paged) throws SQLException;

	// LiteralIndex

	public PreparedStatement getQuerySelectCharacteristics(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectCharacteristicsByValue(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectCharacteristicsByDatatype(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectCharacteristicsByPattern(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectCharacteristicsByPatternAndDatatype(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectDatatypeAwaresByDatatype(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectNames(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectNamesByValue() throws SQLException;

	public PreparedStatement getQuerySelectNamesByPattern() throws SQLException;

	public PreparedStatement getQuerySelectOccurrences(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByDatatype(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByDateRange(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByRange(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByValue() throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByPattern() throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByValueAndDatatype(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectOccurrencesByPatternAndDatatype() throws SQLException;

	public PreparedStatement getQuerySelectVariants(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectVariantsByDatatype() throws SQLException;

	public PreparedStatement getQuerySelectVariantsByValue() throws SQLException;

	public PreparedStatement getQuerySelectVariantsByPattern() throws SQLException;

	public PreparedStatement getQuerySelectVariantsByValueAndDatatype() throws SQLException;

	public PreparedStatement getQuerySelectVariantsByPatternAndDatatype() throws SQLException;

	// IdentityIndex

	public PreparedStatement getQuerySelectItemIdentifiers(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectSubjectIdentifiers(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectSubjectLocators(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectConstructsByIdentitifer(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectConstructsByItemIdentitifer(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectTopicsBySubjectIdentitifer(boolean withLimit) throws SQLException;

	public PreparedStatement getQuerySelectTopicsBySubjectLocator(boolean withLimit) throws SQLException;

	// SupertypeSubtypeIndex

	public PreparedStatement getQuerySelectDirectSubtypes(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectTopicsWithoutSubtypes(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectSubtypesOfTopic(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectSubtypesOfTopics(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectSubtypes(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectDirectSupertypes(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectTopicsWithoutSupertypes(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectSupertypesOfTopic(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectSupertypesOfTopics(boolean paged) throws SQLException;

	public PreparedStatement getQuerySelectSupertypes(boolean paged) throws SQLException;

	// *****************
	// * PERFORM QUERY *
	// *****************

	public PreparedStatement getPerformMergeTopics() throws SQLException;

	// ******************
	// * REVISION QUERY *
	// ******************

	public PreparedStatement getQueryCreateRevision() throws SQLException;

	public PreparedStatement getQueryCreateChangeset() throws SQLException;

	public PreparedStatement getQueryCreateTag() throws SQLException;

	public PreparedStatement getQueryCreateMetadata() throws SQLException;

	public PreparedStatement getQueryReadFirstRevision() throws SQLException;

	public PreparedStatement getQueryReadLastRevision() throws SQLException;

	public PreparedStatement getQueryReadPastRevision() throws SQLException;

	public PreparedStatement getQueryReadFutureRevision() throws SQLException;

	public PreparedStatement getQueryReadChangesets() throws SQLException;

	public PreparedStatement getQueryReadLastModification() throws SQLException;

	public PreparedStatement getQueryReadLastModificationOfTopic() throws SQLException;

	public PreparedStatement getQueryReadTimestamp() throws SQLException;

	public PreparedStatement getQueryReadRevisionsByTopic() throws SQLException;

	public PreparedStatement getQueryReadRevisionsByAssociationType() throws SQLException;

	public PreparedStatement getQueryReadChangesetsByTopic() throws SQLException;

	public PreparedStatement getQueryReadChangesetsByAssociationType() throws SQLException;

	public PreparedStatement getQueryReadRevisionByTag() throws SQLException;

	public PreparedStatement getQueryReadRevisionByTimestamp() throws SQLException;

	public PreparedStatement getQueryReadMetadata() throws SQLException;

	public PreparedStatement getQueryReadMetadataByKey() throws SQLException;

	public PreparedStatement getQueryReadHistory() throws SQLException;
}
