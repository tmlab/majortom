package de.topicmapslab.majortom.database.jdbc.hsqldb;

import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSQueryProcessor;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSSession;
import de.topicmapslab.majortom.util.DatatypeAwareUtils;

public class HSQLDBConnectionProvider  extends RDBMSConnectionProvider{
	
	public HSQLDBConnectionProvider() {
		RDBMSQueryProcessor.GENERATED_KEY_COLUMN_NAME = "ID";
	}
	
	public HSQLDBConnectionProvider(String host, String database, String user, String password) {
		super(host, database, user, password);
		RDBMSQueryProcessor.GENERATED_KEY_COLUMN_NAME = "ID";
		DatatypeAwareUtils.setDateTimeFormat("yyyy-MM-dd HH:mm:ss.SSSSS+HH:mm");
	}
	
	protected String getRdbmsName() {
		return "hsqldb:hsql";
	}
	
	protected String getDriverClassName() {
		return "org.hsqldb.jdbc.JDBCDriver";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public RDBMSSession openSession() {		
		return new HSQLDBSession(this, getUrl(), getUser(), getPassword());
	}

}
