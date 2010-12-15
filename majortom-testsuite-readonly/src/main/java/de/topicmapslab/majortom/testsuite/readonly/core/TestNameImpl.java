/**
 * 
 */
package de.topicmapslab.majortom.testsuite.readonly.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.exception.UnmodifyableStoreException;
import de.topicmapslab.majortom.testsuite.readonly.AbstractTest;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;

public class TestNameImpl extends AbstractTest {

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#createVariant(java.lang.String, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantStringTopicArray/topic/1)
	 * has exactly one name
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantStringTopicArray/theme1)
	 * Topic (http://TestNameImpl/testCreateVariantStringTopicArray/theme2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateVariantStringTopicArray() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringTopicArray/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringTopicArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringTopicArray/theme1"));
		assertNotNull(theme2);
		
		name.createVariant("variant", theme1, theme2);
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#createVariant(java.lang.String, java.util.Collection)}.
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantStringCollectionOfTopic/topic/1)
	 * has exactly one name
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantStringCollectionOfTopic/theme1)
	 * Topic (http://TestNameImpl/testCreateVariantStringCollectionOfTopic/theme2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateVariantStringCollectionOfTopic() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringTopicArray/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringTopicArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringTopicArray/theme1"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		name.createVariant("variant", themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#createVariant(org.tmapi.core.Locator, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantLocatorTopicArray/topic/1)
	 * has exactly one name
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantLocatorTopicArray/theme1)
	 * Topic (http://TestNameImpl/testCreateVariantLocatorTopicArray/theme2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateVariantLocatorTopicArray() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantLocatorTopicArray/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantLocatorTopicArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantLocatorTopicArray/theme1"));
		assertNotNull(theme2);
		
		Locator l = map.createLocator("http://TestNameImpl/testCreateVariantLocatorTopicArray/");
		assertNotNull(l);
		
		name.createVariant(l, theme1, theme2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#createVariant(org.tmapi.core.Locator, java.util.Collection)}.
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantLocatorCollectionOfTopic/topic/1)
	 * has exactly one name
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantLocatorCollectionOfTopic/theme1)
	 * Topic (http://TestNameImpl/testCreateVariantLocatorCollectionOfTopic/theme2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateVariantLocatorCollectionOfTopic() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantLocatorCollectionOfTopic/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantLocatorCollectionOfTopic/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantLocatorCollectionOfTopic/theme1"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		Locator l = map.createLocator("http://TestNameImpl/testCreateVariantLocatorCollectionOfTopic/");
		assertNotNull(l);
		
		name.createVariant(l, themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#createVariant(java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantStringLocatorTopicArray/topic/1)
	 * has exactly one name
	 *  
	 * Topic (http://TestNameImpl/testCreateVariantStringLocatorTopicArray/theme1)
	 * Topic (http://TestNameImpl/testCreateVariantStringLocatorTopicArray/theme2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateVariantStringLocatorTopicArray() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringLocatorTopicArray/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringLocatorTopicArray/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringLocatorTopicArray/theme1"));
		assertNotNull(theme2);
		
		Locator l = map.createLocator("xsd:string");
		assertNotNull(l);
		
		name.createVariant("variant",l, theme1, theme2);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#createVariant(java.lang.String, org.tmapi.core.Locator, java.util.Collection)}.
	 * 
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#createVariant(java.lang.String, org.tmapi.core.Locator, org.tmapi.core.Topic[])}.
	 * 
	 * Topic (http://TestNameImpl/testCreateVariantStringLocatorCollectionOfTopic/topic/1)
	 * has exactly one name
	 *  
	 * Topic (http://TestNameImpl/testCreateVariantStringLocatorCollectionOfTopic/theme1)
	 * Topic (http://TestNameImpl/testCreateVariantStringLocatorCollectionOfTopic/theme2)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testCreateVariantStringLocatorCollectionOfTopic() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringLocatorCollectionOfTopic/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		ITopic theme1 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringLocatorCollectionOfTopic/theme1"));
		assertNotNull(theme1);
		
		ITopic theme2 = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testCreateVariantStringLocatorCollectionOfTopic/theme1"));
		assertNotNull(theme2);
		
		Collection<Topic> themes = new HashSet<Topic>();
		themes.add(theme1);
		themes.add(theme2);
		
		Locator l = map.createLocator("xsd:string");
		assertNotNull(l);
		
		name.createVariant("variant",l, themes);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#getVariants()}.
	 */
	@Test
	public void testGetVariants() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#getVariants(de.topicmapslab.majortom.model.core.IScope)}.
	 */
	@Test
	public void testGetVariantsIScope() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#setType(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestNameImpl/testSetType/topic/1)
	 * has exactly one name with default type
	 *  
	 * Topic (http://TestNameImpl/testSetType/nametype)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetType() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testSetType/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testSetType/nametype"));
		assertNotNull(type);
		
		name.setType(type);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#getType()}.
	 * 
	 * Topic (http://TestNameImpl/testGetType/topic/1)
	 * has 1 name with default type
	 * and 1 name with type (http://TestNameImpl/testGetType/nametype)
	 */
	@Test
	public void testGetType() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetType/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(2, names.size());
		
		ITopic type = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetType/nametype"));
		assertNotNull(type);
		
		ITopic defaulttype = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator(TmdmSubjectIdentifier.TMDM_DEFAULT_NAME_TYPE));
		assertNotNull(defaulttype);
		
		Object[] na = names.toArray();
		
		assertNotSame(((Name)na[0]).getType(), ((Name)na[1]).getType());
		assertTrue(((Name)na[0]).getType().equals(type) || ((Name)na[0]).getType().equals(defaulttype));
		assertTrue(((Name)na[1]).getType().equals(type) || ((Name)na[1]).getType().equals(defaulttype));
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#getParent()}.
	 * 
	 * Topic (http://TestNameImpl/testGetParent/topic/1)
	 * has exactly 1 name
	 */
	@Test
	public void testGetParent() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetParent/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		assertEquals(topic, name.getParent());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#setValue(java.lang.String)}.
	 * 
	 * Topic (http://TestNameImpl/testSetValue/topic/1)
	 * has exactly 1 name
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetValue() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testSetValue/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		
		name.setValue("new value");
		
		
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#getValue()}.
	 * 
	 * Topic (http://TestNameImpl/testGetValue/topic/1)
	 * has exactly 1 name with the value "value"
	 */
	@Test
	public void testGetValue() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetValue/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		assertEquals("value", name.getValue());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#getVariants(int, int)}.
	 */
	@Ignore
	@Test
	public void testGetVariantsIntInt() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#getVariants(int, int, java.util.Comparator)}.
	 */
	@Ignore
	@Test
	public void testGetVariantsIntIntComparatorOfVariant() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.NameImpl#getNumberOfVariants()}.
	 */
	@Ignore
	@Test
	public void testGetNumberOfVariants() {

		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScopeObject()}.
	 * Topic (http://TestNameImpl/testGetScopeObject/topic/1)
	 * has exactly 1 name with single theme scope (http://TestNameImpl/testGetScopeObject/theme)
	 * 
	 */
	@Test
	public void testGetScopeObject() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetScopeObject/topic/1"));
		assertNotNull(topic);
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetScopeObject/theme"));
		assertNotNull(theme);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		IScope scope = ((IName)name).getScopeObject();
		assertEquals(1, scope.getThemes().size());
		assertTrue(scope.containsTheme(theme));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#addTheme(org.tmapi.core.Topic)}.
	 * 
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScopeObject()}.
	 * Topic (http://TestNameImpl/testAddTheme/topic/1)
	 * has exactly 1 name without scope
	 * 
	 * Topic (http://TestNameImpl/testAddTheme/theme1)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddTheme() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testAddTheme/topic/1"));
		assertNotNull(topic);
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testAddTheme/theme"));
		assertNotNull(theme);
				
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		name.addTheme(theme);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#getScope()}.
	 * 
	 * Topic (http://TestNameImpl/testGetScope/topic/1)
	 * has exactly 1 name with single theme scope (http://TestNameImpl/testGetScope/theme)
	 */
	@Test
	public void testGetScope() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetScope/topic/1"));
		assertNotNull(topic);
		
		ITopic theme = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetScope/theme"));
		assertNotNull(theme);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		Set<Topic> themes = name.getScope();
		assertEquals(1, themes.size());
		assertTrue(themes.contains(theme));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ScopeableImpl#removeTheme(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestNameImpl/testRemoveTheme/topic/1)
	 * has exactly 1 name with single theme scope (http://TestNameImpl/testRemoveTheme/theme)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemoveTheme() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testRemoveTheme/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		Set<Topic> themes = name.getScope();
		assertEquals(1, themes.size());
		Topic theme = themes.iterator().next();
		name.removeTheme(theme);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#getReifier()}.
	 */
	@Test
	public void testGetReifier() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ReifiableImpl#setReifier(org.tmapi.core.Topic)}.
	 * 
	 * Topic (http://TestNameImpl/testSetReifier/topic/1)
	 * has exactly 1 name without reifier
	 * 
	 * Topic (http://TestNameImpl/testSetReifier/ref)
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testSetReifier() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testSetReifier/topic/1"));
		assertNotNull(topic);
		
		ITopic ref = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testSetReifier/ref"));
		assertNotNull(ref);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		name.setReifier(ref);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getTopicMap()}.
	 * 
	 * Topic (http://TestNameImpl/testSetReifier/topic/1)
	 * has exactly 1 name
	 */
	@Test
	public void testGetTopicMap() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testSetReifier/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		assertEquals(map, name.getTopicMap());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#addItemIdentifier(org.tmapi.core.Locator)}.
	 * 
	 * Topic (http://TestNameImpl/testAddItemIdentifier/topic/1)
	 * has exactly 1 name
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testAddItemIdentifier() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testAddItemIdentifier/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		Locator l = map.createLocator("http://TestNameImpl/testAddItemIdentifier");
		assertNotNull(l);
		name.addItemIdentifier(l);
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getId()}.
	 * 
	 * Topic (http://TestNameImpl/testGetId/topic/1)
	 * has exactly 1 name
	 */
	@Test
	public void testGetId() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testGetId/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		String id = name.getId();
		assertNotNull(id);
		assertEquals(id, name.getId());
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#getItemIdentifiers()}.
	 */
	@Test
	public void testGetItemIdentifiers() {

		/// TODO implement
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#remove()}.
	 * 
	 * Topic (http://TestNameImpl/testRemove/topic/1)
	 * has exactly 1 name
	 */
	@Test(expected=UnmodifyableStoreException.class)
	public void testRemove() {

		assertNotNull(map);

		ITopic topic = (ITopic)map.getTopicBySubjectIdentifier(map.createLocator("http://TestNameImpl/testRemove/topic/1"));
		assertNotNull(topic);
		
		Set<Name> names = topic.getNames();
		assertEquals(1, names.size());
		
		Name name = names.iterator().next();
		name.remove();
	}


	/**
	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#removeItemIdentifier(org.tmapi.core.Locator)}.
	 */
	@Test
	public void testRemoveItemIdentifier() {

		/// TODO implement
		fail("Not yet implemented");
	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#compareTo(de.topicmapslab.majortom.model.core.IConstruct)}.
//	 */
//	@Test
//	public void testCompareTo() {
//
//		fail("Not yet implemented");
//	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#equals(java.lang.Object)}.
//	 */
//	@Test
//	public void testEqualsObject() {
//
//		fail("Not yet implemented");
//	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#isRemoved()}.
//	 */
//	@Test
//	public void testIsRemoved() {
//
//		fail("Not yet implemented");
//	}

//	/**
//	 * Test method for {@link de.topicmapslab.majortom.core.ConstructImpl#setRemoved(boolean)}.
//	 */
//	@Test
//	public void testSetRemoved() {
//
//		fail("Not yet implemented");
//	}
}
