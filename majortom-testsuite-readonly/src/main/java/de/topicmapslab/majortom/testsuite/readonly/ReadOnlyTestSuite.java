package de.topicmapslab.majortom.testsuite.readonly;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import de.topicmapslab.majortom.model.core.ITopicMap;
import de.topicmapslab.majortom.testsuite.readonly.core.TestAssociationImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestAssociationRoleImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestConstructImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestDatatypeAwareImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestNameImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestOccurrenceImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestReifiableImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestScopeableImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestTopicImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestTopicMapImpl;
import de.topicmapslab.majortom.testsuite.readonly.core.TestVariantImpl;

@RunWith(Suite.class)
@SuiteClasses({TestAssociationImpl.class, 
	TestAssociationRoleImpl.class, 
	TestConstructImpl.class, 
	TestDatatypeAwareImpl.class, 
	TestNameImpl.class, 
	TestOccurrenceImpl.class, 
	TestReifiableImpl.class, 
	TestScopeableImpl.class, 
	TestTopicImpl.class, 
	TestTopicMapImpl.class, 
	TestVariantImpl.class}) 
public class ReadOnlyTestSuite {
	
	protected static void setMap(ITopicMap map){
		AbstractTest.setTopicMap(map);
	}
}
