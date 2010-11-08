package de.topicmapslab.majortom.database.jdbc.hsqldb;

import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSQueryProcessor;

public class HSQLDBConnectionProvider  extends RDBMSConnectionProvider{
	
	public HSQLDBConnectionProvider() {
		RDBMSQueryProcessor.GENERATED_KEY_COLUMN_NAME = "ID";
	}
	
	protected String getRdbmsName() {
		return "hsqldb:hsql";
	}
	
	protected String getDriverClassName() {
		return "org.hsqldb.jdbc.JDBCDriver";
	}
	

}
