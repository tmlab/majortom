/**
 * 
 */
package de.topicmapslab.majortom.importer.file;

import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.DATABASE;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.HOST;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.PASSWORD;
import static de.topicmapslab.majortom.importer.IDatabasePropertiesConstants.USERNAME;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.TreeBidiMap;

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

/**
 * Special Map Handler to write topic map constructs to SQL commands.
 * 
 * @author Sven Krosse
 * 
 */
public class FileWriterMapHandler implements IHandler {

	/**
	 * 
	 */
	private static final String NULL = "NULL";

	/**
	 * the UTF-8 constant
	 */
	private static final String UTF_8 = "UTF-8";

	/**
	 * wildcard variable
	 */
	private static final String PLACEHOLDER = "{0}";

	/**
	 * current construct Id
	 */
	private long id;
	/**
	 * current locator Id
	 */
	private long locatorId;
	/**
	 * current scope Id
	 */
	private long scopeId;

	/**
	 * the PostGreSql connection provider
	 */
	private PostGreSqlConnectionProvider provider;
	/**
	 * the session
	 */
	private PostGreSqlSession session;
	/**
	 * the topic map id
	 */
	private long topicMapId = -1;
	/**
	 * the query builder of MaJorToM
	 */
	private Sql99QueryBuilder builder;
	/**
	 * the output stream to write the SQL commands
	 */
	private OutputStream os;
	/**
	 * the current connection
	 */
	private Connection connection;

	/**
	 * topic cache, storing the topic references to the topic Id
	 */
	private Map<IRef, Long> topicCache = new HashMap<IRef, Long>();
	/**
	 * locator cache storing the reference to the locator Id
	 */
	private BidiMap locatorCache = new TreeBidiMap();
	/**
	 * scope cache storing the theme Id to the scope id
	 */
	private Map<Set<Long>, Long> scopesCache = new HashMap<Set<Long>, Long>();
	/**
	 * query cache storing the known constructs as SQL Query
	 */
	private Map<String, Long> knownQueries = new HashMap<String, Long>();

	/**
	 * Constructor
	 * 
	 * @param os
	 *            the output stream
	 * @throws MIOException
	 */
	public FileWriterMapHandler(OutputStream os) throws MIOException {
		this(os, null);
	}

	/**
	 * Constructor
	 * 
	 * @param os
	 *            the output stream
	 * @param properties
	 *            the DB connection properties
	 * @throws MIOException
	 */
	public FileWriterMapHandler(OutputStream os, Properties properties) throws MIOException {
		this.os = os;
		if (properties == null) {
			try {
				InputStream is = getClass().getResourceAsStream("/db.properties");
				properties = new Properties();
				if (is == null)
					throw new MIOException("Could not load db.properties!");
				properties.load(is);
			} catch (IOException e) {
				throw new MIOException(e);
			}
		}
		init(properties);
	}

