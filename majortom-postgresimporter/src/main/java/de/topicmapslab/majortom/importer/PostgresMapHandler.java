/**
 * 
 */
package de.topicmapslab.majortom.importer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.PostGreSqlConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.PostGreSqlSession;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryBuilder;
import de.topicmapslab.majortom.importer.helper.Association;
import de.topicmapslab.majortom.importer.helper.Name;
import de.topicmapslab.majortom.importer.helper.Occurrence;
import de.topicmapslab.majortom.importer.helper.Role;
import de.topicmapslab.majortom.importer.helper.Variant;
import de.topicmapslab.majortom.importer.model.IHandler;

import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.*;

/**
 * @author Hannes Niederhausen
 * 
 */
public class PostgresMapHandler implements IHandler{

	private static Logger logger = LoggerFactory.getLogger(PostgresMapHandler.class);

	private PostGreSqlConnectionProvider provider;
	private PostGreSqlSession session;
	private Long topicMapId;
	private Sql99QueryBuilder builder;

	private Connection connection;
	
	private Map<String, Long> topicCache = new HashMap<String, Long>();

	/**
	 * Constructor
	 * 
	 * @throws SQLException
	 */
	public PostgresMapHandler() throws MIOException {
		try {
			InputStream is = getClass().getResourceAsStream("/db.properties");
			Properties properties = new Properties();
			if (is==null)
				throw new MIOException("Could not load db.properties!");
			properties.load(is);
			
			init(properties);
		} catch (IOException e) {
			throw new MIOException(e);
		}
	}
	
	private void init(Properties properties) throws MIOException {
		
		try {
			String localhost = properties.getProperty(HOST);
			String db = properties.getProperty(DATABASE);
			String user = properties.getProperty(USERNAME);
			String password = properties.getProperty(PASSWORD);

			provider = new PostGreSqlConnectionProvider(localhost, db, user, password);
			int r = provider.getDatabaseState();
			if (r == IConnectionProvider.STATE_DATABASE_IS_EMPTY)
				provider.createSchema();
			connection = provider.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new MIOException(e);
		}
	}

	/**
	 * Constructor
	 * 
	 * @throws SQLException
	 */
	public PostgresMapHandler(Properties properties) throws MIOException {
		init(properties);
	}
	
