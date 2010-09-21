package de.topicmapslab.majortom.testsuite.readonly;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.testsuite.readonly.core.TestAssociationImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestAssociationRoleImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestNameImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestOccurrenceImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestTopicImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestTopicMapImpl;

@RunWith(Suite.class)
@SuiteClasses({TestAssociationImpl.class, 
	TestAssociationRoleImpl.class,  
	TestNameImpl.class, 
	TestOccurrenceImpl.class, 
	TestTopicImpl.class, 
	TestTopicMapImpl.class}) 
public class ReadOnlyTestSuite {
	
	protected static void setMap(ITopicMap map){
		AbstractTest.setTopicMap(map);
	}
}
