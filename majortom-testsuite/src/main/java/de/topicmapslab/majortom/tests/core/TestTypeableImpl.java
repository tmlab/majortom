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
package de.topicmapslab.majortom.tests.core;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestTypeableImpl extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.engine.core.TypeableImpl#setType(org.tmapi.core.Topic)}
	 * .
	 */
	public void testType() {

		ITopic type = createTopic();
		ITopic otherType = createTopic();

		// topic name

		Name n = createTopic().createName("Name", new Topic[0]);
		assertNotNull(n.getType());
		assertNotSame(type, n.getType());
		assertNotSame(otherType, n.getType());

		n.setType(type);
		assertEquals(type, n.getType());
		assertNotSame(otherType, n.getType());

		n.setType(otherType);
		assertNotSame(type, n.getType());
		assertEquals(otherType, n.getType());

		// occurrence

		Occurrence o = createTopic().createOccurrence(createTopic(), "Occurrence", new Topic[0]);
		assertNotNull(o.getType());
		assertNotSame(type, o.getType());
		assertNotSame(otherType, o.getType());

		o.setType(type);
		assertEquals(type, o.getType());
		assertNotSame(otherType, o.getType());

		o.setType(otherType);
		assertNotSame(type, o.getType());
		assertEquals(otherType, o.getType());

		// association

		Association a = createAssociation(createTopic());
		assertNotNull(a.getType());
		assertNotSame(type, a.getType());
		assertNotSame(otherType, a.getType());

		a.setType(type);
		assertEquals(type, a.getType());
		assertNotSame(otherType, a.getType());

		a.setType(otherType);
		assertNotSame(type, a.getType());
		assertEquals(otherType, a.getType());

		// association role

		Role r = a.createRole(createTopic(), createTopic());
		assertNotNull(r.getType());
		assertNotSame(type, r.getType());
		assertNotSame(otherType, r.getType());

		r.setType(type);
		assertEquals(type, r.getType());
		assertNotSame(otherType, r.getType());

		r.setType(otherType);
		assertNotSame(type, r.getType());
		assertEquals(otherType, r.getType());

	}

}
