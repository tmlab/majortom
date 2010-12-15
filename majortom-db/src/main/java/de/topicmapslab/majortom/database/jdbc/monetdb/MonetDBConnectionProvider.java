package de.topicmapslab.majortom.database.jdbc.monetdb;

import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSConnectionProvider;
import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSSession;

public class MonetDBConnectionProvider extends RDBMSConnectionProvider {

	protected String getRdbmsName() {
		return "monetdb";
	}

	protected String getDriverClassName() {
		return "nl.cwi.monetdb.jdbc.MonetDriver";
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public RDBMSSession openSession() {
		return super.openSession();
	}

}
