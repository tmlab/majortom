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
package de.topicmapslab.majortom.tests.index;

import junit.framework.Test;
import junit.framework.TestSuite;
import de.topicmapslab.majortom.tests.index.paged.PagedIndexTestSuite;

/**
 * @author Sven Krosse
 * 
 */
public class IndexTestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for de.topicmapslab.engine.tests.index");
		// $JUnit-BEGIN$
		 suite.addTestSuite(TestIdentityIndex.class);
		 suite.addTestSuite(TestLiteralIndex.class);
		suite.addTestSuite(TestSupertypeSubtypeIndex.class);
		suite.addTestSuite(TestTransitiveTypeInstanceIndex.class);
		suite.addTestSuite(TestScopeIndex.class);
		suite.addTest(PagedIndexTestSuite.suite());
		// $JUnit-END$
		return suite;
	}

}
