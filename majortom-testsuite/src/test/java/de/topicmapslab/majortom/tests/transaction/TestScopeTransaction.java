package de.topicmapslab.majortom.tests.transaction;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

public class TestScopeTransaction extends MaJorToMTestCase {

	public void testName() throws Exception {
		ITopic theme = createTopic();
		_testScoped((IName) createTopic().createName("Name", theme));
	}
	
	public void testOccurrence() throws Exception {
		ITopic theme = createTopic();
		_testScoped((IOccurrence) createTopic().createOccurrence(createTopic(),"Occ", theme));
	}
	
	public void testVariant() throws Exception {
		ITopic theme = createTopic();
		_testScoped((IName) createTopic().createName("Name", new Topic[0]).createVariant("Variant", theme));
	}

	public void _testScoped(IScopable scopable) throws Exception {

	}

}
