package de.topicmapslab.majortom.database.jdbc.monetdb;

import de.topicmapslab.majortom.database.jdbc.rdbms.RDBMSConnectionProvider;

public class MonetDBConnectionProvider extends RDBMSConnectionProvider {

	
	
	protected String getRdbmsName() {
		return "monetdb";
	}
	
	protected String getDriverClassName() {
		return "nl.cwi.monetdb.jdbc.MonetDriver";
	}


}