	/**
	 * Initialize the database and get the topic map id
	 * 
	 * @param properties
	 *            the properties to connect to db
	 * @throws MIOException
	 */
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
	 * Method calls the database and load existing content of the topic map
	 * 
	 * @throws MIOException
	 *             thrown if anything fails
	 */
	public void preInitialization() throws MIOException {
		try {
			Statement stmt = connection.createStatement();
			/*
			 * get all locators
			 */
			ResultSet rs = stmt.executeQuery(QUERY.SELECT_LOCATOR);
			while (rs.next()) {
				long id = rs.getLong(1);
				String ref = rs.getString(2);
				locatorCache.put(ref, id);
			}
			/*
			 * get all subject-identifiers
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_SI, topicMapId));
			while (rs.next()) {
				long locatorId = rs.getLong(2);
				long topicId = rs.getLong(1);
				String ref = (String) locatorCache.getKey(locatorId);
				topicCache.put(new Ref(ref, IRef.SUBJECT_IDENTIFIER), topicId);
				knownQueries.put(MessageFormat.format(QUERY.SI, Long.toString(topicId), Long.toString(locatorId)), -1L);
			}
			/*
			 * get all subject-locators
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_SL, topicMapId));
			while (rs.next()) {
				long locatorId = rs.getLong(2);
				long topicId = rs.getLong(1);
				String ref = (String) locatorCache.getKey(locatorId);
				topicCache.put(new Ref(ref, IRef.SUBJECT_LOCATOR), topicId);
				knownQueries.put(MessageFormat.format(QUERY.SL, Long.toString(topicId), Long.toString(locatorId)), -1L);
			}
			/*
			 * get all item-identifiers
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_II, topicMapId));
			while (rs.next()) {
				long locatorId = rs.getLong(2);
				long topicId = rs.getLong(1);
				String ref = (String) locatorCache.getKey(locatorId);
				topicCache.put(new Ref(ref, IRef.ITEM_IDENTIFIER), topicId);
				knownQueries.put(MessageFormat.format(QUERY.II, Long.toString(topicId), Long.toString(locatorId)), -1L);
			}
			/*
			 * get scopes
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_SCOPE, topicMapId));
			while (rs.next()) {
				long scopeId = rs.getLong(1);
				Array themes = rs.getArray(2);
				Set<Long> themeIds = new HashSet<Long>();
				ResultSet rsA = themes.getResultSet();
				while (rsA.next()) {
					themeIds.add(rsA.getLong(1));
				}
				scopesCache.put(themeIds, scopeId);
			}
			/*
			 * get names
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_NAME, topicMapId));
			while (rs.next()) {
				long topicMapId = rs.getLong(1);
				long topicId = rs.getLong(2);
				long nameId = rs.getLong(3);
				long typeId = rs.getLong(4);
				String value = rs.getString(5);
				long scopeId = rs.getLong(6);
				long reifierId = rs.getLong(7);
				String reifier = rs.wasNull() ? NULL : Long.toString(reifierId);
				knownQueries.put(MessageFormat.format(QUERY.NAME, Long.toString(topicMapId), Long.toString(topicId),
						PLACEHOLDER, Long.toString(typeId), value, Long.toString(scopeId), reifier), nameId);
			}
			/*
			 * get occurrences
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_OCCURRENCE, topicMapId));
			while (rs.next()) {
				long topicMapId = rs.getLong(1);
				long topicId = rs.getLong(2);
				long occurrenceId = rs.getLong(3);
				long typeId = rs.getLong(4);
				String value = rs.getString(5);
				long locatorId = rs.getLong(6);
				long scopeId = rs.getLong(7);
				long reifierId = rs.getLong(8);
				String reifier = rs.wasNull() ? NULL : Long.toString(reifierId);
				knownQueries.put(MessageFormat.format(QUERY.OCCURRENCE, Long.toString(topicMapId),
						Long.toString(topicId), PLACEHOLDER, Long.toString(typeId), value, Long.toString(locatorId),
						Long.toString(scopeId), reifier), occurrenceId);
			}
			/*
			 * get variants
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_VARIANT, topicMapId));
			while (rs.next()) {
				long topicMapId = rs.getLong(1);
				long nameId = rs.getLong(2);
				long variantId = rs.getLong(3);
				String value = rs.getString(4);
				long locatorId = rs.getLong(5);
				long scopeId = rs.getLong(6);
				long reifierId = rs.getLong(7);
				String reifier = rs.wasNull() ? NULL : Long.toString(reifierId);
				knownQueries.put(MessageFormat.format(QUERY.VARIANT, Long.toString(topicMapId), Long.toString(nameId),
						PLACEHOLDER, value, Long.toString(locatorId), Long.toString(scopeId), reifier), variantId);
			}
			/*
			 * get associations
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_ASSOCIATION, topicMapId));
			while (rs.next()) {
				long topicMapId = rs.getLong(1);
				long associationId = rs.getLong(2);
				long typeId = rs.getLong(3);
				long scopeId = rs.getLong(4);
				long reifierId = rs.getLong(5);
				String reifier = rs.wasNull() ? NULL : Long.toString(reifierId);
				knownQueries.put(
						MessageFormat.format(QUERY.ASSOCIATION, Long.toString(topicMapId), Long.toString(topicMapId),
								PLACEHOLDER, Long.toString(typeId), Long.toString(scopeId), reifier), associationId);
			}
			/*
			 * get roles
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_ROLE, topicMapId));
			while (rs.next()) {
				long topicMapId = rs.getLong(1);
				long associationId = rs.getLong(2);
				long roleId = rs.getLong(3);
				long typeId = rs.getLong(4);
				long playerId = rs.getLong(5);
				long reifierId = rs.getLong(6);
				String reifier = rs.wasNull() ? NULL : Long.toString(reifierId);
				knownQueries.put(MessageFormat.format(QUERY.ROLE, topicMapId, associationId, PLACEHOLDER, typeId,
						playerId, reifier), roleId);
			}
			/*
			 * get type-instances
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_ISA, topicMapId));
			while (rs.next()) {
				long typeId = rs.getLong(1);
				long instanceId = rs.getLong(2);
				knownQueries
						.put(MessageFormat.format(QUERY.ISA, Long.toString(typeId), Long.toString(instanceId)), -1L);
			}
			/*
			 * get kind of
			 */
			rs = stmt.executeQuery(MessageFormat.format(QUERY.SELECT_AKO, topicMapId));
			while (rs.next()) {
				long supertypeId = rs.getLong(1);
				long subtypeId = rs.getLong(2);
				knownQueries.put(MessageFormat.format(QUERY.AKO, Long.toString(supertypeId), Long.toString(subtypeId)),
						-1L);
			}
			stmt.close();
		} catch (SQLException e) {
			throw new MIOException(e);
		}
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
	 * Handler for start event preparing the connection
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
			/*
			 * update sequences
			 */
			String sql = MessageFormat.format(QUERY.SETVAL, Long.toString(id), Long.toString(locatorId),
					Long.toString(scopeId));
			os.write(sql.getBytes(UTF_8));
			/*
			 * flush
			 */
			os.flush();
		} catch (SQLException e) {
			throw new MIOException(e);
		} catch (IOException e) {
			throw new MIOException(e);
		}
	}

	/**
	 * Returns the id of the topic map for the given locator. If it does not exist it will be created.
	 * 
	 * @param locator
	 *            locator of the topic map
	 * @return the id of the topic map
	 * @throws MIOException
	 */
	public long getTopicMapId(String locator) throws MIOException {
		if ( topicMapId != -1 ){
			return topicMapId;
		}
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

			preInitialization();

			rs = connection.createStatement().executeQuery(QUERY.CURRVAL);
			rs.next();
			id = rs.getLong(1);
			locatorId = rs.getLong(2);
			scopeId = rs.getLong(3);

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
	 *            the association to add
	 * @throws MIOException
	 */
	public void addAssociation(Association assoc) throws MIOException {
		long assocID = id++;
		String reifier = assoc.getReifier() == null ? NULL : Long.toString(getTopic(assoc.getReifier()));
		String sql = MessageFormat.format(QUERY.ASSOCIATION, Long.toString(topicMapId), Long.toString(topicMapId),
				Long.toString(assocID), Long.toString(getTopic(assoc.getType())), getScopeId(assoc.getThemes()),
				reifier);
		try {
			os.write(sql.getBytes(UTF_8));
		} catch (Exception e) {
			throw new MIOException(e);
		}
		for (Role role : assoc.getRoles()) {
			addRole(assocID, role);
		}
	}

	/**
	 * Adds a role to the association
	 * 
	 * @param assocID
	 *            the id of the parent association
	 * @param role
	 *            the role helper
	 * @throws MIOException
	 */
	private void addRole(long assocID, Role role) throws MIOException {
		long roleId = id++;
		String sql = MessageFormat.format(QUERY.ROLE, Long.toString(topicMapId), Long.toString(assocID), PLACEHOLDER,
				Long.toString(getTopic(role.getRoleType())), Long.toString(getTopic(role.getRolePlayer())), NULL);
		if (!knownQueries.containsKey(sql)) {
			sql = MessageFormat.format(QUERY.ROLE, Long.toString(topicMapId), Long.toString(assocID),
					Long.toString(roleId), Long.toString(getTopic(role.getRoleType())),
					Long.toString(getTopic(role.getRolePlayer())), NULL);
			try {
				os.write(sql.getBytes(UTF_8));
			} catch (Exception e) {
				throw new MIOException(e);
			}
		}
	}

	/**
	 * Adds a name to the topic map
	 * 
	 * @param name
	 * @throws MIOException
	 */
	public void addName(Name name) throws MIOException {
		long nameId = id++;
		String reifier = name.getReifier() == null ? NULL : Long.toString(getTopic(name.getReifier()));
		String sql = MessageFormat.format(QUERY.NAME, Long.toString(name.getTopicMapId()),
				Long.toString(name.getParentId()), PLACEHOLDER, Long.toString(getTopic(name.getTypeRef())),
				escape(name.getValue()), getScopeId(name.getThemeRefs()), reifier);
		if (!knownQueries.containsKey(sql)) {
			sql = MessageFormat.format(QUERY.NAME, Long.toString(name.getTopicMapId()),
					Long.toString(name.getParentId()), Long.toString(nameId),
					Long.toString(getTopic(name.getTypeRef())), escape(name.getValue()),
					getScopeId(name.getThemeRefs()), reifier);
			try {
				os.write(sql.getBytes(UTF_8));
			} catch (Exception e) {
				throw new MIOException(e);
			}
		} else {
			nameId = knownQueries.get(sql);
		}
		for (Variant v : name.getVariants()) {
			addVariant(nameId, v);
		}
	}

	/**
	 * Adds an occurrence
	 * 
	 * @param occurrence
	 * @throws MIOException
	 */
	public void addOccurrence(Occurrence occurrence) throws MIOException {
		long occurrenceId = id++;
		String reifier = occurrence.getReifier() == null ? NULL : Long.toString(getTopic(occurrence.getReifier()));
		String sql = MessageFormat.format(QUERY.OCCURRENCE, Long.toString(occurrence.getTopicMapId()),
				Long.toString(occurrence.getParentId()), PLACEHOLDER, Long.toString(getTopic(occurrence.getTypeRef())),
				escape(occurrence.getValue()), getLocatorId(occurrence.getDatatype()),
				getScopeId(occurrence.getThemeRefs()), reifier);
		if (!knownQueries.containsKey(sql)) {
			sql = MessageFormat.format(QUERY.OCCURRENCE, Long.toString(occurrence.getTopicMapId()),
					Long.toString(occurrence.getParentId()), Long.toString(occurrenceId),
					Long.toString(getTopic(occurrence.getTypeRef())), escape(occurrence.getValue()),
					getLocatorId(occurrence.getDatatype()), getScopeId(occurrence.getThemeRefs()), reifier);
			try {
				os.write(sql.getBytes(UTF_8));
			} catch (Exception e) {
				throw new MIOException(e);
			}
		}
	}

	/**
	 * Returns the topic id for the given identifier. It it isn't in the db, it will be created.
	 * 
	 * @param ref
	 *            the identifier of the topic
	 * @return the id of the topic
	 * @throws MIOException
	 */
	public long getTopic(IRef ref) throws MIOException {

		// check cache
		Long val = topicCache.get(ref);
		if (val != null)
			return val.longValue();

		long topicId = id++;
		String sql = MessageFormat.format(QUERY.TOPIC, Long.toString(topicMapId), Long.toString(topicMapId),
				Long.toString(topicId));
		try {
			os.write(sql.getBytes(UTF_8));
		} catch (Exception e) {
			throw new MIOException(e);
		}
		topicCache.put(ref, topicId);

		String locatorId = getLocatorId(ref.getIRI());

		// create read statement
		switch (ref.getType()) {
		case IRef.SUBJECT_IDENTIFIER:
			sql = MessageFormat.format(QUERY.SI, Long.toString(topicId), locatorId);
			break;
		case IRef.ITEM_IDENTIFIER:
			sql = MessageFormat.format(QUERY.II, Long.toString(topicId), locatorId);
			break;
		case IRef.SUBJECT_LOCATOR:
			sql = MessageFormat.format(QUERY.SL, Long.toString(topicId), locatorId);
			break;
		}
		return topicId;
	}

	/**
	 * Returns the locator id of the given reference
	 * 
	 * @param ref
	 *            the reference
	 * @return the locator Id as {@link String}
	 * @throws MIOException
	 */
	public String getLocatorId(String ref) throws MIOException {
		// check cache
		Long val = (Long) locatorCache.get(ref);
		if (val != null)
			return Long.toString(val.longValue());
		long locatorId = this.locatorId++;

		String sql = MessageFormat.format(QUERY.LOCATOR, Long.toString(locatorId), ref);
		try {
			os.write(sql.getBytes(UTF_8));
		} catch (Exception e) {
			throw new MIOException(e);
		}

		locatorCache.put(ref, locatorId);

		return Long.toString(locatorId);
	}

	/**
	 * adds an identifier
	 * 
	 * @param topicId
	 *            id of the topic
	 * @param ref
	 *            the URI
	 * @param type
	 *            the type of identifier
	 * @throws MIOException
	 */
	public void addIdentifier(long topicId, String ref, int type) throws MIOException {

		/*
		 * topic already exists -> may be merged with this one
		 */
		if (topicCache.containsKey(new Ref(ref, type))) {
			long existingId = topicCache.get(new Ref(ref, type));
			if (topicId == existingId) {
				return;
			}
			String newTopicId = Long.toString(topicId);
			String oldTopicId = Long.toString(existingId);
			Object[] values = new Object[25];
			for (int i = 0; i < 24; i += 2) {
				values[i] = newTopicId;
				values[i + 1] = oldTopicId;
			}
			values[24] = oldTopicId;
			String sql = MessageFormat.format(QUERY.MERGE, values);
			try {
				os.write(sql.getBytes(UTF_8));
			} catch (Exception e) {
				throw new MIOException(e);
			}
			/*
			 * update references
			 */
			Set<IRef> refs = new HashSet<IRef>();
			for (Entry<IRef, Long> entry : topicCache.entrySet()) {
				if (entry.getKey().equals(oldTopicId)) {
					refs.add(entry.getKey());
				}
			}
			for (IRef ref_ : refs) {
				topicCache.put(ref_, topicId);
			}
			/*
			 * update scopes
			 */
			Iterator<Set<Long>> iterator = scopesCache.keySet().iterator();
			while (iterator.hasNext()) {
				Set<Long> key = iterator.next();
				if (key.contains(existingId)) {
					Set<Long> keys_ = new HashSet<Long>(key);
					keys_.remove(existingId);
					keys_.add(topicId);
					scopesCache.put(keys_, scopesCache.get(key));
					scopesCache.remove(key);
				}
			}
		}
		/*
		 * add identifier
		 */
		else {
			String locatorId = getLocatorId(ref);
			String sql = "";
			switch (type) {
			case IRef.SUBJECT_IDENTIFIER:
				sql = MessageFormat.format(QUERY.SI, Long.toString(topicId), locatorId);
				break;
			case IRef.ITEM_IDENTIFIER:
				sql = MessageFormat.format(QUERY.II, Long.toString(topicId), locatorId);
				break;
			case IRef.SUBJECT_LOCATOR:
				sql = MessageFormat.format(QUERY.SL, Long.toString(topicId), locatorId);
				break;
			}
			if (!knownQueries.containsKey(sql)) {
				try {
					os.write(sql.getBytes(UTF_8));
				} catch (Exception e) {
					throw new MIOException(e);
				}
			}
		}
	}

	/**
	 * Adds a type to the topic with the given id
	 * 
	 * @param currTopicId
	 *            the current topic id
	 * @param arg0
	 *            the reference of the typing topic
	 * @throws MIOException
	 * @throws
	 */
	public void addType(long currTopicId, IRef arg0) throws MIOException {
		long typeId = getTopic(arg0);
		String sql = MessageFormat.format(QUERY.ISA, Long.toString(typeId), Long.toString(currTopicId));
		if (!knownQueries.containsKey(sql)) {
			try {
				os.write(sql.getBytes(UTF_8));
			} catch (Exception e) {
				throw new MIOException(e);
			}
		}
	}

	/**
	 * Returns the scope Id of the given themes
	 * 
	 * @param themes
	 *            the themes
	 * @return the scope Id
	 * @throws MIOException
	 */
	private String getScopeId(List<IRef> themes) throws MIOException {
		Set<Long> set = new HashSet<Long>();
		for (IRef ref : themes) {
			long themeId = getTopic(ref);
			set.add(themeId);
		}
		if (scopesCache.containsKey(set)) {
			return Long.toString(scopesCache.get(set));
		}
		long scopeId = this.scopeId++;
		String sql = MessageFormat.format(QUERY.SCOPE, topicMapId, scopeId);
		try {
			os.write(sql.getBytes(UTF_8));
		} catch (Exception e) {
			throw new MIOException(e);
		}
		for (Long themeId : set) {
			sql = MessageFormat.format(QUERY.THEME, scopeId, themeId);
			try {
				os.write(sql.getBytes(UTF_8));
			} catch (Exception e) {
				throw new MIOException(e);
			}
		}
		scopesCache.put(set, scopeId);
		return Long.toString(scopeId);
	}

	/**
	 * Adds a variant to database
	 * 
	 * @param id
	 *            the name Id
	 * @param variant
	 *            the variant container
	 * @throws MIOException
	 */
	private void addVariant(long id, Variant variant) throws MIOException {
		long variantId = this.id++;
		String reifier = variant.getReifier() == null ? NULL : Long.toString(getTopic(variant.getReifier()));
		String sql = MessageFormat.format(QUERY.VARIANT, Long.toString(topicMapId), Long.toString(id), PLACEHOLDER,
				escape(variant.getValue()), getLocatorId(variant.getDatatype()), getScopeId(variant.getThemeRefs()),
				reifier);
		if (!knownQueries.containsKey(sql)) {
			sql = MessageFormat.format(QUERY.VARIANT, Long.toString(topicMapId), Long.toString(id),
					Long.toString(variantId), escape(variant.getValue()), getLocatorId(variant.getDatatype()),
					getScopeId(variant.getThemeRefs()), reifier);
			try {
				os.write(sql.getBytes(UTF_8));
			} catch (Exception e) {
				throw new MIOException(e);
			}
		}
	}

	/**
	 * Utility method to escape SQL reserved symbols as part of string values
	 * 
	 * @param value
	 *            the value
	 * @return the escaped value
	 */
	private String escape(String value) {
		return value.replace("'", "\u00B4");
	}
}
