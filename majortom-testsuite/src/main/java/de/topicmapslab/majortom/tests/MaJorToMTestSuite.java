/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.topicmapslab.majortom.tests.canonical.CanonicalTestSuite;
import de.topicmapslab.majortom.tests.core.CoreTestSuite;
import de.topicmapslab.majortom.tests.event.EventTestSuite;
import de.topicmapslab.majortom.tests.index.IndexTestSuite;
import de.topicmapslab.majortom.tests.merge.MergeTestSuite;
import de.topicmapslab.majortom.tests.revision.RevisionTestSuite;
import de.topicmapslab.majortom.tests.transaction.TransactionSuite;

/**
 * @author Sven Krosse
 * 
 */
public class MaJorToMTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.topicmapslab.engine.tests");
		// $JUnit-BEGIN$
		 suite.addTest(CoreTestSuite.suite());
		 suite.addTest(EventTestSuite.suite());
		suite.addTest(IndexTestSuite.suite());
		 suite.addTest(MergeTestSuite.suite());
		// suite.addTest(IOTestSuite.suite());
		suite.addTest(RevisionTestSuite.suite());
		suite.addTest(TransactionSuite.suite());
		suite.addTest(org.tmapi.AllTests.suite());
		suite.addTest(CanonicalTestSuite.suite());
		// $JUnit-END$
		return suite;
	}

}
