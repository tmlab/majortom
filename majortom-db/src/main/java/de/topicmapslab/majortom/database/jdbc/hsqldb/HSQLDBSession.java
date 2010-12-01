package de.topicmapslab.majortom.database.jdbc.hsqldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSQueryProcessor;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSSession;
import de.topicmapslab.majortom.model.exception.TopicMapStoreException;

public class HSQLDBSession extends RDBMSSession {

	public static int numProcessors = 0;
	
	public HSQLDBSession(RDBMSConnectionProvider connectionProvider,
			String url, String user, String password) {
		super(connectionProvider, url, user, password);
	}

	@Override
	protected RDBMSQueryProcessor createProcessor(Connection connection) throws TopicMapStoreException {
		numProcessors++;
		return new RDBMSQueryProcessor(this, connection);
	}
	
	@Override
	protected Connection openConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:file:data/" + getConnectionProvider().getDatabase(), "SA", "");
	}

}
