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
/**
 * 
 */
package de.topicmapslab.majortom.tests.revision;

import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.namespace.Namespaces;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.revision.core.ReadOnlyOccurrence;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestOccurrenceRevisions extends MaJorToMTestCase {

	public void testRevision() throws Exception {
		ITopic topic = createTopic();
		ITopic theme = createTopic();
		ITopic type = createTopic();
		ITopic reifier = createTopic();
		IOccurrence o = (IOccurrence) topic.createOccurrence(type, "Value", theme);
		o.setReifier(reifier);
		o.remove();
		topicMap.getStore().commit();

		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();

		IRevision revision = index.getLastRevision();
		assertEquals(2, revision.getChangeset().size());
		assertEquals(o, revision.getChangeset().get(1).getOldValue());
		assertTrue(revision.getChangeset().get(1).getOldValue() instanceof ReadOnlyOccurrence);

		o = (IOccurrence) revision.getChangeset().get(1).getOldValue();

		assertEquals("Value", o.getValue());
		assertEquals(reifier, o.getReifier());
		assertEquals(topic, o.getParent());
		assertTrue(o.getScope().contains(theme));
		assertEquals(type, o.getType());
		assertTrue(o.getDatatype().getReference().equals(Namespaces.XSD.STRING));
	}

}
