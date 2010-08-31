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

import org.tmapi.core.Construct;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.event.TopicMapEventType;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.model.revision.IRevisionChange;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestRevisionIndex extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getChangeset(org.tmapi.core.Topic)}
	 * .
	 */
	public void testGetChangeset() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getFirstRevision()}
	 * .
	 */
	public void testGetFirstRevision() {
		IRevisionIndex index = null;
		try {
			index = topicMap.getIndex(IRevisionIndex.class);
		} catch (Exception e) {
			fail("Cannot create index");
		}
		assertNotNull(index);
		try {
			index.getFirstRevision();
			fail("Index should be close!");
		} catch (Exception e) {
			index.open();
		}

		assertNull(index.getFirstRevision());
		Topic t = createTopic();
		assertNotNull(index.getFirstRevision());
		IRevision r = index.getFirstRevision();
		assertEquals(1, r.getChangeset().size());
		checkChange(r.getChangeset().get(0), TopicMapEventType.TOPIC_ADDED, topicMap, t, null);
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getLastModification()}
	 * .
	 */
	public void testGetLastModification() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getLastModification(org.tmapi.core.Topic)}
	 * .
	 */
	public void testGetLastModificationTopic() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getLastRevision()}
	 * .
	 */
	public void testGetLastRevision() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getRevision(java.util.Calendar)}
	 * .
	 */
	public void testGetRevisionCalendar() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getRevision(java.lang.String)}
	 * .
	 */
	public void testGetRevisionString() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getRevision(long)}
	 * .
	 */
	public void testGetRevisionLong() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryRevisionIndex#getRevisions(org.tmapi.core.Topic)}
	 * .
	 */
	public void testGetRevisions() {
		fail("Not yet implemented");
	}

	public void checkChange(IRevisionChange change, TopicMapEventType type, Construct context, Object newValue, Object oldValue) {
		assertEquals(type, change.getType());
		assertEquals(context, change.getContext());
		assertEquals(newValue, change.getNewValue());
		assertEquals(oldValue, change.getOldValue());
	}

}
