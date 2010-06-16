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
import org.tmapi.core.Locator;

import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestConstructImpl extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#getItemIdentifiers()}.
	 */
	public void testGetItemIdentifiers() {
		Association a = createAssociation(createTopic());
		assertTrue(a.getItemIdentifiers().isEmpty());
		
		Locator ii = createLoctor("http://psi.example.org/ii");
		a.addItemIdentifier(ii);
		assertEquals(1, a.getItemIdentifiers().size());
		assertTrue(a.getItemIdentifiers().contains(ii));
		
		a.addItemIdentifier(ii);
		assertEquals(1, a.getItemIdentifiers().size());
		assertTrue(a.getItemIdentifiers().contains(ii));
		
		Locator ii2 = createLoctor("http://psi.example.org/ii2");
		a.addItemIdentifier(ii2);
		assertEquals(2, a.getItemIdentifiers().size());
		assertTrue(a.getItemIdentifiers().contains(ii));
		assertTrue(a.getItemIdentifiers().contains(ii2));
		
		a.removeItemIdentifier(ii);
		assertEquals(1, a.getItemIdentifiers().size());
		assertFalse(a.getItemIdentifiers().contains(ii));
		assertTrue(a.getItemIdentifiers().contains(ii2));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ConstructImpl#remove()}.
	 */
	public void testRemove() {
		Association a = createAssociation(createTopic());
		a.createRole(createTopic(), createTopic());
		a.createRole(createTopic(), createTopic());
		a.createRole(createTopic(), createTopic());
		a.remove();

		assertTrue(topicMap.getAssociations().isEmpty());
	}

}
