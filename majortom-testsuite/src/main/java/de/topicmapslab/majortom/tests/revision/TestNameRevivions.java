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

import org.tmapi.core.Name;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.revision.core.ReadOnlyName;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestNameRevivions extends MaJorToMTestCase {

	public void testRevision() throws Exception {
		ITopic topic = createTopic();
		ITopic type = createTopic();
		ITopic theme = createTopic();
		ITopic reifier = createTopic();
		Name n = topic.createName(type, "Value", theme);

		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();		


		IRevision revision = index.getLastRevision();		
		assertEquals(4, revision.getChangeset().size());
		
		Variant v = n.createVariant("Var", createTopic());
		Variant other = n.createVariant("Var2", createTopic());
		n.setReifier(reifier);
		n.remove();
		topicMap.getStore().commit();

		revision = index.getLastRevision();		
		assertEquals(4, revision.getChangeset().size());
		assertEquals(n, revision.getChangeset().get(3).getOldValue());
		assertTrue(revision.getChangeset().get(3).getOldValue() instanceof ReadOnlyName);

		n = (IName) revision.getChangeset().get(3).getOldValue();

		assertEquals("Value", n.getValue());
		assertEquals(reifier, n.getReifier());
		assertEquals(topic, n.getParent());
		assertTrue(n.getScope().contains(theme));
		assertEquals(type, n.getType());
		assertEquals(2, n.getVariants().size());
		assertTrue(n.getVariants().contains(v));
		assertTrue(n.getVariants().contains(other));
	}

}
