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
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestReifiableImpl extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ReifiableImpl#getReifier()}.
	 */
	public void testReifier() {

		ITopic reifier = createTopic();

		// topic name

		Name n = createTopic().createName("Name", new Topic[0]);
		assertNull(n.getReifier());
		assertNull(reifier.getReified());

		n.setReifier(reifier);
		assertNotNull(n.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(n, reifier.getReified());
		assertEquals(reifier, n.getReifier());

		n.setReifier(null);
		assertNull(n.getReifier());
		assertNull(reifier.getReified());

		// topic name variants

		Variant v = n.createVariant("Variant", createTopic());
		assertNull(v.getReifier());
		assertNull(reifier.getReified());

		v.setReifier(reifier);
		assertNotNull(v.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(v, reifier.getReified());
		assertEquals(reifier, v.getReifier());

		v.setReifier(null);
		assertNull(v.getReifier());
		assertNull(reifier.getReified());

		// occurrence

		Occurrence o = createTopic().createOccurrence(createTopic(), "Name", new Topic[0]);
		assertNull(o.getReifier());
		assertNull(reifier.getReified());

		o.setReifier(reifier);
		assertNotNull(o.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(o, reifier.getReified());
		assertEquals(reifier, o.getReifier());

		o.setReifier(null);
		assertNull(o.getReifier());
		assertNull(reifier.getReified());

		// association

		Association a = createAssociation(createTopic());
		assertNull(a.getReifier());
		assertNull(reifier.getReified());

		a.setReifier(reifier);
		assertNotNull(a.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(a, reifier.getReified());
		assertEquals(reifier, a.getReifier());

		a.setReifier(null);
		assertNull(a.getReifier());
		assertNull(reifier.getReified());

		// role

		Role r = a.createRole(createTopic(), createTopic());
		assertNull(r.getReifier());
		assertNull(reifier.getReified());

		r.setReifier(reifier);
		assertNotNull(r.getReifier());
		assertNotNull(reifier.getReified());
		assertEquals(r, reifier.getReified());
		assertEquals(reifier, r.getReifier());

		r.setReifier(null);
		assertNull(r.getReifier());
		assertNull(reifier.getReified());

	}

}
