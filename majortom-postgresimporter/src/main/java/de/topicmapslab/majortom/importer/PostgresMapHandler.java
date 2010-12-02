/**
 * 
 */
package de.topicmapslab.majortom.importer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.semagia.mio.IRef;
import com.semagia.mio.MIOException;

import de.topicmapslab.majortom.database.jdbc.model.IConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.PostGreSqlConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.postgres.optimized.PostGreSqlSession;
import de.topicmapslab.majortom.database.jdbc.postgres.sql99.Sql99QueryBuilder;
import de.topicmapslab.majortom.importer.helper.Characteristic;
import de.topicmapslab.majortom.importer.helper.Occurrence;

/**
 * @author Hannes Niederhausen
 * 
 */
public class PostgresMapHandler {

	private static Logger logger = LoggerFactory.getLogger(PostgresMapHandler.class);

	private PostGreSqlConnectionProvider provider;
	private PostGreSqlSession session;
	private Long topicMapId;
	private Sql99QueryBuilder builder;

	/**
	 * Constructor
	 * 
	 * @throws SQLException
	 */
	public PostgresMapHandler() throws SQLException {
		provider = new PostGreSqlConnectionProvider("localhost", "it", "postgres", "lala01");
		int r = provider.getDatabaseState();
		if (r == IConnectionProvider.STATE_DATABASE_IS_EMPTY)
			provider.createSchema();
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
	 * Adds a name to the topic map
	 * 
	 * @param name
	 * @throws MIOException
	 */
	public void addName(Characteristic name) throws MIOException {
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
			}
			
			
		} catch (SQLException e) {
			throw new MIOException(e);
		}
	}

	/**
	 * Adds an occurrence
	 * 
	 * @param occurrence
	 */
	public void addOccurrence(Occurrence occurrence) {
		throw new UnsupportedOperationException();
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

			if (id == -1) {
				id = createTopic(ref);
			}
			
			rs.close();
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
			}

			return id;
		} catch (SQLException e) {
			logger.error("", e);
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
