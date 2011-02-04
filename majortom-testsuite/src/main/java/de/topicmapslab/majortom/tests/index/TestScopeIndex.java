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

import java.util.Arrays;
import java.util.Collection;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestScopeIndex extends MaJorToMTestCase {

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getScope(org.tmapi.core.Topic[])}
	 * .
	 */
	public void testGetScopeTopicArray() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope = index.getScope(theme, otherTheme);
		assertNotNull(scope);
		assertEquals(2, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme));
		assertTrue(scope.containsTheme(otherTheme));

		IScope anotherScope = index.getScope(theme, otherTheme, anotherTheme);
		assertNotNull(anotherScope);
		assertEquals(3, anotherScope.getThemes().size());
		assertTrue(anotherScope.containsTheme(theme));
		assertTrue(anotherScope.containsTheme(otherTheme));
		assertTrue(anotherScope.containsTheme(anotherTheme));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getScope(java.util.Collection)}
	 * .
	 */
	public void testGetScopeCollectionOfQextendsTopic() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope = index.getScope(Arrays.asList(new Topic[] { theme, otherTheme }));
		assertNotNull(scope);
		assertEquals(2, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme));
		assertTrue(scope.containsTheme(otherTheme));

		IScope anotherScope = index.getScope(Arrays.asList(new Topic[] { theme, otherTheme, anotherTheme }));
		assertNotNull(anotherScope);
		assertEquals(3, anotherScope.getThemes().size());
		assertTrue(anotherScope.containsTheme(theme));
		assertTrue(anotherScope.containsTheme(otherTheme));
		assertTrue(anotherScope.containsTheme(anotherTheme));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getScopes(org.tmapi.core.Topic[])}
	 * .
	 */
	public void testGetScopesTopicArray() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope = index.getScope(new Topic[] { theme, otherTheme });
		assertNotNull(scope);
		assertEquals(2, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme));
		assertTrue(scope.containsTheme(otherTheme));

		IScope anotherScope = index.getScope(new Topic[] { theme, otherTheme, anotherTheme });
		assertNotNull(anotherScope);
		assertEquals(3, anotherScope.getThemes().size());
		assertTrue(anotherScope.containsTheme(theme));
		assertTrue(anotherScope.containsTheme(otherTheme));
		assertTrue(anotherScope.containsTheme(anotherTheme));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getScopes(org.tmapi.core.Topic[], boolean)}
	 * .
	 */
	public void testGetScopesTopicArrayBoolean() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope2 = index.getScope(otherTheme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope4 = index.getScope(theme, otherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope6 = index.getScope(otherTheme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);

		/*
		 * create scoped constructs
		 */
		createTopic().createName("Value", theme);
		createTopic().createName("Value", otherTheme);
		createTopic().createName("Value", anotherTheme);
		createTopic().createName("Value", theme, otherTheme);
		createTopic().createName("Value", theme, anotherTheme);
		createTopic().createName("Value", otherTheme, anotherTheme);
		createTopic().createName("Value", anotherTheme, otherTheme, theme);
		

		Collection<IScope> scopes = index.getScopes(new Topic[] { theme, otherTheme }, false);
		assertEquals(6, scopes.size());
		assertTrue(scopes.contains(scope1));
		assertTrue(scopes.contains(scope2));
		assertTrue(scopes.contains(scope4));
		assertTrue(scopes.contains(scope5));
		assertTrue(scopes.contains(scope6));
		assertTrue(scopes.contains(scope7));

		scopes = index.getScopes(new Topic[] { theme, otherTheme }, true);
		assertEquals(2, scopes.size());
		assertTrue(scopes.contains(scope4));
		assertTrue(scopes.contains(scope7));

		scopes = index.getScopes(new Topic[] { theme, otherTheme, anotherTheme }, false);
		assertEquals(7, scopes.size());
		assertTrue(scopes.contains(scope1));
		assertTrue(scopes.contains(scope2));
		assertTrue(scopes.contains(scope3));
		assertTrue(scopes.contains(scope4));
		assertTrue(scopes.contains(scope5));
		assertTrue(scopes.contains(scope6));
		assertTrue(scopes.contains(scope7));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getScopes(java.util.Collection, boolean)}
	 * .
	 */
	public void testGetScopesCollectionOfTopicBoolean() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope2 = index.getScope(otherTheme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope4 = index.getScope(theme, otherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope6 = index.getScope(otherTheme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);

		/*
		 * create scoped constructs
		 */
		createTopic().createName("Value", theme);
		createTopic().createName("Value", otherTheme);
		createTopic().createName("Value", anotherTheme);
		createTopic().createName("Value", theme, otherTheme);
		createTopic().createName("Value", theme, anotherTheme);
		createTopic().createName("Value", otherTheme, anotherTheme);
		createTopic().createName("Value", anotherTheme, otherTheme, theme);
		
		Collection<IScope> scopes = index.getScopes(Arrays.asList(new Topic[] { theme, otherTheme }), false);
		assertEquals(6, scopes.size());
		assertTrue(scopes.contains(scope1));
		assertTrue(scopes.contains(scope2));
		assertTrue(scopes.contains(scope4));
		assertTrue(scopes.contains(scope5));
		assertTrue(scopes.contains(scope6));
		assertTrue(scopes.contains(scope7));

		scopes = index.getScopes(Arrays.asList(new Topic[] { theme, otherTheme }), true);
		assertEquals(2, scopes.size());
		assertTrue(scopes.contains(scope4));
		assertTrue(scopes.contains(scope7));

		scopes = index.getScopes(Arrays.asList(new Topic[] { theme, otherTheme, anotherTheme }), false);
		assertEquals(7, scopes.size());
		assertTrue(scopes.contains(scope1));
		assertTrue(scopes.contains(scope2));
		assertTrue(scopes.contains(scope3));
		assertTrue(scopes.contains(scope4));
		assertTrue(scopes.contains(scope5));
		assertTrue(scopes.contains(scope6));
		assertTrue(scopes.contains(scope7));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getScopables(de.topicmapslab.majortom.model.core.IScope)}
	 * .
	 */
	public void testGetScopablesIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope4 = index.getScope(theme, otherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Association association = topicMap.createAssociation(createTopic(), theme);
		Name name = createTopic().createName("Name", theme, otherTheme);
		Variant variant = name.createVariant("Variant", theme, otherTheme, anotherTheme);
		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "Occurrence", theme, otherTheme);

		assertEquals(1, index.getScopables(scope1).size());
		assertTrue(index.getScopables(scope1).contains(association));
		assertEquals(2, index.getScopables(scope4).size());
		assertTrue(index.getScopables(scope4).contains(name));
		assertTrue(index.getScopables(scope4).contains(occurrence));
		assertEquals(1, index.getScopables(scope7).size());
		assertTrue(index.getScopables(scope7).contains(variant));

		name.removeTheme(otherTheme);
		assertEquals(2, index.getScopables(scope1).size());
		assertTrue(index.getScopables(scope1).contains(association));
		assertTrue(index.getScopables(scope1).contains(name));
		assertEquals(1, index.getScopables(scope4).size());
		assertTrue(index.getScopables(scope4).contains(occurrence));
		assertEquals(1, index.getScopables(scope7).size());
		assertTrue(index.getScopables(scope7).contains(variant));

		variant.removeTheme(otherTheme);
		assertEquals(2, index.getScopables(scope1).size());
		assertTrue(index.getScopables(scope1).contains(association));
		assertTrue(index.getScopables(scope1).contains(name));
		assertEquals(1, index.getScopables(scope4).size());
		assertTrue(index.getScopables(scope4).contains(occurrence));
		assertEquals(0, index.getScopables(scope7).size());
		assertEquals(1, index.getScopables(scope5).size());
		assertTrue(index.getScopables(scope5).contains(variant));

		association.removeTheme(theme);
		assertEquals(1, index.getScopables(scope1).size());
		assertTrue(index.getScopables(scope1).contains(name));
		assertEquals(1, index.getScopables(scope4).size());
		assertTrue(index.getScopables(scope4).contains(occurrence));
		assertEquals(1, index.getScopables(scope5).size());
		assertTrue(index.getScopables(scope5).contains(variant));
		assertEquals(1, index.getScopables(scope8).size());
		assertTrue(index.getScopables(scope8).contains(association));

		association.addTheme(anotherTheme);
		assertEquals(1, index.getScopables(scope1).size());
		assertTrue(index.getScopables(scope1).contains(name));
		assertEquals(1, index.getScopables(scope4).size());
		assertTrue(index.getScopables(scope4).contains(occurrence));
		assertEquals(1, index.getScopables(scope5).size());
		assertTrue(index.getScopables(scope5).contains(variant));
		assertEquals(0, index.getScopables(scope8).size());
		assertEquals(1, index.getScopables(scope3).size());
		assertTrue(index.getScopables(scope3).contains(association));

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getScopables(de.topicmapslab.majortom.model.core.IScope[])}
	 * .
	 */
	public void testGetScopablesIScopeArray() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope4 = index.getScope(theme, otherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Association association = topicMap.createAssociation(createTopic(), theme);
		Name name = createTopic().createName("Name", theme, otherTheme);
		Variant variant = name.createVariant("Variant", theme, otherTheme, anotherTheme);
		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "Occurrence", theme, otherTheme);

		assertEquals(1, index.getScopables(scope1, scope8).size());
		assertTrue(index.getScopables(scope1, scope8).contains(association));
		assertEquals(3, index.getScopables(scope4, scope7).size());
		assertTrue(index.getScopables(scope4, scope7).contains(name));
		assertTrue(index.getScopables(scope4, scope7).contains(variant));
		assertTrue(index.getScopables(scope4, scope7).contains(occurrence));

		occurrence.removeTheme(otherTheme);
		assertEquals(2, index.getScopables(scope1, scope8).size());
		assertTrue(index.getScopables(scope1, scope8).contains(association));
		assertTrue(index.getScopables(scope1, scope8).contains(occurrence));
		assertEquals(2, index.getScopables(scope4, scope7).size());
		assertTrue(index.getScopables(scope4, scope7).contains(name));
		assertTrue(index.getScopables(scope4, scope7).contains(variant));

		association.removeTheme(theme);
		assertEquals(2, index.getScopables(scope1, scope8).size());
		assertTrue(index.getScopables(scope1, scope8).contains(association));
		assertTrue(index.getScopables(scope1, scope8).contains(occurrence));
		assertEquals(2, index.getScopables(scope4, scope7).size());
		assertTrue(index.getScopables(scope4, scope7).contains(name));
		assertTrue(index.getScopables(scope4, scope7).contains(variant));

		association.addTheme(anotherTheme);
		assertEquals(1, index.getScopables(scope1, scope8).size());
		assertTrue(index.getScopables(scope1, scope8).contains(occurrence));
		assertEquals(2, index.getScopables(scope4, scope7).size());
		assertTrue(index.getScopables(scope4, scope7).contains(name));
		assertTrue(index.getScopables(scope4, scope7).contains(variant));
		assertEquals(1, index.getScopables(scope3, scope5).size());
		assertTrue(index.getScopables(scope3, scope5).contains(association));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getAssociationScopes()}
	 * .
	 */
	public void testGetAssociationScopes() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		assertEquals(0, index.getAssociationScopes().size());

		Association association = createAssociation(createTopic());
		assertEquals(1, index.getAssociationScopes().size());
		assertTrue(index.getAssociationScopes().contains(scope8));

		association.addTheme(theme);
		assertEquals(1, index.getAssociationScopes().size());
		assertTrue(index.getAssociationScopes().contains(scope1));

		association.addTheme(anotherTheme);
		assertEquals(1, index.getAssociationScopes().size());
		assertTrue(index.getAssociationScopes().contains(scope5));

		Association other = topicMap.createAssociation(createTopic(), theme, anotherTheme, otherTheme);
		assertEquals(2, index.getAssociationScopes().size());
		assertTrue(index.getAssociationScopes().contains(scope5));
		assertTrue(index.getAssociationScopes().contains(scope7));

		other.removeTheme(otherTheme);
		assertEquals(1, index.getAssociationScopes().size());
		assertTrue(index.getAssociationScopes().contains(scope5));

		other.removeTheme(theme);
		assertEquals(2, index.getAssociationScopes().size());
		assertTrue(index.getAssociationScopes().contains(scope5));
		assertTrue(index.getAssociationScopes().contains(scope3));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getAssociations(de.topicmapslab.majortom.model.core.IScope)}
	 * .
	 */
	public void testGetAssociationsIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Association association = topicMap.createAssociation(createTopic(), theme);
		Association other = topicMap.createAssociation(createTopic(), theme, anotherTheme, otherTheme);

		assertEquals(0, index.getAssociations(scope8).size());
		assertEquals(1, index.getAssociations(scope1).size());
		assertTrue(index.getAssociations(scope1).contains(association));
		assertEquals(1, index.getAssociations(scope7).size());
		assertTrue(index.getAssociations(scope7).contains(other));

		association.removeTheme(theme);
		assertEquals(1, index.getAssociations(scope8).size());
		assertTrue(index.getAssociations(scope8).contains(association));
		assertEquals(0, index.getAssociations(scope1).size());
		assertEquals(1, index.getAssociations(scope7).size());
		assertTrue(index.getAssociations(scope7).contains(other));

		association.addTheme(anotherTheme);
		assertEquals(0, index.getAssociations(scope8).size());
		assertEquals(1, index.getAssociations(scope3).size());
		assertTrue(index.getAssociations(scope3).contains(association));
		assertEquals(1, index.getAssociations(scope7).size());
		assertTrue(index.getAssociations(scope7).contains(other));

	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getAssociations(de.topicmapslab.majortom.model.core.IScope[])}
	 * .
	 */
	public void testGetAssociationsIScopeArray() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Association association = topicMap.createAssociation(createTopic(), theme);
		Association other = topicMap.createAssociation(createTopic(), theme, anotherTheme, otherTheme);

		assertEquals(0, index.getAssociations(scope8, scope3).size());
		assertEquals(1, index.getAssociations(scope8, scope3, scope1).size());
		assertTrue(index.getAssociations(scope8, scope3, scope1).contains(association));
		assertEquals(1, index.getAssociations(scope8, scope3, scope7).size());
		assertTrue(index.getAssociations(scope8, scope3, scope7).contains(other));

		association.removeTheme(theme);
		assertEquals(1, index.getAssociations(scope8, scope3, scope8).size());
		assertTrue(index.getAssociations(scope8, scope3, scope8).contains(association));
		assertEquals(0, index.getAssociations(scope3, scope1).size());
		assertEquals(1, index.getAssociations(scope1, scope7).size());
		assertTrue(index.getAssociations(scope1, scope7).contains(other));
		assertEquals(2, index.getAssociations(scope8, scope7).size());
		assertTrue(index.getAssociations(scope8, scope7).contains(other));
		assertTrue(index.getAssociations(scope8, scope7).contains(association));

		association.addTheme(anotherTheme);
		assertEquals(1, index.getAssociations(scope8, scope3, scope8).size());
		assertTrue(index.getAssociations(scope8, scope3, scope8).contains(association));
		assertEquals(0, index.getAssociations(scope8, scope1).size());
		assertEquals(1, index.getAssociations(scope1, scope7).size());
		assertTrue(index.getAssociations(scope1, scope7).contains(other));
		assertEquals(2, index.getAssociations(scope3, scope7).size());
		assertTrue(index.getAssociations(scope3, scope7).contains(other));
		assertTrue(index.getAssociations(scope3, scope7).contains(association));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getAssociations(java.util.Collection)}
	 * .
	 */
	public void testGetAssociationsCollectionOfIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Association association = topicMap.createAssociation(createTopic(), theme);
		Association other = topicMap.createAssociation(createTopic(), theme, anotherTheme, otherTheme);

		assertEquals(0, index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3 })).size());
		assertEquals(1, index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3, scope1 })).size());
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3, scope1 })).contains(association));
		assertEquals(1, index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3, scope7 })).size());
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3, scope7 })).contains(other));

		association.removeTheme(theme);
		assertEquals(1, index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3, scope8 })).size());
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3, scope8 })).contains(association));
		assertEquals(0, index.getAssociations(Arrays.asList(new IScope[] { scope3, scope1 })).size());
		assertEquals(1, index.getAssociations(Arrays.asList(new IScope[] { scope1, scope7 })).size());
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope1, scope7 })).contains(other));
		assertEquals(2, index.getAssociations(Arrays.asList(new IScope[] { scope8, scope7 })).size());
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope8, scope7 })).contains(other));
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope8, scope7 })).contains(association));

		association.addTheme(anotherTheme);
		assertEquals(1, index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3, scope8 })).size());
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope8, scope3, scope8 })).contains(association));
		assertEquals(0, index.getAssociations(Arrays.asList(new IScope[] { scope8, scope1 })).size());
		assertEquals(1, index.getAssociations(Arrays.asList(new IScope[] { scope1, scope7 })).size());
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope1, scope7 })).contains(other));
		assertEquals(2, index.getAssociations(Arrays.asList(new IScope[] { scope3, scope7 })).size());
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope3, scope7 })).contains(other));
		assertTrue(index.getAssociations(Arrays.asList(new IScope[] { scope3, scope7 })).contains(association));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getCharacteristics(de.topicmapslab.majortom.model.core.IScope)}
	 * .
	 */
	public void testGetCharacteristicsIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Name name = createTopic().createName("name", theme);
		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "other", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getCharacteristics(scope8).size());
		assertEquals(1, index.getCharacteristics(scope1).size());
		assertTrue(index.getCharacteristics(scope1).contains(name));
		assertEquals(1, index.getCharacteristics(scope7).size());
		assertTrue(index.getCharacteristics(scope7).contains(occurrence));

		name.removeTheme(theme);
		assertEquals(1, index.getCharacteristics(scope8).size());
		assertTrue(index.getCharacteristics(scope8).contains(name));
		assertEquals(0, index.getCharacteristics(scope1).size());
		assertEquals(1, index.getCharacteristics(scope7).size());
		assertTrue(index.getCharacteristics(scope7).contains(occurrence));

		name.addTheme(anotherTheme);
		assertEquals(0, index.getCharacteristics(scope8).size());
		assertEquals(1, index.getCharacteristics(scope3).size());
		assertTrue(index.getCharacteristics(scope3).contains(name));
		assertEquals(1, index.getCharacteristics(scope7).size());
		assertTrue(index.getCharacteristics(scope7).contains(occurrence));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getCharacteristics(de.topicmapslab.majortom.model.core.IScope[])}
	 * .
	 */
	public void testGetCharacteristicsIScopeArray() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Name name = createTopic().createName("name", theme);
		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "other", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getCharacteristics(scope8, scope3).size());
		assertEquals(1, index.getCharacteristics(scope8, scope3, scope1).size());
		assertTrue(index.getCharacteristics(scope8, scope3, scope1).contains(name));
		assertEquals(1, index.getCharacteristics(scope8, scope3, scope7).size());
		assertTrue(index.getCharacteristics(scope8, scope3, scope7).contains(occurrence));

		name.removeTheme(theme);
		assertEquals(1, index.getCharacteristics(scope8, scope3, scope8).size());
		assertTrue(index.getCharacteristics(scope8, scope3, scope8).contains(name));
		assertEquals(0, index.getCharacteristics(scope3, scope1).size());
		assertEquals(1, index.getCharacteristics(scope1, scope7).size());
		assertTrue(index.getCharacteristics(scope1, scope7).contains(occurrence));
		assertEquals(2, index.getCharacteristics(scope8, scope7).size());
		assertTrue(index.getCharacteristics(scope8, scope7).contains(occurrence));
		assertTrue(index.getCharacteristics(scope8, scope7).contains(name));

		name.addTheme(anotherTheme);
		assertEquals(1, index.getCharacteristics(scope8, scope3, scope8).size());
		assertTrue(index.getCharacteristics(scope8, scope3, scope8).contains(name));
		assertEquals(0, index.getCharacteristics(scope8, scope1).size());
		assertEquals(1, index.getCharacteristics(scope1, scope7).size());
		assertTrue(index.getCharacteristics(scope1, scope7).contains(occurrence));
		assertEquals(2, index.getCharacteristics(scope3, scope7).size());
		assertTrue(index.getCharacteristics(scope3, scope7).contains(occurrence));
		assertTrue(index.getCharacteristics(scope3, scope7).contains(name));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getNameScopes()}
	 * .
	 */
	public void testGetNameScopes() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		assertEquals(0, index.getNameScopes().size());

		Name name = createTopic().createName("Name", new Topic[0]);
		assertEquals(1, index.getNameScopes().size());
		assertTrue(index.getNameScopes().contains(scope8));

		name.addTheme(theme);
		assertEquals(1, index.getNameScopes().size());
		assertTrue(index.getNameScopes().contains(scope1));

		name.addTheme(anotherTheme);
		assertEquals(1, index.getNameScopes().size());
		assertTrue(index.getNameScopes().contains(scope5));

		Name other = createTopic().createName("Other", theme, anotherTheme, otherTheme);
		assertEquals(2, index.getNameScopes().size());
		assertTrue(index.getNameScopes().contains(scope5));
		assertTrue(index.getNameScopes().contains(scope7));

		other.removeTheme(otherTheme);
		assertEquals(1, index.getNameScopes().size());
		assertTrue(index.getNameScopes().contains(scope5));

		other.removeTheme(theme);
		assertEquals(2, index.getNameScopes().size());
		assertTrue(index.getNameScopes().contains(scope5));
		assertTrue(index.getNameScopes().contains(scope3));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getNames(de.topicmapslab.majortom.model.core.IScope)}
	 * .
	 */
	public void testGetNamesIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Name name = createTopic().createName("name", theme);
		Name otherName = createTopic().createName("other", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getNames(scope8).size());
		assertEquals(1, index.getNames(scope1).size());
		assertTrue(index.getNames(scope1).contains(name));
		assertEquals(1, index.getNames(scope7).size());
		assertTrue(index.getNames(scope7).contains(otherName));

		name.removeTheme(theme);
		assertEquals(1, index.getNames(scope8).size());
		assertTrue(index.getNames(scope8).contains(name));
		assertEquals(0, index.getNames(scope1).size());
		assertEquals(1, index.getNames(scope7).size());
		assertTrue(index.getNames(scope7).contains(otherName));

		name.addTheme(anotherTheme);
		assertEquals(0, index.getNames(scope8).size());
		assertEquals(1, index.getNames(scope3).size());
		assertTrue(index.getNames(scope3).contains(name));
		assertEquals(1, index.getNames(scope7).size());
		assertTrue(index.getNames(scope7).contains(otherName));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getNames(de.topicmapslab.majortom.model.core.IScope[])}
	 * .
	 */
	public void testGetNamesIScopeArray() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Name name = createTopic().createName("name", theme);
		Name other = createTopic().createName("other", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getNames(scope8, scope3).size());
		assertEquals(1, index.getNames(scope8, scope3, scope1).size());
		assertTrue(index.getNames(scope8, scope3, scope1).contains(name));
		assertEquals(1, index.getNames(scope8, scope3, scope7).size());
		assertTrue(index.getNames(scope8, scope3, scope7).contains(other));

		name.removeTheme(theme);
		assertEquals(1, index.getNames(scope8, scope3, scope8).size());
		assertTrue(index.getNames(scope8, scope3, scope8).contains(name));
		assertEquals(0, index.getNames(scope3, scope1).size());
		assertEquals(1, index.getNames(scope1, scope7).size());
		assertTrue(index.getNames(scope1, scope7).contains(other));
		assertEquals(2, index.getNames(scope8, scope7).size());
		assertTrue(index.getNames(scope8, scope7).contains(other));
		assertTrue(index.getNames(scope8, scope7).contains(name));

		name.addTheme(anotherTheme);
		assertEquals(1, index.getNames(scope8, scope3, scope8).size());
		assertTrue(index.getNames(scope8, scope3, scope8).contains(name));
		assertEquals(0, index.getNames(scope8, scope1).size());
		assertEquals(1, index.getNames(scope1, scope7).size());
		assertTrue(index.getNames(scope1, scope7).contains(other));
		assertEquals(2, index.getNames(scope3, scope7).size());
		assertTrue(index.getNames(scope3, scope7).contains(other));
		assertTrue(index.getNames(scope3, scope7).contains(name));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getNames(java.util.Collection)}
	 * .
	 */
	public void testGetNamesCollectionOfIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Name name = createTopic().createName("name", theme);
		Name other = createTopic().createName("other", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getNames(Arrays.asList(scope8, scope3)).size());
		assertEquals(1, index.getNames(Arrays.asList(scope8, scope3, scope1)).size());
		assertTrue(index.getNames(Arrays.asList(scope8, scope3, scope1)).contains(name));
		assertEquals(1, index.getNames(Arrays.asList(scope8, scope3, scope7)).size());
		assertTrue(index.getNames(Arrays.asList(scope8, scope3, scope7)).contains(other));

		name.removeTheme(theme);
		assertEquals(1, index.getNames(Arrays.asList(scope8, scope3, scope8)).size());
		assertTrue(index.getNames(Arrays.asList(scope8, scope3, scope8)).contains(name));
		assertEquals(0, index.getNames(Arrays.asList(scope3, scope1)).size());
		assertEquals(1, index.getNames(Arrays.asList(scope1, scope7)).size());
		assertTrue(index.getNames(Arrays.asList(scope1, scope7)).contains(other));
		assertEquals(2, index.getNames(Arrays.asList(scope8, scope7)).size());
		assertTrue(index.getNames(Arrays.asList(scope8, scope7)).contains(other));
		assertTrue(index.getNames(Arrays.asList(scope8, scope7)).contains(name));

		name.addTheme(anotherTheme);
		assertEquals(1, index.getNames(Arrays.asList(scope8, scope3, scope8)).size());
		assertTrue(index.getNames(Arrays.asList(scope8, scope3, scope8)).contains(name));
		assertEquals(0, index.getNames(Arrays.asList(scope8, scope1)).size());
		assertEquals(1, index.getNames(Arrays.asList(scope1, scope7)).size());
		assertTrue(index.getNames(Arrays.asList(scope1, scope7)).contains(other));
		assertEquals(2, index.getNames(Arrays.asList(scope3, scope7)).size());
		assertTrue(index.getNames(Arrays.asList(scope3, scope7)).contains(other));
		assertTrue(index.getNames(Arrays.asList(scope3, scope7)).contains(name));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getOccurrenceScopes()}
	 * .
	 */
	public void testGetOccurrenceScopes() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		assertEquals(0, index.getOccurrenceScopes().size());

		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "Occ", new Topic[0]);
		assertEquals(1, index.getOccurrenceScopes().size());
		assertTrue(index.getOccurrenceScopes().contains(scope8));

		occurrence.addTheme(theme);
		assertEquals(1, index.getOccurrenceScopes().size());
		assertTrue(index.getOccurrenceScopes().contains(scope1));

		occurrence.addTheme(anotherTheme);
		assertEquals(1, index.getOccurrenceScopes().size());
		assertTrue(index.getOccurrenceScopes().contains(scope5));

		Occurrence other = createTopic().createOccurrence(createTopic(), "Other", theme, anotherTheme, otherTheme);
		assertEquals(2, index.getOccurrenceScopes().size());
		assertTrue(index.getOccurrenceScopes().contains(scope5));
		assertTrue(index.getOccurrenceScopes().contains(scope7));

		other.removeTheme(otherTheme);
		assertEquals(1, index.getOccurrenceScopes().size());
		assertTrue(index.getOccurrenceScopes().contains(scope5));

		other.removeTheme(theme);
		assertEquals(2, index.getOccurrenceScopes().size());
		assertTrue(index.getOccurrenceScopes().contains(scope5));
		assertTrue(index.getOccurrenceScopes().contains(scope3));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getOccurrences(de.topicmapslab.majortom.model.core.IScope)}
	 * .
	 */
	public void testGetOccurrencesIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Occurrence occurrence = createTopic().createOccurrence(createTopic(),"occ", theme);
		Occurrence otherName = createTopic().createOccurrence(createTopic(),"other", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getOccurrences(scope8).size());
		assertEquals(1, index.getOccurrences(scope1).size());
		assertTrue(index.getOccurrences(scope1).contains(occurrence));
		assertEquals(1, index.getOccurrences(scope7).size());
		assertTrue(index.getOccurrences(scope7).contains(otherName));

		occurrence.removeTheme(theme);
		assertEquals(1, index.getOccurrences(scope8).size());
		assertTrue(index.getOccurrences(scope8).contains(occurrence));
		assertEquals(0, index.getOccurrences(scope1).size());
		assertEquals(1, index.getOccurrences(scope7).size());
		assertTrue(index.getOccurrences(scope7).contains(otherName));

		occurrence.addTheme(anotherTheme);
		assertEquals(0, index.getOccurrences(scope8).size());
		assertEquals(1, index.getOccurrences(scope3).size());
		assertTrue(index.getOccurrences(scope3).contains(occurrence));
		assertEquals(1, index.getOccurrences(scope7).size());
		assertTrue(index.getOccurrences(scope7).contains(otherName));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getOccurrences(de.topicmapslab.majortom.model.core.IScope[])}
	 * .
	 */
	public void testGetOccurrencesIScopeArray() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}

		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "occ", theme);
		Occurrence other = createTopic().createOccurrence(createTopic(), "other", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getOccurrences(scope8, scope3).size());
		assertEquals(1, index.getOccurrences(scope8, scope3, scope1).size());
		assertTrue(index.getOccurrences(scope8, scope3, scope1).contains(occurrence));
		assertEquals(1, index.getOccurrences(scope8, scope3, scope7).size());
		assertTrue(index.getOccurrences(scope8, scope3, scope7).contains(other));

		occurrence.removeTheme(theme);
		assertEquals(1, index.getOccurrences(scope8, scope3, scope8).size());
		assertTrue(index.getOccurrences(scope8, scope3, scope8).contains(occurrence));
		assertEquals(0, index.getOccurrences(scope3, scope1).size());
		assertEquals(1, index.getOccurrences(scope1, scope7).size());
		assertTrue(index.getOccurrences(scope1, scope7).contains(other));
		assertEquals(2, index.getOccurrences(scope8, scope7).size());
		assertTrue(index.getOccurrences(scope8, scope7).contains(other));
		assertTrue(index.getOccurrences(scope8, scope7).contains(occurrence));

		occurrence.addTheme(anotherTheme);
		assertEquals(1, index.getOccurrences(scope8, scope3, scope8).size());
		assertTrue(index.getOccurrences(scope8, scope3, scope8).contains(occurrence));
		assertEquals(0, index.getOccurrences(scope8, scope1).size());
		assertEquals(1, index.getOccurrences(scope1, scope7).size());
		assertTrue(index.getOccurrences(scope1, scope7).contains(other));
		assertEquals(2, index.getOccurrences(scope3, scope7).size());
		assertTrue(index.getOccurrences(scope3, scope7).contains(other));
		assertTrue(index.getOccurrences(scope3, scope7).contains(occurrence));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getOccurrences(java.util.Collection)}
	 * .
	 */
	public void testGetOccurrencesCollectionOfIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}


		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Occurrence occurrence = createTopic().createOccurrence(createTopic(), "occ", theme);
		Occurrence other = createTopic().createOccurrence(createTopic(), "other", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getOccurrences(Arrays.asList(scope8, scope3)).size());
		assertEquals(1, index.getOccurrences(Arrays.asList(scope8, scope3, scope1)).size());
		assertTrue(index.getOccurrences(Arrays.asList(scope8, scope3, scope1)).contains(occurrence));
		assertEquals(1, index.getOccurrences(Arrays.asList(scope8, scope3, scope7)).size());
		assertTrue(index.getOccurrences(Arrays.asList(scope8, scope3, scope7)).contains(other));

		occurrence.removeTheme(theme);
		assertEquals(1, index.getOccurrences(Arrays.asList(scope8, scope3, scope8)).size());
		assertTrue(index.getOccurrences(Arrays.asList(scope8, scope3, scope8)).contains(occurrence));
		assertEquals(0, index.getOccurrences(Arrays.asList(scope3, scope1)).size());
		assertEquals(1, index.getOccurrences(Arrays.asList(scope1, scope7)).size());
		assertTrue(index.getOccurrences(Arrays.asList(scope1, scope7)).contains(other));
		assertEquals(2, index.getOccurrences(Arrays.asList(scope8, scope7)).size());
		assertTrue(index.getOccurrences(Arrays.asList(scope8, scope7)).contains(other));
		assertTrue(index.getOccurrences(Arrays.asList(scope8, scope7)).contains(occurrence));

		occurrence.addTheme(anotherTheme);
		assertEquals(1, index.getOccurrences(Arrays.asList(scope8, scope3, scope8)).size());
		assertTrue(index.getOccurrences(Arrays.asList(scope8, scope3, scope8)).contains(occurrence));
		assertEquals(0, index.getOccurrences(Arrays.asList(scope8, scope1)).size());
		assertEquals(1, index.getOccurrences(Arrays.asList(scope1, scope7)).size());
		assertTrue(index.getOccurrences(Arrays.asList(scope1, scope7)).contains(other));
		assertEquals(2, index.getOccurrences(Arrays.asList(scope3, scope7)).size());
		assertTrue(index.getOccurrences(Arrays.asList(scope3, scope7)).contains(other));
		assertTrue(index.getOccurrences(Arrays.asList(scope3, scope7)).contains(occurrence));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getVariantScopes()}
	 * .
	 */
	public void testGetVariantScopes() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope3 = index.getScope(anotherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);

		assertEquals(0, index.getVariantScopes().size());

		Variant variant = createTopic().createName("Name", new Topic[0]).createVariant("Variant", anotherTheme);
		assertEquals(1, index.getVariantScopes().size());
		assertTrue(index.getVariantScopes().contains(scope3));

		variant.addTheme(theme);
		assertEquals(1, index.getVariantScopes().size());
		assertTrue(index.getVariantScopes().contains(scope5));

		variant.addTheme(anotherTheme);
		assertEquals(1, index.getVariantScopes().size());
		assertTrue(index.getVariantScopes().contains(scope5));

		Variant other = createTopic().createName("Other", new Topic[0]).createVariant("Variant", theme, anotherTheme, otherTheme);
		assertEquals(2, index.getVariantScopes().size());
		assertTrue(index.getVariantScopes().contains(scope5));
		assertTrue(index.getVariantScopes().contains(scope7));

		other.removeTheme(otherTheme);
		assertEquals(1, index.getVariantScopes().size());
		assertTrue(index.getVariantScopes().contains(scope5));

		other.removeTheme(theme);
		assertEquals(2, index.getVariantScopes().size());
		assertTrue(index.getVariantScopes().contains(scope5));
		assertTrue(index.getVariantScopes().contains(scope3));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getVariants(de.topicmapslab.majortom.model.core.IScope)}
	 * .
	 */
	public void testGetVariantsIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);	
		IScope scope4 = index.getScope(otherTheme, anotherTheme);
		IScope scope5 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope();

		Variant variant = createTopic().createName("name",  new Topic[0]).createVariant("Variant", theme);
		Variant other = createTopic().createName("other", new Topic[0]).createVariant("Variant", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getVariants(scope8).size());
		assertEquals(1, index.getVariants(scope1).size());
		assertTrue(index.getVariants(scope1).contains(variant));
		assertEquals(1, index.getVariants(scope7).size());
		assertTrue(index.getVariants(scope7).contains(other));

		other.removeTheme(theme);
		assertEquals(1, index.getVariants(scope1).size());
		assertTrue(index.getVariants(scope1).contains(variant));
		assertEquals(1, index.getVariants(scope4).size());
		assertTrue(index.getVariants(scope4).contains(other));

		variant.addTheme(anotherTheme);
		assertEquals(1, index.getVariants(scope5).size());
		assertTrue(index.getVariants(scope5).contains(variant));
		assertEquals(1, index.getVariants(scope4).size());
		assertTrue(index.getVariants(scope4).contains(other));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getVariants(de.topicmapslab.majortom.model.core.IScope[])}
	 * .
	 */
	public void testGetVariantsIScopeArray() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope4 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope(otherTheme, anotherTheme);

		Variant variant = createTopic().createName("name",  new Topic[0]).createVariant("Variant", theme);
		Variant other = createTopic().createName("other", new Topic[0]).createVariant("Variant", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getVariants(scope8, scope3).size());
		assertEquals(1, index.getVariants(scope8, scope3, scope1).size());
		assertTrue(index.getVariants(scope8, scope3, scope1).contains(variant));
		assertEquals(1, index.getVariants(scope8, scope3, scope7).size());
		assertTrue(index.getVariants(scope8, scope3, scope7).contains(other));

		other.removeTheme(theme);
		assertEquals(1, index.getVariants(scope8, scope3, scope8).size());
		assertTrue(index.getVariants(scope8, scope3, scope8).contains(other));
		assertEquals(1, index.getVariants(scope1, scope7).size());
		assertTrue(index.getVariants(scope1, scope7).contains(variant));
		assertEquals(2, index.getVariants(scope8, scope1).size());
		assertTrue(index.getVariants(scope8, scope1).contains(other));
		assertTrue(index.getVariants(scope8, scope1).contains(variant));

		variant.addTheme(anotherTheme);
		assertEquals(1, index.getVariants(scope8, scope3, scope8).size());
		assertTrue(index.getVariants(scope8, scope3, scope8).contains(other));
		assertEquals(0, index.getVariants(scope1, scope7).size());
		assertEquals(1, index.getVariants(scope1, scope4, scope7).size());
		assertTrue(index.getVariants(scope7, scope1, scope4).contains(variant));
		assertEquals(2, index.getVariants(scope4, scope8).size());
		assertTrue(index.getVariants(scope4, scope8).contains(other));
		assertTrue(index.getVariants(scope4, scope8).contains(variant));
	}

	/**
	 * Test method for
	 * {@link de.topicmapslab.majortom.inMemory.index.InMemoryScopedIndex#getVariants(java.util.Collection)}
	 * .
	 */
	public void testGetVariantsCollectionOfIScope() {
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		assertNotNull(index);
		try {
			index.getScope();
			fail("Index should be close!");
		} catch (TMAPIRuntimeException e) {
			index.open();
		}
		ITopic theme = createTopic();
		ITopic otherTheme = createTopic();
		ITopic anotherTheme = createTopic();

		IScope scope1 = index.getScope(theme);
		IScope scope3 = index.getScope(anotherTheme);
		IScope scope4 = index.getScope(theme, anotherTheme);
		IScope scope7 = index.getScope(theme, otherTheme, anotherTheme);
		IScope scope8 = index.getScope(otherTheme, anotherTheme);

		Variant variant = createTopic().createName("name",  new Topic[0]).createVariant("Variant", theme);
		Variant other = createTopic().createName("other", new Topic[0]).createVariant("Variant", theme, anotherTheme, otherTheme);

		assertEquals(0, index.getVariants(Arrays.asList(scope8, scope3)).size());
		assertEquals(1, index.getVariants(Arrays.asList(scope8, scope3, scope1)).size());
		assertTrue(index.getVariants(Arrays.asList(scope8, scope3, scope1)).contains(variant));
		assertEquals(1, index.getVariants(Arrays.asList(scope8, scope3, scope7)).size());
		assertTrue(index.getVariants(Arrays.asList(scope8, scope3, scope7)).contains(other));

		other.removeTheme(theme);
		assertEquals(1, index.getVariants(Arrays.asList(scope8, scope3, scope8)).size());
		assertTrue(index.getVariants(Arrays.asList(scope8, scope3, scope8)).contains(other));
		assertEquals(1, index.getVariants(Arrays.asList(scope1, scope7)).size());
		assertTrue(index.getVariants(Arrays.asList(scope1, scope7)).contains(variant));
		assertEquals(2, index.getVariants(Arrays.asList(scope8, scope1)).size());
		assertTrue(index.getVariants(Arrays.asList(scope8, scope1)).contains(other));
		assertTrue(index.getVariants(Arrays.asList(scope8, scope1)).contains(variant));

		variant.addTheme(anotherTheme);
		assertEquals(1, index.getVariants(Arrays.asList(scope8, scope3, scope8)).size());
		assertTrue(index.getVariants(Arrays.asList(scope8, scope3, scope8)).contains(other));
		assertEquals(0, index.getVariants(Arrays.asList(scope1, scope7)).size());
		assertEquals(1, index.getVariants(Arrays.asList(scope1, scope4, scope7)).size());
		assertTrue(index.getVariants(Arrays.asList(scope7, scope1, scope4)).contains(variant));
		assertEquals(2, index.getVariants(Arrays.asList(scope4, scope8)).size());
		assertTrue(index.getVariants(Arrays.asList(scope4, scope8)).contains(other));
		assertTrue(index.getVariants(Arrays.asList(scope4, scope8)).contains(variant));
	}

}
