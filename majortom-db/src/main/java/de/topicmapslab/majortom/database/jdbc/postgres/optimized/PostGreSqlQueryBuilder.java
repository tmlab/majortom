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
package de.topicmapslab.majortom.database.jdbc.postgres.optimized;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlIndexQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.query.IPostGreSqlSelectQueries;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryBuilder;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

/**
 * @author Sven Krosse
 * 
 */
public class PostGreSqlQueryBuilder extends Sql99QueryBuilder {

	/**
	 * @param provider
	 *            the JDBC connection provider to create the {@link PreparedStatement}
	 */
	public PostGreSqlQueryBuilder(PostGreSqlConnectionProvider provider) {
		super(provider);
	}

	/**
	 * @return the processor
	 */
	public PostGreSqlQueryProcessor getProcessor() {
		return (PostGreSqlQueryProcessor) super.getProcessor();
	}

	/**
	 * {@inheritDoc}
	 */
	public void close() {
		try {
			for (Field field : getClass().getDeclaredFields()) {
				if (PreparedStatement.class.equals(field.getType())) {
					PreparedStatement stmt = (PreparedStatement) field.get(this);
					if (stmt != null) {
						stmt.cancel();
						stmt.close();
					}
				}
			}
		} catch (Exception e) {
			throw new TopicMapStoreException("Cannot close prepared statements!", e);
		}
	}

	// ****************
	// * SELECT QUERY *
	// ****************