	/**
	 * Commits the last statements
	 * 
	 * @throws MIOException
	 */
	public void commit() throws MIOException {
		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new MIOException(e);
		}
	}

	/**
	 * Handler for start event prepareing the connection
	 * 
	 * @throws MIOException
	 */
	public void start() throws MIOException {
		session = provider.openSession();
		builder = new Sql99QueryBuilder(session);
	}

	/**
	 * Closes the connection
	 * 
	 * @throws MIOException
	 */
	public void end() throws MIOException {
		try {
			commit();
			session.close();
			session = null;
		} catch (SQLException e) {
			throw new MIOException(e);
		}
	}

	/**
	 * Returns the id of the topic map for the given locator. If it does not exist it will be created.
	 * 
	 * @param locator
	 *            locator of the tm
	 * @return the id of the tm
	 * @throws MIOException
	 */
	public long getTopicMapId(String locator) throws MIOException {
		try {
			PreparedStatement stm = builder.getQueryReadTopicMap();
			stm.setString(1, locator);

			topicMapId = -1l;

			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				topicMapId = rs.getLong(1);
			} else {
				rs.close();

				stm = builder.getQueryCreateTopicMap();

				for (int i = 1; i < 5; i++) {
					stm.setString(i, locator);
				}

				stm.execute();
				rs = stm.getResultSet();
				if (rs.next()) {
					topicMapId = rs.getLong(1);
				}
				rs.close();
			}

			if (topicMapId == -1)
				throw new MIOException("Could not create topic map");

			// clear topic map
			PreparedStatement stmt = builder.getQueryClearTopicMap();
			stmt.setLong(1, topicMapId);
			stmt.setLong(2, topicMapId);
			stmt.setLong(3, topicMapId);
			stmt.setLong(4, topicMapId);
			stmt.setLong(5, topicMapId);
			stmt.execute();

			// returning id
			return topicMapId;
		} catch (Exception e) {
			e.printStackTrace();
			throw new MIOException(e);
		}

	}

	/**
	 * Adds the given association to the database.
	 * 
	 * @param assoc
	 * @throws MIOException 
	 */
	public void addAssociation(Association assoc) throws MIOException {
		try {
			long assocID = -1;
			PreparedStatement stm = null;
			stm = builder.getQueryCreateAssociationWithScope();

			stm.setLong(1, topicMapId);
			stm.setLong(2, topicMapId);
			stm.setLong(3, getTopic(assoc.getType()));
			stm.setLong(4, getScopeId(assoc.getThemes()));

			stm.execute();
			ResultSet rs = stm.getGeneratedKeys();
			if (rs.next()) {
				assocID = rs.getLong(1);
			} else {
				throw new MIOException("Could not create association for " + assoc.getType().getIRI());
			}
			
			for (Role role : assoc.getRoles()) {
				addRole(assocID, role);
			}
			
			if (assoc.getReifier()!=null) {
				addReifier(assocID, assoc.getReifier());
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new MIOException(e);
		}

	}

	/**
	 * Adds a role to the association
	 * 
	 * @param assocID the id of the parent association
	 * @param role the role helper 
	 * @throws MIOException
	 */
	private void addRole(long assocID, Role role) throws MIOException {
		try {
			PreparedStatement stm = builder.getQueryCreateRole();
			stm.setLong(1, topicMapId);
			stm.setLong(2, assocID);
			stm.setLong(3, getTopic(role.getRoleType()));
			stm.setLong(4, getTopic(role.getRolePlayer()));
			
			stm.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new MIOException(e);
		}
	}

	/**
	 * Adds a name to the topic map
	 * 
	 * @param name
	 * @throws MIOException
	 */
	public void addName(Name name) throws MIOException {
		try {
			PreparedStatement stm = builder.getQueryCreateNameWithScope();

			stm.setLong(1, name.getTopicMapId());
			stm.setLong(2, name.getParentId());
			stm.setLong(3, getTopic(name.getTypeRef()));
			stm.setString(4, name.getValue());
			stm.setLong(5, getScopeId(name.getThemeRefs()));
			stm.execute();
			
			ResultSet rs = stm.getGeneratedKeys();
			if (rs.next()) {
				long id = rs.getLong("id");
				if (name.getReifier()!=null) {
					addReifier(id, name.getReifier());
				}
				for (Variant v : name.getVariants()) {
					addVariant(id, v);
				}
			}
			rs.close();
			
			
		} catch (SQLException e) {
			throw new MIOException(e);
		}
	}

	/**
	 * Adds an occurrence
	 * 
	 * @param occurrence
	 * @throws MIOException 
	 */
	public void addOccurrence(Occurrence occurrence) throws MIOException {
		try {
			PreparedStatement stm = builder.getQueryCreateOccurrenceWithScope();

			stm.setString(1, occurrence.getDatatype());
			stm.setString(2, occurrence.getDatatype());
			stm.setLong(3, occurrence.getTopicMapId());
			stm.setLong(4, occurrence.getParentId());
			stm.setLong(5, getTopic(occurrence.getTypeRef()));
			stm.setString(6, occurrence.getValue());
			stm.setLong(7, getScopeId(occurrence.getThemeRefs()));
			stm.setString(8, occurrence.getDatatype());
			stm.execute();

			if (occurrence.getReifier()!=null) {
				ResultSet rs = stm.getResultSet();
				if (rs.next()) {
					long id = rs.getLong("id");
					addReifier(id, occurrence.getReifier());
				}
				rs.close();
			}

		} catch (SQLException e) {
			throw new MIOException(e);
		}

	}

	/**
	 * Returns the topic id for the given identifier. It
	 * it isn't in the db, it will be created.
	 * 
	 * @param ref the identifier of the topic
	 * @return the id of the topic
	 * @throws MIOException
	 */
	public long getTopic(IRef ref) throws MIOException {
		try {

			// check cache
			Long val = topicCache.get(ref.getIRI());
			if (val != null)
				return val.longValue();
			
			PreparedStatement stm = null;

			// create read statement
			switch (ref.getType()) {
			case IRef.SUBJECT_IDENTIFIER:
				stm = builder.getQueryReadTopicBySubjectIdentifier();
				break;
			case IRef.ITEM_IDENTIFIER:
				stm = builder.getQueryReadConstructByItemIdentifier();
				break;
			case IRef.SUBJECT_LOCATOR:
				stm = builder.getQueryReadTopicBySubjectLocator();
				break;
			}

			// set variables
			switch (ref.getType()) {
			case IRef.SUBJECT_IDENTIFIER:
			case IRef.SUBJECT_LOCATOR:
				stm.setLong(1, topicMapId);
				stm.setString(2, ref.getIRI());
				break;
			case IRef.ITEM_IDENTIFIER:
				stm.setString(1, ref.getIRI());
				for (int i = 2; i < 8; i++)
					stm.setLong(i, topicMapId);
				break;
			}

			long id = -1;

			ResultSet rs = stm.executeQuery();
			if (rs.next()) {
				id = rs.getLong(1);
			}
			rs.close();
			
			if (id == -1) {
				id = createTopic(ref);
			}
			
			topicCache.put(ref.getIRI(), id);
			
			return id;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * adds an identifier
	 * 
	 * @param topicId id of the topic
	 * @param ref the uri 
	 * @param type the type of identifier
	 * @throws MIOException
	 */
	public void addIdentifier(long topicId, String ref, int type) throws MIOException {
		try {
			PreparedStatement stm = null;

			switch (type) {
			case IRef.SUBJECT_IDENTIFIER:
				stm = builder.getQueryAddSubjectIdentifier();
				break;
			case IRef.ITEM_IDENTIFIER:
				stm = builder.getQueryAddItemIdentifier();
				break;
			case IRef.SUBJECT_LOCATOR:
				stm = builder.getQueryAddSubjectLocator();
				break;
			}

			stm.setString(1, ref);
			stm.setString(2, ref);
			stm.setLong(3, topicId);
			stm.setString(4, ref);

			stm.execute();
		} catch (SQLException e) {
			throw new MIOException(e);
		}
	}

	/**
	 * Adds a type to the topic with the given id
	 * 
	 * @param currTopicId the current topic id
	 * @param arg0 the reference of the typing topic
	 * @throws MIOException
	 * @throws  
	 */
	public void addType(long currTopicId, IRef arg0) throws MIOException {
		try {
			long typeId = getTopic(arg0);
			
			PreparedStatement stmt = builder.getQueryModifyTypes();
			stmt.setLong(1, currTopicId);
			stmt.setLong(2, typeId);
			stmt.setLong(3, currTopicId);
			stmt.setLong(4, typeId);
			stmt.execute();
			
			
		} catch (SQLException e) {
			throw new MIOException(e);
		}
		
	}

	private long createTopic(IRef ref) {
		long id = -1;
		try {
			PreparedStatement stm = builder.getQueryCreateTopic();
			stm.setLong(1, topicMapId);
			stm.setLong(2, topicMapId);

			stm.execute();
			ResultSet rs = stm.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getLong(1);
			}
			rs.close();

			addIdentifier(id, ref.getIRI(), ref.getType());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return id;
	}

	private long getScopeId(List<IRef> themes) throws MIOException {
		try {
			long id = -1;

			PreparedStatement stmt = builder.getQueryReadScopeByThemes();

			ArrayList<Long> themeIds = new ArrayList<Long>();

			for (IRef ref : themes) {
				themeIds.add(getTopic(ref));
			}

			stmt.setArray(1, session.getConnection().createArrayOf("bigint", themeIds.toArray(new Long[themeIds.size()])));
			stmt.setLong(2, themeIds.size());

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				id = rs.getLong("id_scope");
			} else {
				rs.close();

				// create new scope
				stmt = builder.getQueryCreateScope();
				stmt.setLong(1, topicMapId);
				stmt.execute();

				rs = stmt.getGeneratedKeys();
				rs.next();
				id = rs.getLong("id");
				rs.close();

				// adding themes:
				stmt = builder.getQueryAddThemes(themes.size());
				int i = 0;
				for (Long themeId : themeIds) {
					stmt.setLong(i * 2 + 1, id);
					stmt.setLong(i * 2 + 2, themeId);
					i++;
				}
				stmt.execute();
			}

			return id;
		} catch (SQLException e) {
			logger.error("", e);
			throw new MIOException(e);
		}

	}
	
	private void addVariant(long id, Variant variant) throws MIOException {
		try {
			long scopeId = getScopeId(variant.getThemeRefs());
			
			PreparedStatement stmt = builder.getQueryCreateVariant();
			stmt.setString(1, variant.getDatatype());
			stmt.setString(2, variant.getDatatype());
			stmt.setLong(3, topicMapId);
			stmt.setLong(4, id);
			stmt.setString(5, variant.getValue());
			stmt.setLong(6, scopeId);
			stmt.setString(7, variant.getDatatype());
			stmt.execute();
			
			
			if (variant.getReifier() != null) {
				ResultSet rs = stmt.getResultSet();
				rs.next();
				long variantId = rs.getLong("id");
				rs.close();

				stmt = builder.getQueryModifyVariantReifier();

				stmt.setLong(1, getTopic(variant.getReifier()));
				stmt.setLong(2, variantId);

				stmt.execute();
			}
		} catch (SQLException e) {
			throw new MIOException(e);
		}
		
	}

	/**
	 * Adds a reifier
	 * @param id reifiable id
	 * @param reifierRef the ref to the reifier topic
	 * @throws MIOException 
	 */
	private void addReifier(long id, IRef reifierRef) throws MIOException {
		try {
			PreparedStatement stmt = builder.getQueryModifyReifier();
			stmt.setLong(1, getTopic(reifierRef));
			stmt.setLong(2, id);
			
			stmt.execute();
		} catch (SQLException e) {
			throw new MIOException(e);
		}
	}
}
