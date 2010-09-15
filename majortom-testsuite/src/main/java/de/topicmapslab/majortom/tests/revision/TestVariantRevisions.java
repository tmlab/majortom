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
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.revision.core.ReadOnlyVariant;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.XmlSchemeDatatypes;

/**
 * @author Sven Krosse
 * 
 */
public class TestVariantRevisions extends MaJorToMTestCase {

	public void testRevision() throws Exception {
		ITopic topic = createTopic();
		ITopic reifier = createTopic();
		Name n = topic.createName("Value", new Topic[0]);
		ITopic theme = createTopic();
		Variant v = n.createVariant("Var", theme);
		v.setReifier(reifier);
		
		v.remove();
		
		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();
		
		IRevision revision = index.getLastRevision();
		assertEquals(2, revision.getChangeset().size());
		assertEquals(v, revision.getChangeset().get(1).getOldValue());
		assertTrue(revision.getChangeset().get(1).getOldValue() instanceof ReadOnlyVariant);
		
		v = (IVariant) revision.getChangeset().get(1).getOldValue();
		
		assertEquals("Var", v.getValue());
		assertEquals(reifier, v.getReifier());
		assertEquals(XmlSchemeDatatypes.XSD_STRING, v.getDatatype().getReference());
		assertEquals(n, v.getParent());
		assertTrue(v.getScope().contains(theme));
	}

}