	private PreparedStatement preparedStatementReadSupertypes;
	private PreparedStatement preparedStatementReadBestLabel;
	private PreparedStatement preparedStatementReadBestLabelWithTheme;
	private PreparedStatement preparedStatementReadScopeByThemes;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadScopeByThemes() throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureScopeByThemes()) {
			return super.getQueryReadScopeByThemes();
		}
		if (preparedStatementReadScopeByThemes == null) {
			this.preparedStatementReadScopeByThemes = getWriterConnection().prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SCOPES_BY_THEMES);
		}
		return preparedStatementReadScopeByThemes;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryReadSupertypes() throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTransitiveSupertypes()) {
			return super.getQueryReadSupertypes();
		}
		if (this.preparedStatementReadSupertypes == null) {
			this.preparedStatementReadSupertypes = getWriterConnection().prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_SUPERTYPES);
		}
		return this.preparedStatementReadSupertypes;
	}
	
	/**
	 * @since 1.1.2
	 */
	public PreparedStatement getQueryReadBestLabel() throws SQLException{
		if (this.preparedStatementReadBestLabel == null) {
			this.preparedStatementReadBestLabel = getWriterConnection().prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_BEST_LABEL);
		}
		return this.preparedStatementReadBestLabel;
	}
	
	/**
	 * @since 1.1.2
	 */
	public PreparedStatement getQueryReadBestLabelWithTheme() throws SQLException{
		if (this.preparedStatementReadBestLabelWithTheme == null) {
			this.preparedStatementReadBestLabelWithTheme = getWriterConnection().prepareStatement(IPostGreSqlSelectQueries.QUERY_READ_BEST_LABEL_WITH_THEME);
		}
		return this.preparedStatementReadBestLabelWithTheme;
	}

	// TransitiveTypeInstanceIndex

	private PreparedStatement preparedStatementIndexAssociationsByTypeTransitive;
	private PreparedStatement preparedStatementIndexRolesByTypeTransitive;
	private PreparedStatement preparedStatementIndexNamesByTypeTransitive;
	private PreparedStatement preparedStatementIndexOccurrencesByTypeTransitive;
	private PreparedStatement preparedStatementIndexTopicsByTypeTransitive;
	private PreparedStatement preparedStatementIndexTopicsByTypesTransitive;
	private PreparedStatement preparedStatementIndexAssociationsByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexRolesByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexNamesByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexOccurrencesByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexTopicsByTypeTransitivePaged;
	private PreparedStatement preparedStatementIndexTopicsByTypesTransitivePaged;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectAssociationsByTypeTransitive(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTypesAndSubtypes()) {
			return super.getQuerySelectAssociationsByTypeTransitive(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexAssociationsByTypeTransitivePaged == null) {
				this.preparedStatementIndexAssociationsByTypeTransitivePaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
			}
			return this.preparedStatementIndexAssociationsByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexAssociationsByTypeTransitive == null) {
			this.preparedStatementIndexAssociationsByTypeTransitive = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_ASSOCIATIONS_BY_TYPE);
		}
		return this.preparedStatementIndexAssociationsByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectRolesByTypeTransitive(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTypesAndSubtypes()
				|| !getProcessor().getConnectionProvider().existsProcedureTypesAndSubtypesArray()) {
			return super.getQuerySelectRolesByTypeTransitive(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexRolesByTypeTransitivePaged == null) {
				this.preparedStatementIndexRolesByTypeTransitivePaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_ROLES_BY_TYPE);
			}
			return this.preparedStatementIndexRolesByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexRolesByTypeTransitive == null) {
			this.preparedStatementIndexRolesByTypeTransitive = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_ROLES_BY_TYPE);
		}
		return this.preparedStatementIndexRolesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectNamesByTypeTransitive(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTypesAndSubtypes()
				|| !getProcessor().getConnectionProvider().existsProcedureTypesAndSubtypesArray()) {
			return super.getQuerySelectNamesByTypeTransitive(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexNamesByTypeTransitivePaged == null) {
				this.preparedStatementIndexNamesByTypeTransitivePaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_NAMES_BY_TYPE);
			}
			return this.preparedStatementIndexNamesByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexNamesByTypeTransitive == null) {
			this.preparedStatementIndexNamesByTypeTransitive = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_NAMES_BY_TYPE);
		}
		return this.preparedStatementIndexNamesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectOccurrencesByTypeTransitive(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTypesAndSubtypes()
				|| !getProcessor().getConnectionProvider().existsProcedureTypesAndSubtypesArray()) {
			return super.getQuerySelectOccurrencesByTypeTransitive(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexOccurrencesByTypeTransitivePaged == null) {
				this.preparedStatementIndexOccurrencesByTypeTransitivePaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_OCCURRENCES_BY_TYPE);
			}
			return this.preparedStatementIndexOccurrencesByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexOccurrencesByTypeTransitive == null) {
			this.preparedStatementIndexOccurrencesByTypeTransitive = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_OCCURRENCES_BY_TYPE);
		}
		return this.preparedStatementIndexOccurrencesByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypeTransitive(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTypesAndSubtypes()) {
			return super.getQuerySelectTopicsByTypeTransitive(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexTopicsByTypeTransitivePaged == null) {
				this.preparedStatementIndexTopicsByTypeTransitivePaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_TOPICS_BY_TYPE);
			}
			return this.preparedStatementIndexTopicsByTypeTransitivePaged;
		}
		if (this.preparedStatementIndexTopicsByTypeTransitive == null) {
			this.preparedStatementIndexTopicsByTypeTransitive = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_TOPICS_BY_TYPE);
		}
		return this.preparedStatementIndexTopicsByTypeTransitive;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectTopicsByTypesTransitive(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTopicsByTypeTransitive()) {
			return super.getQuerySelectTopicsByTypesTransitive(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexTopicsByTypesTransitivePaged == null) {
				this.preparedStatementIndexTopicsByTypesTransitivePaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.Paged.QUERY_SELECT_TOPICS_BY_TYPES);
			}
			return this.preparedStatementIndexTopicsByTypesTransitivePaged;
		}
		if (this.preparedStatementIndexTopicsByTypesTransitive == null) {
			this.preparedStatementIndexTopicsByTypesTransitive = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QueryTransitiveTypeInstanceIndex.NonPaged.QUERY_SELECT_TOPICS_BY_TYPES);
		}
		return this.preparedStatementIndexTopicsByTypesTransitive;
	}

	// ScopeIndex
	private PreparedStatement preparedStatementIndexScopesByThemesUsed;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQueryScopesByThemesUsed() throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureScopeByThemes()) {
			return super.getQueryScopesByThemesUsed();
		}
		if (this.preparedStatementIndexScopesByThemesUsed == null) {
			preparedStatementIndexScopesByThemesUsed = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QueryScopeIndex.QUERY_SELECT_SCOPES_BY_THEMES_USED);
		}
		return preparedStatementIndexScopesByThemesUsed;
	}

	// SupertypeSubtypeIndex

	private PreparedStatement preparedStatementIndexSubtypesOfTopic;
	private PreparedStatement preparedStatementIndexSubtypesOfTopics;
	private PreparedStatement preparedStatementIndexSupertypesOfTopic;
	private PreparedStatement preparedStatementIndexSupertypesOfTopics;
	private PreparedStatement preparedStatementIndexSubtypesOfTopicPaged;
	private PreparedStatement preparedStatementIndexSubtypesOfTopicsPaged;
	private PreparedStatement preparedStatementIndexSupertypesOfTopicPaged;
	private PreparedStatement preparedStatementIndexSupertypesOfTopicsPaged;

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypesOfTopic(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTransitiveSubtypes()) {
			return super.getQuerySelectSubtypesOfTopic(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexSubtypesOfTopicPaged == null) {
				this.preparedStatementIndexSubtypesOfTopicPaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUBTYPES_OF_TOPIC);
			}
			return this.preparedStatementIndexSubtypesOfTopicPaged;
		}
		if (this.preparedStatementIndexSubtypesOfTopic == null) {
			this.preparedStatementIndexSubtypesOfTopic = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUBTYPES_OF_TOPIC);
		}
		return this.preparedStatementIndexSubtypesOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSubtypesOfTopics(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTransitiveSubtypesArray()) {
			return super.getQuerySelectSubtypesOfTopics(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexSubtypesOfTopicsPaged == null) {
				this.preparedStatementIndexSubtypesOfTopicsPaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUBTYPES_OF_TOPICS);
			}
			return this.preparedStatementIndexSubtypesOfTopicsPaged;
		}
		if (this.preparedStatementIndexSubtypesOfTopics == null) {
			this.preparedStatementIndexSubtypesOfTopics = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUBTYPES_OF_TOPICS);
		}
		return this.preparedStatementIndexSubtypesOfTopics;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypesOfTopic(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTransitiveSupertypes()) {
			return super.getQuerySelectSupertypesOfTopic(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexSupertypesOfTopicPaged == null) {
				this.preparedStatementIndexSupertypesOfTopicPaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUPERTYPES_OF_TOPIC);
			}
			return this.preparedStatementIndexSupertypesOfTopicPaged;
		}
		if (this.preparedStatementIndexSupertypesOfTopic == null) {
			this.preparedStatementIndexSupertypesOfTopic = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUPERTYPES_OF_TOPIC);
		}
		return this.preparedStatementIndexSupertypesOfTopic;
	}

	/**
	 * {@inheritDoc}
	 */
	public PreparedStatement getQuerySelectSupertypesOfTopics(boolean paged) throws SQLException {
		/*
		 * check if optimisation procedure exists
		 */
		if (!getProcessor().getConnectionProvider().existsProcedureTransitiveSupertypesArray()) {
			return super.getQuerySelectSupertypesOfTopics(paged);
		}
		if (paged) {
			if (this.preparedStatementIndexSupertypesOfTopicsPaged == null) {
				this.preparedStatementIndexSupertypesOfTopicsPaged = getWriterConnection().prepareStatement(
						IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.Paged.QUERY_SELECT_SUPERTYPES_OF_TOPICS);
			}
			return this.preparedStatementIndexSupertypesOfTopicsPaged;
		}
		if (this.preparedStatementIndexSupertypesOfTopics == null) {
			this.preparedStatementIndexSupertypesOfTopics = getWriterConnection().prepareStatement(
					IPostGreSqlIndexQueries.QuerySupertypeSubtypeIndex.NonPaged.QUERY_SELECT_SUPERTYPES_OF_TOPICS);
		}
		return this.preparedStatementIndexSupertypesOfTopics;
	}

}
