package de.topicmapslab.majortom.tests.index;

import java.util.Set;

import junit.framework.Assert;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.HashUtil;
import de.topicmapslab.majortom.util.TmdmSubjectIdentifier;

public class TestSupertypeSubtypeIndex extends MaJorToMTestCase {

	public void testGetSubtypes() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSubtypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		type.addSupertype(supertypeA);
		Assert.assertFalse(index.getSubtypes().isEmpty());
		Assert.assertTrue(index.getSubtypes().size() == 1);
		Assert.assertTrue(index.getSubtypes().contains(type));
		ITopic supertypeB = createTopic();
		supertypeA.addSupertype(supertypeB);
		Assert.assertFalse(index.getSubtypes().isEmpty());
		Assert.assertTrue(index.getSubtypes().size() == 2);
		Assert.assertTrue(index.getSubtypes().contains(type));
		Assert.assertTrue(index.getSubtypes().contains(supertypeA));
		ITopic supertypeC = createTopic();
		type.addSupertype(supertypeC);
		Assert.assertFalse(index.getSubtypes().isEmpty());
		Assert.assertTrue(index.getSubtypes().size() == 2);
		Assert.assertTrue(index.getSubtypes().contains(type));
		Assert.assertTrue(index.getSubtypes().contains(supertypeA));
		supertypeB.addSupertype(supertypeC);
		Assert.assertFalse(index.getSubtypes().isEmpty());
		Assert.assertTrue(index.getSubtypes().size() == 3);
		Assert.assertTrue(index.getSubtypes().contains(type));
		Assert.assertTrue(index.getSubtypes().contains(supertypeA));
		Assert.assertTrue(index.getSubtypes().contains(supertypeB));
		index.close();
	}

	public void testGetSubtypesTopic() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		ITopic supertypeB = createTopic();
		ITopic supertypeC = createTopic();
		Assert.assertTrue(index.getSubtypes((Topic) null).size() == 4);
		type.addSupertype(supertypeA);
		/*
		 * the tmdm types for supertype-subtype assoc and roles will created
		 */
		Assert.assertTrue(index.getSubtypes((Topic) null).size() == 6);
		Assert.assertTrue(index.getSubtypes((Topic) null).contains(type));
		Assert.assertTrue(index.getSubtypes((Topic) null).contains(supertypeB));
		Assert.assertTrue(index.getSubtypes((Topic) null).contains(supertypeC));
		Assert.assertTrue(index.getSubtypes(supertypeA).contains(type));
		Assert.assertTrue(index.getSubtypes(supertypeA).size() == 1);
		Assert.assertTrue(index.getSubtypes(supertypeA).contains(type));
		supertypeC.addSupertype(supertypeB);
		Assert.assertTrue(index.getSubtypes((Topic) null).size() == 5);
		Assert.assertTrue(index.getSubtypes((Topic) null).contains(type));
		Assert.assertTrue(index.getSubtypes((Topic) null).contains(supertypeC));
		Assert.assertTrue(index.getSubtypes(supertypeB).size() == 1);
		Assert.assertTrue(index.getSubtypes(supertypeB).contains(supertypeC));
		supertypeB.addSupertype(supertypeA);
		Assert.assertTrue(index.getSubtypes(supertypeA).size() == 3);
		Assert.assertTrue(index.getSubtypes(supertypeA).contains(type));
		Assert.assertTrue(index.getSubtypes(supertypeA).contains(supertypeB));
		Assert.assertTrue(index.getSubtypes(supertypeA).contains(supertypeC));
		Assert.assertEquals(5, index.getSubtypes((Topic) null).size());
		Assert.assertTrue(index.getSubtypes((Topic) null).contains(type));
		Assert.assertTrue(index.getSubtypes((Topic) null).contains(supertypeC));
	}

	public void testGetSubtypesTopicArray() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		ITopic supertypeB = createTopic();
		ITopic supertypeC = createTopic();
		try {
			index.getSubtypes((Topic[]) null);
			Assert.fail("Type array null is not allowed!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO DO
		}

		type.addSupertype(supertypeA);
		Assert.assertTrue(index.getSubtypes(supertypeA).size() == 1);
		Assert.assertTrue(index.getSubtypes(supertypeA).contains(type));
		Assert.assertTrue(index.getSubtypes(supertypeA, supertypeB).size() == 1);
		Assert.assertTrue(index.getSubtypes(supertypeA, supertypeB).contains(type));
		supertypeC.addSupertype(supertypeB);
		Assert.assertTrue(index.getSubtypes(supertypeA, supertypeB).size() == 2);
		Assert.assertTrue(index.getSubtypes(supertypeA, supertypeB).contains(type));
		Assert.assertTrue(index.getSubtypes(supertypeA, supertypeB).contains(supertypeC));
		type.addSupertype(supertypeB);
		Assert.assertTrue(index.getSubtypes(supertypeA, supertypeB).size() == 2);
		Assert.assertTrue(index.getSubtypes(supertypeA, supertypeB).contains(type));
		Assert.assertTrue(index.getSubtypes(supertypeA, supertypeB).contains(supertypeC));
	}

	public void testGetSubtypesCollectionOfQextendsTopic() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		ITopic supertypeB = createTopic();
		ITopic supertypeC = createTopic();
		try {
			index.getSubtypes(null, true);
			Assert.fail("Type array null is not allowed!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO DO
		}

		type.addSupertype(supertypeA);
		Set<Topic> set = HashUtil.getHashSet();
		set.add(supertypeA);
		Assert.assertTrue(index.getSubtypes(set).size() == 1);
		Assert.assertTrue(index.getSubtypes(set).contains(type));
		set.add(supertypeB);
		Assert.assertTrue(index.getSubtypes(set).size() == 1);
		Assert.assertTrue(index.getSubtypes(set).contains(type));
		supertypeC.addSupertype(supertypeB);
		Assert.assertTrue(index.getSubtypes(set).size() == 2);
		Assert.assertTrue(index.getSubtypes(set).contains(type));
		Assert.assertTrue(index.getSubtypes(set).contains(supertypeC));
		type.addSupertype(supertypeB);
		Assert.assertTrue(index.getSubtypes(set).size() == 2);
		Assert.assertTrue(index.getSubtypes(set).contains(type));
		Assert.assertTrue(index.getSubtypes(set).contains(supertypeC));
	}

	public void testGetSubtypesCollectionOfQextendsTopicBoolean() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		ITopic supertypeB = createTopic();
		ITopic supertypeC = createTopic();
		try {
			index.getSubtypes(null, true);
			Assert.fail("Type array null is not allowed!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO DO
		}

		type.addSupertype(supertypeA);
		Set<Topic> set = HashUtil.getHashSet();
		set.add(supertypeA);
		Assert.assertTrue(index.getSubtypes(set, false).size() == 1);
		Assert.assertTrue(index.getSubtypes(set, false).contains(type));
		set.add(supertypeB);
		Assert.assertTrue(index.getSubtypes(set, false).size() == 1);
		Assert.assertTrue(index.getSubtypes(set, false).contains(type));
		Assert.assertTrue(index.getSubtypes(set, true).isEmpty());
		supertypeC.addSupertype(supertypeB);
		Assert.assertTrue(index.getSubtypes(set, false).size() == 2);
		Assert.assertTrue(index.getSubtypes(set, false).contains(type));
		Assert.assertTrue(index.getSubtypes(set, false).contains(supertypeC));
		Assert.assertTrue(index.getSubtypes(set, true).isEmpty());
		type.addSupertype(supertypeB);
		Assert.assertTrue(index.getSubtypes(set, false).size() == 2);
		Assert.assertTrue(index.getSubtypes(set, false).contains(type));
		Assert.assertTrue(index.getSubtypes(set, false).contains(supertypeC));
		Assert.assertFalse(index.getSubtypes(set, true).isEmpty());
		Assert.assertTrue(index.getSubtypes(set, true).contains(type));
	}

	public void testGetSupertypes() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		type.addSupertype(supertypeA);
		assertFalse(topicMap.getAssociations().isEmpty());
		assertEquals(1, type.getAssociationsPlayed(topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TmdmSubjectIdentifier.TMDM_SUPERTYPE_SUBTYPE_ASSOCIATION))).size());
		assertEquals(1, type.getRolesPlayed(topicMap.getTopicBySubjectIdentifier(topicMap.createLocator(TmdmSubjectIdentifier.TMDM_SUBTYPE_ROLE_TYPE))).size());
		Assert.assertFalse(index.getSupertypes().isEmpty());
		Assert.assertTrue(index.getSupertypes().size() == 1);
		Assert.assertTrue(index.getSupertypes().contains(supertypeA));
		ITopic supertypeB = createTopic();
		supertypeA.addSupertype(supertypeB);
		assertFalse(topicMap.getAssociations().isEmpty());
		Assert.assertFalse(index.getSupertypes().isEmpty());
		Assert.assertTrue(index.getSupertypes().size() == 2);
		Assert.assertTrue(index.getSupertypes().contains(supertypeA));
		Assert.assertTrue(index.getSupertypes().contains(supertypeB));
		ITopic supertypeC = createTopic();
		type.addSupertype(supertypeC);
		assertFalse(topicMap.getAssociations().isEmpty());
		Assert.assertFalse(index.getSupertypes().isEmpty());
		Assert.assertTrue(index.getSupertypes().size() == 3);
		Assert.assertTrue(index.getSupertypes().contains(supertypeB));
		Assert.assertTrue(index.getSupertypes().contains(supertypeA));
		Assert.assertTrue(index.getSupertypes().contains(supertypeC));
		supertypeB.addSupertype(supertypeC);
		assertFalse(topicMap.getAssociations().isEmpty());
		Assert.assertFalse(index.getSupertypes().isEmpty());
		Assert.assertTrue(index.getSupertypes().size() == 3);
		Assert.assertTrue(index.getSupertypes().contains(supertypeC));
		Assert.assertTrue(index.getSupertypes().contains(supertypeA));
		Assert.assertTrue(index.getSupertypes().contains(supertypeB));
		index.close();
	}

	public void testGetSupertypesTopic() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		type.addSupertype(supertypeA);
		Assert.assertFalse(index.getSupertypes(type).isEmpty());
		Assert.assertTrue(index.getSupertypes(type).size() == 1);
		Assert.assertTrue(index.getSupertypes(type).contains(supertypeA));
		// TMDM Type-Hierarchy Topic Types created
		Assert.assertTrue(index.getSupertypes((Topic) null).size() == 4);
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeA));
		ITopic supertypeB = createTopic();
		Assert.assertTrue(index.getSupertypes((Topic) null).size() == 5);
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeB));
		supertypeA.addSupertype(supertypeB);
		Assert.assertFalse(index.getSupertypes().isEmpty());
		Assert.assertTrue(index.getSupertypes().size() == 2);
		Assert.assertTrue(index.getSupertypes(supertypeA).contains(supertypeB));
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeB));
		Assert.assertTrue(index.getSupertypes(type).size() == 2);
		Assert.assertTrue(index.getSupertypes(type).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(type).contains(supertypeB));
		ITopic supertypeC = createTopic();
		Assert.assertTrue(index.getSupertypes((Topic) null).size() == 5);
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeC));
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeB));
		type.addSupertype(supertypeC);
		Assert.assertFalse(index.getSupertypes().isEmpty());
		Assert.assertTrue(index.getSupertypes().size() == 3);
		Assert.assertTrue(index.getSupertypes(type).contains(supertypeC));
		Assert.assertTrue(index.getSupertypes((Topic) null).size() == 5);
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeC));
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeB));
		supertypeB.addSupertype(supertypeC);
		Assert.assertFalse(index.getSupertypes().isEmpty());
		Assert.assertTrue(index.getSupertypes().size() == 3);
		Assert.assertTrue(index.getSupertypes(supertypeB).contains(supertypeC));
		Assert.assertTrue(index.getSupertypes(type).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(type).contains(supertypeC));
		Assert.assertTrue(index.getSupertypes(supertypeA).contains(supertypeB));
		Assert.assertTrue(index.getSupertypes((Topic) null).size() == 4);
		Assert.assertTrue(index.getSupertypes((Topic) null).contains(supertypeC));
		index.close();
	}

	public void testGetSupertypesTopicArray() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		ITopic supertypeB = createTopic();
		ITopic supertypeC = createTopic();
		try {
			index.getSupertypes((Topic[]) null);
			Assert.fail("Type array null is not allowed!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO DO
		}

		type.addSupertype(supertypeA);
		Assert.assertTrue(index.getSupertypes(type).size() == 1);
		Assert.assertTrue(index.getSupertypes(type).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(type, supertypeC).size() == 1);
		Assert.assertTrue(index.getSupertypes(type, supertypeC).contains(supertypeA));
		supertypeC.addSupertype(supertypeB);
		Assert.assertTrue(index.getSupertypes(type, supertypeC).size() == 2);
		Assert.assertTrue(index.getSupertypes(type, supertypeC).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(type, supertypeC).contains(supertypeB));
		type.addSupertype(supertypeC);
		Assert.assertTrue(index.getSupertypes(type, supertypeC).size() == 3);
		Assert.assertTrue(index.getSupertypes(type, supertypeC).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(type, supertypeC).contains(supertypeB));
		Assert.assertTrue(index.getSupertypes(type, supertypeC).contains(supertypeC));
	}

	public void testGetSupertypesCollectionOfQextendsTopic() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		ITopic supertypeB = createTopic();
		ITopic supertypeC = createTopic();
		try {
			index.getSupertypes(null, true);
			Assert.fail("Type array null is not allowed!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO DO
		}

		type.addSupertype(supertypeA);
		Set<Topic> set = HashUtil.getHashSet();
		set.add(type);
		Assert.assertTrue(index.getSupertypes(set).size() == 1);
		Assert.assertTrue(index.getSupertypes(set).contains(supertypeA));
		set.add(supertypeC);
		Assert.assertTrue(index.getSupertypes(set).size() == 1);
		Assert.assertTrue(index.getSupertypes(set).contains(supertypeA));
		supertypeC.addSupertype(supertypeB);
		Assert.assertTrue(index.getSupertypes(set).size() == 2);
		Assert.assertTrue(index.getSupertypes(set).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(set).contains(supertypeB));
		type.addSupertype(supertypeC);
		Assert.assertTrue(index.getSupertypes(set).size() == 3);
		Assert.assertTrue(index.getSupertypes(set).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(set).contains(supertypeB));
		Assert.assertTrue(index.getSupertypes(set).contains(supertypeC));
	}

	public void testGetSupertypesCollectionOfQextendsTopicBoolean() {
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		index.open();
		Assert.assertNotNull(index);
		Assert.assertTrue(index.getSupertypes().isEmpty());
		ITopic type = createTopic();
		ITopic supertypeA = createTopic();
		ITopic supertypeB = createTopic();
		ITopic supertypeC = createTopic();
		try {
			index.getSupertypes(null, true);
			Assert.fail("Type array null is not allowed!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO DO
		}

		type.addSupertype(supertypeA);
		Set<Topic> set = HashUtil.getHashSet();
		set.add(type);
		Assert.assertTrue(index.getSupertypes(set, false).size() == 1);
		Assert.assertTrue(index.getSupertypes(set, false).contains(supertypeA));
		set.add(supertypeC);
		Assert.assertTrue(index.getSupertypes(set, false).size() == 1);
		Assert.assertTrue(index.getSupertypes(set, false).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(set, true).isEmpty());
		supertypeC.addSupertype(supertypeB);
		Assert.assertTrue(index.getSupertypes(set, false).size() == 2);
		Assert.assertTrue(index.getSupertypes(set, false).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(set, false).contains(supertypeB));
		Assert.assertTrue(index.getSupertypes(set, true).isEmpty());
		type.addSupertype(supertypeC);
		Assert.assertTrue(index.getSupertypes(set, false).size() == 3);
		Assert.assertTrue(index.getSupertypes(set, false).contains(supertypeA));
		Assert.assertTrue(index.getSupertypes(set, false).contains(supertypeB));
		Assert.assertTrue(index.getSupertypes(set, false).contains(supertypeC));
		Assert.assertFalse(index.getSupertypes(set, true).isEmpty());
		Assert.assertTrue(index.getSupertypes(set, true).contains(supertypeB));
	}
	
	public void testTypeCycle() throws Exception {
		ITopic type = createTopic();
		ITopic otherType = createTopic();
		type.addSupertype(otherType);
		otherType.addSupertype(type);
		ITopic instance = createTopic();
		instance.addType(type);
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		index.open();

		assertEquals(1, index.getTopics(type).size());
		assertEquals(1, index.getTopics(otherType).size());
				
		assertEquals(1, instance.getTypes().size());
		assertTrue(instance.getTypes().contains(type));
		
		ITopic anotherType = createTopic();
		anotherType.addSupertype(type);
		assertEquals(2, anotherType.getSupertypes().size());
		assertTrue(anotherType.getSupertypes().contains(type));
		assertTrue(anotherType.getSupertypes().contains(otherType));
		
		ISupertypeSubtypeIndex subtypeIndex = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		subtypeIndex.open();
		
		assertEquals(3, subtypeIndex.getSubtypes().size());
		
		assertEquals(3, subtypeIndex.getSubtypes(type).size());
		assertTrue(subtypeIndex.getSubtypes(type).contains(otherType));
		assertTrue(subtypeIndex.getSubtypes(type).contains(type));
		assertTrue(subtypeIndex.getSubtypes(type).contains(anotherType));
		
		assertEquals(3, subtypeIndex.getSubtypes(otherType).size());
		assertTrue(subtypeIndex.getSubtypes(otherType).contains(otherType));
		assertTrue(subtypeIndex.getSubtypes(otherType).contains(type));
		assertTrue(subtypeIndex.getSubtypes(otherType).contains(anotherType));
		
		assertEquals(0, subtypeIndex.getSubtypes(anotherType).size());
		
		assertEquals(2, subtypeIndex.getSupertypes().size());
		assertTrue(subtypeIndex.getSupertypes().contains(otherType));
		assertTrue(subtypeIndex.getSupertypes().contains(type));
		
		assertEquals(2, subtypeIndex.getSupertypes(type).size());
		assertTrue(subtypeIndex.getSupertypes(type).contains(otherType));
		assertTrue(subtypeIndex.getSupertypes(type).contains(type));
		
		assertEquals(2, subtypeIndex.getSupertypes(otherType).size());
		assertTrue(subtypeIndex.getSupertypes(otherType).contains(otherType));
		assertTrue(subtypeIndex.getSupertypes(otherType).contains(type));
		
		assertEquals(2, subtypeIndex.getSupertypes(otherType).size());
		assertTrue(subtypeIndex.getSupertypes(otherType).contains(otherType));
		assertTrue(subtypeIndex.getSupertypes(otherType).contains(type));
		
	}

}
