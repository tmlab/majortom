package de.topicmapslab.majortom.testsuite.readonly;

import de.topicmapslab.majortom.model.core.ITopicMap;

//@RunWith(Suite.class)
//@SuiteClasses({TestAssociationImpl.class, 
//	TestAssociationRoleImpl.class, 
//	TestConstructImpl.class, 
//	TestDatatypeAwareImpl.class, 
//	TestNameImpl.class, 
//	TestOccurrenceImpl.class, 
//	TestReifiableImpl.class, 
//	TestScopeableImpl.class, 
//	TestTopicImpl.class, 
//	TestTopicMapImpl.class, 
//	TestVariantImpl.class}) 
public class ReadOnlyTestSuite {
	
	protected static void setMap(ITopicMap map){
		AbstractTest.setTopicMap(map);
	}
}
