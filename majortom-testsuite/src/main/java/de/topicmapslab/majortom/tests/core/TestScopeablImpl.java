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

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestScopeablImpl extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.core.ScopeableImpl#getScopeObject()}.
	 */
	public void testGetScopeObject() {

		ITopic theme1 = createTopic();
		ITopic theme2 = createTopic();
		ITopic theme3 = createTopic();
		ITopic theme4 = createTopic();
		ITopic theme5 = createTopic();

		ITopic topic = createTopic();
		IName name = (IName) topic.createName("Name", theme1, theme2, theme3);
		IScope scope = name.getScopeObject();
		assertEquals(3, scope.getThemes().size());
		assertTrue(scope.getThemes().contains(theme1));
		assertTrue(scope.getThemes().contains(theme2));
		assertTrue(scope.getThemes().contains(theme3));
		
		IVariant variant = (IVariant)name.createVariant("Variant", theme1, theme2, theme3, theme4);
		scope = variant.getScopeObject();
		assertEquals(4, scope.getThemes().size());
		assertTrue(scope.getThemes().contains(theme1));
		assertTrue(scope.getThemes().contains(theme2));
		assertTrue(scope.getThemes().contains(theme3));
		assertTrue(scope.getThemes().contains(theme4));
		
		IOccurrence occ = (IOccurrence) topic.createOccurrence(createTopic(), "Occurrence", theme2, theme2,theme4,theme3, theme5);
		scope = occ.getScopeObject();
		assertEquals(4, scope.getThemes().size());
		assertTrue(scope.getThemes().contains(theme2));
		assertTrue(scope.getThemes().contains(theme3));
		assertTrue(scope.getThemes().contains(theme4));
		assertTrue(scope.getThemes().contains(theme5));
		
		IAssociation association = createAssociation(createTopic());
		scope = association.getScopeObject();
		assertEquals(0, scope.getThemes().size());
		
		association.addTheme(theme5);
		association.addTheme(theme4);
		
		scope = association.getScopeObject();
		assertEquals(2, scope.getThemes().size());
		assertTrue(scope.getThemes().contains(theme4));
		assertTrue(scope.getThemes().contains(theme5));
		
		association.removeTheme(theme5);
		
		scope = association.getScopeObject();
		assertEquals(1, scope.getThemes().size());
		assertTrue(scope.getThemes().contains(theme4));
		assertFalse(scope.getThemes().contains(theme5));

	}
}
