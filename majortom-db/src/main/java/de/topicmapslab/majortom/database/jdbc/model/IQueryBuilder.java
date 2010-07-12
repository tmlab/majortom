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

import de.topicmapslab.majortom.model.core.IConstruct;

/**
 * @author Sven Krosse
 * 
 */
public interface IQueryBuilder {

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

	public PreparedStatement getQueryReadPlayedAssociation() throws SQLException;

	public PreparedStatement getQueryReadPlayedAssociationWithType() throws SQLException;

	public PreparedStatement getQueryReadPlayedAssociationWithTypeAndScope() throws SQLException;

	public PreparedStatement getQueryReadPlayedAssociationWithScope() throws SQLException;

	public PreparedStatement getQueryReadAssociation() throws SQLException;

	public PreparedStatement getQueryReadAssociationWithType() throws SQLException;

	public PreparedStatement getQueryReadAssociationWithTypeAndScope() throws SQLException;

	public PreparedStatement getQueryReadAssociationWithScope() throws SQLException;

	public PreparedStatement getQueryReadConstructById(Class<? extends IConstruct> clazz) throws SQLException;

	public PreparedStatement getQueryReadConstructByItemIdentifier(Class<? extends IConstruct> clazz) throws SQLException;

	public PreparedStatement getQueryReadDataType() throws SQLException;

	public PreparedStatement getQueryReadItemIdentifiers() throws SQLException;

	public PreparedStatement getQueryReadNames() throws SQLException;

	public PreparedStatement getQueryReadNamesWithType() throws SQLException;

	public PreparedStatement getQueryReadNamesWithTypeAndScope() throws SQLException;

	public PreparedStatement getQueryReadNamesWithScope() throws SQLException;

	public PreparedStatement getQueryReadOccurrences() throws SQLException;

	public PreparedStatement getQueryReadOccurrencesWithType() throws SQLException;

	public PreparedStatement getQueryReadOccurrencesWithTypeAndScope() throws SQLException;

	public PreparedStatement getQueryReadOccurrencesWithScope() throws SQLException;

	public PreparedStatement getQueryReadPlayer() throws SQLException;

	public PreparedStatement getQueryReadReifier() throws SQLException;

	public PreparedStatement getQueryReadReified() throws SQLException;

	public PreparedStatement getQueryReadRoles() throws SQLException;

	public PreparedStatement getQueryReadRolesWithType() throws SQLException;

	public PreparedStatement getQueryReadPlayedRoles() throws SQLException;

	public PreparedStatement getQueryReadPlayedRolesWithType() throws SQLException;

	public PreparedStatement getQueryReadPlayedRolesWithTypeAndAssociationType() throws SQLException;

	public PreparedStatement getQueryReadRoleTypes() throws SQLException;

	public PreparedStatement getQueryReadSubjectIdentifiers() throws SQLException;

	public PreparedStatement getQueryReadSubjectLocators() throws SQLException;

	public PreparedStatement getQueryReadSupertypes() throws SQLException;

	public PreparedStatement getQueryReadTopicBySubjectIdentifier() throws SQLException;

	public PreparedStatement getQueryReadTopicBySubjectLocator() throws SQLException;
	
	public PreparedStatement getQueryReadTopicMap() throws SQLException;

	public PreparedStatement getQueryReadTopics() throws SQLException;

	public PreparedStatement getQueryReadTopicsWithType() throws SQLException;

	public PreparedStatement getQueryReadType() throws SQLException;

	public PreparedStatement getQueryReadTypes() throws SQLException;

	public PreparedStatement getQueryReadScope() throws SQLException;
	
	public PreparedStatement getQueryReadScopeByThemes(long themeNumber) throws SQLException;

	public PreparedStatement getQueryReadValue() throws SQLException;

	public PreparedStatement getQueryReadVariants() throws SQLException;

	public PreparedStatement getQueryReadVariantsWithScope() throws SQLException;

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
}
