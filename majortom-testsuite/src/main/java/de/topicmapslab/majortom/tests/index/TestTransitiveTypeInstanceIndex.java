package de.topicmapslab.majortom.tests.index;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.tmapi.core.Role;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ITransitiveTypeInstanceIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;
import de.topicmapslab.majortom.util.FeatureStrings;

public class TestTransitiveTypeInstanceIndex extends MaJorToMTestCase {

	public void testGetAssociationsTopicArray() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getAssociations((Topic[]) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IAssociation association = createAssociation(type);

		assertTrue(index.getAssociations(new Topic[] { type }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { type }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getAssociations(new Topic[] { otherType }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getAssociations(new Topic[] { type }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { type }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { supertype }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { otherType }).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getAssociations(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getAssociations(new Topic[] { type }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { type }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { otherType }).isEmpty());

		association.setType(otherType);
		assertTrue(index.getAssociations(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { otherType }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { type }).isEmpty());
		assertTrue(index.getAssociations(new Topic[] { supertype }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getAssociations(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { otherType }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { type }).isEmpty());
		assertTrue(index.getAssociations(new Topic[] { supertype }).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getAssociations(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { otherType }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { type }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { type }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { supertype }).contains(association));
		association.setType(supertype);
		assertTrue(index.getAssociations(new Topic[] { type }).isEmpty());
		assertTrue(index.getAssociations(new Topic[] { otherType }).isEmpty());
		assertTrue(index.getAssociations(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { supertype }).contains(association));

		IAssociation association2 = createAssociation(type);
		type.removeSupertype(supertype);
		assertTrue(index.getAssociations(new Topic[] { type }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { type }).contains(association2));
		assertTrue(index.getAssociations(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getAssociations(new Topic[] { supertype }).contains(association));
		assertTrue(index.getAssociations(new Topic[] { otherType }).isEmpty());
		assertTrue(index.getAssociations(new Topic[] { type, supertype }).size() == 2);
		assertTrue(index.getAssociations(new Topic[] { type, supertype }).contains(association2));
		assertTrue(index.getAssociations(new Topic[] { type, supertype }).contains(association));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getAssociations(new Topic[] { type, otherSuperType }).size() == 2);
		assertTrue(index.getAssociations(new Topic[] { type, otherSuperType }).contains(association2));
		assertTrue(index.getAssociations(new Topic[] { type, otherSuperType }).contains(association));

		index.close();
	}

	public void testGetAssociationsCollectionOfQextendsTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getAssociations((Collection<Topic>) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IAssociation association = createAssociation(type);

		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).isEmpty());

		association.setType(otherType);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).contains(association));
		association.setType(supertype);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).isEmpty());
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).contains(association));

		IAssociation association2 = createAssociation(type);
		type.removeSupertype(supertype);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type })).contains(association2));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { supertype })).contains(association));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { otherType })).isEmpty());
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type, supertype })).size() == 2);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type, supertype })).contains(association2));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type, supertype })).contains(association));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type, supertype })).size() == 2);
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type, otherSuperType })).contains(association2));
		assertTrue(index.getAssociations(Arrays.asList(new Topic[] { type, otherSuperType })).contains(association));

		index.close();
	}

	public void testGetAssociationsTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getAssociations((Topic) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IAssociation association = createAssociation(type);

		assertTrue(index.getAssociations(type).size() == 1);
		assertTrue(index.getAssociations(type).contains(association));
		assertTrue(index.getAssociations(supertype).isEmpty());
		assertTrue(index.getAssociations(otherType).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getAssociations(type).size() == 1);
		assertTrue(index.getAssociations(type).contains(association));
		assertTrue(index.getAssociations(supertype).size() == 1);
		assertTrue(index.getAssociations(supertype).contains(association));
		assertTrue(index.getAssociations(otherType).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getAssociations(supertype).isEmpty());
		assertTrue(index.getAssociations(type).size() == 1);
		assertTrue(index.getAssociations(type).contains(association));
		assertTrue(index.getAssociations(otherType).isEmpty());

		association.setType(otherType);
		assertTrue(index.getAssociations(otherType).size() == 1);
		assertTrue(index.getAssociations(otherType).contains(association));
		assertTrue(index.getAssociations(type).isEmpty());
		assertTrue(index.getAssociations(supertype).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getAssociations(otherType).size() == 1);
		assertTrue(index.getAssociations(otherType).contains(association));
		assertTrue(index.getAssociations(type).isEmpty());
		assertTrue(index.getAssociations(supertype).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getAssociations(otherType).size() == 1);
		assertTrue(index.getAssociations(otherType).contains(association));
		assertTrue(index.getAssociations(type).size() == 1);
		assertTrue(index.getAssociations(type).contains(association));
		assertTrue(index.getAssociations(supertype).size() == 1);
		assertTrue(index.getAssociations(supertype).contains(association));
		association.setType(supertype);
		assertTrue(index.getAssociations(type).isEmpty());
		assertTrue(index.getAssociations(otherType).isEmpty());
		assertTrue(index.getAssociations(supertype).size() == 1);
		assertTrue(index.getAssociations(supertype).contains(association));

		IAssociation association2 = createAssociation(type);
		type.removeSupertype(supertype);
		assertTrue(index.getAssociations(type).size() == 1);
		assertTrue(index.getAssociations(type).contains(association2));
		assertTrue(index.getAssociations(supertype).size() == 1);
		assertTrue(index.getAssociations(supertype).contains(association));
		assertTrue(index.getAssociations(otherType).isEmpty());

		index.close();
	}

	public void testGetCharacteristicsTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getCharacteristics((Topic) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(type, "Occurrence", new Topic[0]);

		assertTrue(index.getCharacteristics(type).size() == 1);
		assertTrue(index.getCharacteristics(type).contains(occurrence));
		assertTrue(index.getCharacteristics(supertype).isEmpty());
		assertTrue(index.getCharacteristics(otherType).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getCharacteristics(type).size() == 1);
		assertTrue(index.getCharacteristics(type).contains(occurrence));
		assertTrue(index.getCharacteristics(supertype).size() == 1);
		assertTrue(index.getCharacteristics(supertype).contains(occurrence));
		assertTrue(index.getCharacteristics(otherType).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getCharacteristics(supertype).isEmpty());
		assertTrue(index.getCharacteristics(type).size() == 1);
		assertTrue(index.getCharacteristics(type).contains(occurrence));
		assertTrue(index.getCharacteristics(otherType).isEmpty());

		occurrence.setType(otherType);
		assertTrue(index.getCharacteristics(otherType).size() == 1);
		assertTrue(index.getCharacteristics(otherType).contains(occurrence));
		assertTrue(index.getCharacteristics(type).isEmpty());
		assertTrue(index.getCharacteristics(supertype).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getCharacteristics(otherType).size() == 1);
		assertTrue(index.getCharacteristics(otherType).contains(occurrence));
		assertTrue(index.getCharacteristics(type).isEmpty());
		assertTrue(index.getCharacteristics(supertype).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getCharacteristics(otherType).size() == 1);
		assertTrue(index.getCharacteristics(otherType).contains(occurrence));
		assertTrue(index.getCharacteristics(type).size() == 1);
		assertTrue(index.getCharacteristics(type).contains(occurrence));
		assertTrue(index.getCharacteristics(supertype).size() == 1);
		assertTrue(index.getCharacteristics(supertype).contains(occurrence));
		occurrence.setType(supertype);
		assertTrue(index.getCharacteristics(type).isEmpty());
		assertTrue(index.getCharacteristics(otherType).isEmpty());
		assertTrue(index.getCharacteristics(supertype).size() == 1);
		assertTrue(index.getCharacteristics(supertype).contains(occurrence));

		IName name = (IName) createTopic().createName(type, "Name", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getCharacteristics(type).size() == 1);
		assertTrue(index.getCharacteristics(type).contains(name));
		assertTrue(index.getCharacteristics(supertype).size() == 1);
		assertTrue(index.getCharacteristics(supertype).contains(occurrence));
		name.setType(supertype);
		assertTrue(index.getCharacteristics(supertype).size() == 2);
		assertTrue(index.getCharacteristics(supertype).contains(occurrence));
		assertTrue(index.getCharacteristics(supertype).contains(name));
		type.addSupertype(supertype);
		name.setType(type);
		assertTrue(index.getCharacteristics(supertype).size() == 2);
		assertTrue(index.getCharacteristics(supertype).contains(name));
		assertTrue(index.getCharacteristics(supertype).contains(occurrence));

		index.close();
	}

	public void testGetCharacteristicsTopicArray() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getCharacteristics((Topic[]) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(type, "Occurrence", new Topic[0]);

		assertTrue(index.getCharacteristics(new Topic[] { type }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { type }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getCharacteristics(new Topic[] { type }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { type }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getCharacteristics(new Topic[] { type }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { type }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).isEmpty());

		occurrence.setType(otherType);
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { type }).isEmpty());
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { type }).isEmpty());
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { type }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { type }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).contains(occurrence));
		occurrence.setType(supertype);
		assertTrue(index.getCharacteristics(new Topic[] { type }).isEmpty());
		assertTrue(index.getCharacteristics(new Topic[] { otherType }).isEmpty());
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).contains(occurrence));

		IName name = (IName) createTopic().createName(type, "Name", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getCharacteristics(new Topic[] { type }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { type }).contains(name));
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { type, supertype }).size() == 2);
		assertTrue(index.getCharacteristics(new Topic[] { type, supertype }).contains(name));
		assertTrue(index.getCharacteristics(new Topic[] { type, supertype }).contains(occurrence));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getCharacteristics(new Topic[] { type, otherSuperType }).size() == 2);
		assertTrue(index.getCharacteristics(new Topic[] { type, otherSuperType }).contains(name));
		assertTrue(index.getCharacteristics(new Topic[] { type, otherSuperType }).contains(occurrence));
		name.setType(supertype);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).contains(name));
		type.addSupertype(supertype);
		name.setType(type);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).contains(name));
		assertTrue(index.getCharacteristics(new Topic[] { supertype }).contains(occurrence));
		assertTrue(index.getCharacteristics(new Topic[] { otherSuperType }).size() == 2);
		assertTrue(index.getCharacteristics(new Topic[] { otherSuperType }).contains(name));
		assertTrue(index.getCharacteristics(new Topic[] { otherSuperType }).contains(occurrence));

		index.close();
	}

	public void testGetCharacteristicsCollectionOfQextendsTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getCharacteristics((Collection<Topic>) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(type, "Occurrence", new Topic[0]);

		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).isEmpty());

		occurrence.setType(otherType);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		occurrence.setType(supertype);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherType })).isEmpty());
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).contains(occurrence));

		IName name = (IName) createTopic().createName(type, "Name", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type })).contains(name));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type, supertype })).size() == 2);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type, supertype })).contains(name));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type, supertype })).contains(occurrence));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type, otherSuperType })).size() == 2);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type, otherSuperType })).contains(name));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { type, otherSuperType })).contains(occurrence));
		name.setType(supertype);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).contains(name));
		type.addSupertype(supertype);
		name.setType(type);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).contains(name));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherSuperType })).size() == 2);
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherSuperType })).contains(name));
		assertTrue(index.getCharacteristics(Arrays.asList(new Topic[] { otherSuperType })).contains(occurrence));

		index.close();
	}

	public void testGetRolesTopicArray() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getRoles((Topic[]) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IAssociation association = createAssociation(createTopic());
		Role role = association.createRole(type, createTopic());

		assertTrue(index.getRoles(new Topic[] { type }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { type }).contains(role));
		assertTrue(index.getRoles(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getRoles(new Topic[] { otherType }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getRoles(new Topic[] { type }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { type }).contains(role));
		assertTrue(index.getRoles(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { supertype }).contains(role));
		assertTrue(index.getRoles(new Topic[] { otherType }).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getRoles(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getRoles(new Topic[] { type }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { type }).contains(role));
		assertTrue(index.getRoles(new Topic[] { otherType }).isEmpty());

		role.setType(otherType);
		assertTrue(index.getRoles(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { otherType }).contains(role));
		assertTrue(index.getRoles(new Topic[] { type }).isEmpty());
		assertTrue(index.getRoles(new Topic[] { supertype }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getRoles(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { otherType }).contains(role));
		assertTrue(index.getRoles(new Topic[] { type }).isEmpty());
		assertTrue(index.getRoles(new Topic[] { supertype }).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getRoles(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { otherType }).contains(role));
		assertTrue(index.getRoles(new Topic[] { type }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { type }).contains(role));
		assertTrue(index.getRoles(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { supertype }).contains(role));
		role.setType(supertype);
		assertTrue(index.getRoles(new Topic[] { type }).isEmpty());
		assertTrue(index.getRoles(new Topic[] { otherType }).isEmpty());
		assertTrue(index.getRoles(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { supertype }).contains(role));

		Role role2 = association.createRole(type, createTopic());
		type.removeSupertype(supertype);
		assertTrue(index.getRoles(new Topic[] { type }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { type }).contains(role2));
		assertTrue(index.getRoles(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getRoles(new Topic[] { supertype }).contains(role));
		assertTrue(index.getRoles(new Topic[] { type, supertype }).size() == 2);
		assertTrue(index.getRoles(new Topic[] { type, supertype }).contains(role2));
		assertTrue(index.getRoles(new Topic[] { type, supertype }).contains(role));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getRoles(new Topic[] { type, otherSuperType }).size() == 2);
		assertTrue(index.getRoles(new Topic[] { type, otherSuperType }).contains(role2));
		assertTrue(index.getRoles(new Topic[] { type, otherSuperType }).contains(role));
		role2.setType(supertype);
		assertTrue(index.getRoles(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getRoles(new Topic[] { supertype }).contains(role));
		assertTrue(index.getRoles(new Topic[] { supertype }).contains(role2));
		type.addSupertype(supertype);
		role2.setType(type);
		assertTrue(index.getRoles(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getRoles(new Topic[] { supertype }).contains(role2));
		assertTrue(index.getRoles(new Topic[] { supertype }).contains(role));
		assertTrue(index.getRoles(new Topic[] { otherSuperType }).size() == 2);
		assertTrue(index.getRoles(new Topic[] { otherSuperType }).contains(role2));
		assertTrue(index.getRoles(new Topic[] { otherSuperType }).contains(role));

		index.close();
	}

	public void testGetRolesTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getRoles((Topic) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IAssociation association = createAssociation(createTopic());
		Role role = association.createRole(type, createTopic());

		assertTrue(index.getRoles(type).size() == 1);
		assertTrue(index.getRoles(type).contains(role));
		assertTrue(index.getRoles(supertype).isEmpty());
		assertTrue(index.getRoles(otherType).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getRoles(type).size() == 1);
		assertTrue(index.getRoles(type).contains(role));
		assertTrue(index.getRoles(supertype).size() == 1);
		assertTrue(index.getRoles(supertype).contains(role));
		assertTrue(index.getRoles(otherType).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getRoles(supertype).isEmpty());
		assertTrue(index.getRoles(type).size() == 1);
		assertTrue(index.getRoles(type).contains(role));
		assertTrue(index.getRoles(otherType).isEmpty());

		role.setType(otherType);
		assertTrue(index.getRoles(otherType).size() == 1);
		assertTrue(index.getRoles(otherType).contains(role));
		assertTrue(index.getRoles(type).isEmpty());
		assertTrue(index.getRoles(supertype).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getRoles(otherType).size() == 1);
		assertTrue(index.getRoles(otherType).contains(role));
		assertTrue(index.getRoles(type).isEmpty());
		assertTrue(index.getRoles(supertype).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getRoles(otherType).size() == 1);
		assertTrue(index.getRoles(otherType).contains(role));
		assertTrue(index.getRoles(type).size() == 1);
		assertTrue(index.getRoles(type).contains(role));
		assertTrue(index.getRoles(supertype).size() == 1);
		assertTrue(index.getRoles(supertype).contains(role));
		role.setType(supertype);
		assertTrue(index.getRoles(type).isEmpty());
		assertTrue(index.getRoles(otherType).isEmpty());
		assertTrue(index.getRoles(supertype).size() == 1);
		assertTrue(index.getRoles(supertype).contains(role));

		Role role2 = association.createRole(type, createTopic());
		type.removeSupertype(supertype);
		assertTrue(index.getRoles(type).size() == 1);
		assertTrue(index.getRoles(type).contains(role2));
		assertTrue(index.getRoles(supertype).size() == 1);
		assertTrue(index.getRoles(supertype).contains(role));
		assertTrue(index.getRoles(type, supertype).size() == 2);
		assertTrue(index.getRoles(type, supertype).contains(role2));
		assertTrue(index.getRoles(type, supertype).contains(role));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getRoles(type, otherSuperType).size() == 2);
		assertTrue(index.getRoles(type, otherSuperType).contains(role2));
		assertTrue(index.getRoles(type, otherSuperType).contains(role));
		role2.setType(supertype);
		assertTrue(index.getRoles(supertype).size() == 2);
		assertTrue(index.getRoles(supertype).contains(role));
		assertTrue(index.getRoles(supertype).contains(role2));
		type.addSupertype(supertype);
		role2.setType(type);
		assertTrue(index.getRoles(supertype).size() == 2);
		assertTrue(index.getRoles(supertype).contains(role2));
		assertTrue(index.getRoles(supertype).contains(role));
		assertTrue(index.getRoles(otherSuperType).size() == 2);
		assertTrue(index.getRoles(otherSuperType).contains(role2));
		assertTrue(index.getRoles(otherSuperType).contains(role));

		index.close();
	}

	public void testGetRolesCollectionOfQextendsTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getRoles((Collection<Topic>) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IAssociation association = createAssociation(createTopic());
		Role role = association.createRole(type, createTopic());

		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).isEmpty());

		role.setType(otherType);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).contains(role));
		role.setType(supertype);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherType })).isEmpty());
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).contains(role));

		Role role2 = association.createRole(type, createTopic());
		type.removeSupertype(supertype);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type })).contains(role2));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type, supertype })).size() == 2);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type, supertype })).contains(role2));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type, supertype })).contains(role));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type, otherSuperType })).size() == 2);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type, otherSuperType })).contains(role2));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { type, otherSuperType })).contains(role));
		role2.setType(supertype);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).contains(role2));
		type.addSupertype(supertype);
		role2.setType(type);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).contains(role2));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { supertype })).contains(role));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherSuperType })).size() == 2);
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherSuperType })).contains(role2));
		assertTrue(index.getRoles(Arrays.asList(new Topic[] { otherSuperType })).contains(role));

		index.close();
	}

	public void testGetNamesTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getNames((Topic) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IName name = (IName) createTopic().createName(type, "Name", new Topic[0]);

		assertTrue(index.getNames(type).size() == 1);
		assertTrue(index.getNames(type).contains(name));
		assertTrue(index.getNames(supertype).isEmpty());
		assertTrue(index.getNames(otherType).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getNames(type).size() == 1);
		assertTrue(index.getNames(type).contains(name));
		assertTrue(index.getNames(supertype).size() == 1);
		assertTrue(index.getNames(supertype).contains(name));
		assertTrue(index.getNames(otherType).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getNames(supertype).isEmpty());
		assertTrue(index.getNames(type).size() == 1);
		assertTrue(index.getNames(type).contains(name));
		assertTrue(index.getNames(otherType).isEmpty());

		name.setType(otherType);
		assertTrue(index.getNames(otherType).size() == 1);
		assertTrue(index.getNames(otherType).contains(name));
		assertTrue(index.getNames(type).isEmpty());
		assertTrue(index.getNames(supertype).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getNames(otherType).size() == 1);
		assertTrue(index.getNames(otherType).contains(name));
		assertTrue(index.getNames(type).isEmpty());
		assertTrue(index.getNames(supertype).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getNames(otherType).size() == 1);
		assertTrue(index.getNames(otherType).contains(name));
		assertTrue(index.getNames(type).size() == 1);
		assertTrue(index.getNames(type).contains(name));
		assertTrue(index.getNames(supertype).size() == 1);
		assertTrue(index.getNames(supertype).contains(name));
		name.setType(supertype);
		assertTrue(index.getNames(type).isEmpty());
		assertTrue(index.getNames(otherType).isEmpty());
		assertTrue(index.getNames(supertype).size() == 1);
		assertTrue(index.getNames(supertype).contains(name));

		IName name2 = (IName) createTopic().createName(type, "Name 2", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getNames(type).size() == 1);
		assertTrue(index.getNames(type).contains(name2));
		assertTrue(index.getNames(supertype).size() == 1);
		assertTrue(index.getNames(supertype).contains(name));
		assertTrue(index.getNames(type, supertype).size() == 2);
		assertTrue(index.getNames(type, supertype).contains(name2));
		assertTrue(index.getNames(type, supertype).contains(name));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getNames(type, otherSuperType).size() == 2);
		assertTrue(index.getNames(type, otherSuperType).contains(name2));
		assertTrue(index.getNames(type, otherSuperType).contains(name));
		name2.setType(supertype);
		assertTrue(index.getNames(supertype).size() == 2);
		assertTrue(index.getNames(supertype).contains(name));
		assertTrue(index.getNames(supertype).contains(name2));
		type.addSupertype(supertype);
		name2.setType(type);
		assertTrue(index.getNames(supertype).size() == 2);
		assertTrue(index.getNames(supertype).contains(name2));
		assertTrue(index.getNames(supertype).contains(name));
		assertTrue(index.getNames(otherSuperType).size() == 2);
		assertTrue(index.getNames(otherSuperType).contains(name2));
		assertTrue(index.getNames(otherSuperType).contains(name));

		index.close();
	}

	public void testGetNamesTopicArray() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getNames((Topic[]) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IName name = (IName) createTopic().createName(type, "Name", new Topic[0]);

		assertTrue(index.getNames(new Topic[] { type }).size() == 1);
		assertTrue(index.getNames(new Topic[] { type }).contains(name));
		assertTrue(index.getNames(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getNames(new Topic[] { otherType }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getNames(new Topic[] { type }).size() == 1);
		assertTrue(index.getNames(new Topic[] { type }).contains(name));
		assertTrue(index.getNames(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getNames(new Topic[] { supertype }).contains(name));
		assertTrue(index.getNames(new Topic[] { otherType }).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getNames(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getNames(new Topic[] { type }).size() == 1);
		assertTrue(index.getNames(new Topic[] { type }).contains(name));
		assertTrue(index.getNames(new Topic[] { otherType }).isEmpty());

		name.setType(otherType);
		assertTrue(index.getNames(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getNames(new Topic[] { otherType }).contains(name));
		assertTrue(index.getNames(new Topic[] { type }).isEmpty());
		assertTrue(index.getNames(new Topic[] { supertype }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getNames(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getNames(new Topic[] { otherType }).contains(name));
		assertTrue(index.getNames(new Topic[] { type }).isEmpty());
		assertTrue(index.getNames(new Topic[] { supertype }).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getNames(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getNames(new Topic[] { otherType }).contains(name));
		assertTrue(index.getNames(new Topic[] { type }).size() == 1);
		assertTrue(index.getNames(new Topic[] { type }).contains(name));
		assertTrue(index.getNames(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getNames(new Topic[] { supertype }).contains(name));
		name.setType(supertype);
		assertTrue(index.getNames(new Topic[] { type }).isEmpty());
		assertTrue(index.getNames(new Topic[] { otherType }).isEmpty());
		assertTrue(index.getNames(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getNames(new Topic[] { supertype }).contains(name));

		IName name2 = (IName) createTopic().createName(type, "Name 2", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getNames(new Topic[] { type }).size() == 1);
		assertTrue(index.getNames(new Topic[] { type }).contains(name2));
		assertTrue(index.getNames(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getNames(new Topic[] { supertype }).contains(name));
		assertTrue(index.getNames(new Topic[] { type, supertype }).size() == 2);
		assertTrue(index.getNames(new Topic[] { type, supertype }).contains(name2));
		assertTrue(index.getNames(new Topic[] { type, supertype }).contains(name));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getNames(new Topic[] { type, otherSuperType }).size() == 2);
		assertTrue(index.getNames(new Topic[] { type, otherSuperType }).contains(name2));
		assertTrue(index.getNames(new Topic[] { type, otherSuperType }).contains(name));
		name2.setType(supertype);
		assertTrue(index.getNames(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getNames(new Topic[] { supertype }).contains(name));
		assertTrue(index.getNames(new Topic[] { supertype }).contains(name2));
		type.addSupertype(supertype);
		name2.setType(type);
		assertTrue(index.getNames(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getNames(new Topic[] { supertype }).contains(name2));
		assertTrue(index.getNames(new Topic[] { supertype }).contains(name));
		assertTrue(index.getNames(new Topic[] { otherSuperType }).size() == 2);
		assertTrue(index.getNames(new Topic[] { otherSuperType }).contains(name2));
		assertTrue(index.getNames(new Topic[] { otherSuperType }).contains(name));

		index.close();
	}

	public void testGetNamesCollectionOfQextendsTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getNames((Collection<Topic>) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IName name = (IName) createTopic().createName(type, "Name", new Topic[0]);

		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).isEmpty());

		name.setType(otherType);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).contains(name));
		name.setType(supertype);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherType })).isEmpty());
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).contains(name));

		IName name2 = (IName) createTopic().createName(type, "Name 2", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type })).contains(name2));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type, supertype })).size() == 2);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type, supertype })).contains(name2));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type, supertype })).contains(name));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type, otherSuperType })).size() == 2);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type, otherSuperType })).contains(name2));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { type, otherSuperType })).contains(name));
		name2.setType(supertype);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).contains(name2));
		type.addSupertype(supertype);
		name2.setType(type);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).contains(name2));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { supertype })).contains(name));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherSuperType })).size() == 2);
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherSuperType })).contains(name2));
		assertTrue(index.getNames(Arrays.asList(new Topic[] { otherSuperType })).contains(name));

		index.close();
	}

	public void testGetOccurrencesTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getOccurrences((Topic) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(type, "Occurrence", new Topic[0]);

		assertTrue(index.getOccurrences(type).size() == 1);
		assertTrue(index.getOccurrences(type).contains(occurrence));
		assertTrue(index.getOccurrences(supertype).isEmpty());
		assertTrue(index.getOccurrences(otherType).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getOccurrences(type).size() == 1);
		assertTrue(index.getOccurrences(type).contains(occurrence));
		assertTrue(index.getOccurrences(supertype).size() == 1);
		assertTrue(index.getOccurrences(supertype).contains(occurrence));
		assertTrue(index.getOccurrences(otherType).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getOccurrences(supertype).isEmpty());
		assertTrue(index.getOccurrences(type).size() == 1);
		assertTrue(index.getOccurrences(type).contains(occurrence));
		assertTrue(index.getOccurrences(otherType).isEmpty());

		occurrence.setType(otherType);
		assertTrue(index.getOccurrences(otherType).size() == 1);
		assertTrue(index.getOccurrences(otherType).contains(occurrence));
		assertTrue(index.getOccurrences(type).isEmpty());
		assertTrue(index.getOccurrences(supertype).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getOccurrences(otherType).size() == 1);
		assertTrue(index.getOccurrences(otherType).contains(occurrence));
		assertTrue(index.getOccurrences(type).isEmpty());
		assertTrue(index.getOccurrences(supertype).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getOccurrences(otherType).size() == 1);
		assertTrue(index.getOccurrences(otherType).contains(occurrence));
		assertTrue(index.getOccurrences(type).size() == 1);
		assertTrue(index.getOccurrences(type).contains(occurrence));
		assertTrue(index.getOccurrences(supertype).size() == 1);
		assertTrue(index.getOccurrences(supertype).contains(occurrence));
		occurrence.setType(supertype);
		assertTrue(index.getOccurrences(type).isEmpty());
		assertTrue(index.getOccurrences(otherType).isEmpty());
		assertTrue(index.getOccurrences(supertype).size() == 1);
		assertTrue(index.getOccurrences(supertype).contains(occurrence));

		IOccurrence occurrence2 = (IOccurrence) createTopic().createOccurrence(type, "Occurrence 2", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getOccurrences(type).size() == 1);
		assertTrue(index.getOccurrences(type).contains(occurrence2));
		assertTrue(index.getOccurrences(supertype).size() == 1);
		assertTrue(index.getOccurrences(supertype).contains(occurrence));
		assertTrue(index.getOccurrences(type, supertype).size() == 2);
		assertTrue(index.getOccurrences(type, supertype).contains(occurrence2));
		assertTrue(index.getOccurrences(type, supertype).contains(occurrence));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getOccurrences(type, otherSuperType).size() == 2);
		assertTrue(index.getOccurrences(type, otherSuperType).contains(occurrence2));
		assertTrue(index.getOccurrences(type, otherSuperType).contains(occurrence));
		occurrence2.setType(supertype);
		assertTrue(index.getOccurrences(supertype).size() == 2);
		assertTrue(index.getOccurrences(supertype).contains(occurrence));
		assertTrue(index.getOccurrences(supertype).contains(occurrence2));
		type.addSupertype(supertype);
		occurrence2.setType(type);
		assertTrue(index.getOccurrences(supertype).size() == 2);
		assertTrue(index.getOccurrences(supertype).contains(occurrence2));
		assertTrue(index.getOccurrences(supertype).contains(occurrence));
		assertTrue(index.getOccurrences(otherSuperType).size() == 2);
		assertTrue(index.getOccurrences(otherSuperType).contains(occurrence2));
		assertTrue(index.getOccurrences(otherSuperType).contains(occurrence));

		index.close();
	}

	public void testGetOccurrencesTopicArray() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getOccurrences((Topic[]) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(type, "Occurrence", new Topic[0]);

		assertTrue(index.getOccurrences(new Topic[] { type }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { type }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getOccurrences(new Topic[] { otherType }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getOccurrences(new Topic[] { type }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { type }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { otherType }).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getOccurrences(new Topic[] { type }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { type }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { otherType }).isEmpty());

		occurrence.setType(otherType);
		assertTrue(index.getOccurrences(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { otherType }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { type }).isEmpty());
		assertTrue(index.getOccurrences(new Topic[] { supertype }).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getOccurrences(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { otherType }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { type }).isEmpty());
		assertTrue(index.getOccurrences(new Topic[] { supertype }).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getOccurrences(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { otherType }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { type }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { type }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).contains(occurrence));
		occurrence.setType(supertype);
		assertTrue(index.getOccurrences(new Topic[] { type }).isEmpty());
		assertTrue(index.getOccurrences(new Topic[] { otherType }).isEmpty());
		assertTrue(index.getOccurrences(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).contains(occurrence));

		IOccurrence occurrence2 = (IOccurrence) createTopic().createOccurrence(type, "Occurrence 2", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getOccurrences(new Topic[] { type }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { type }).contains(occurrence2));
		assertTrue(index.getOccurrences(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { type, supertype }).size() == 2);
		assertTrue(index.getOccurrences(new Topic[] { type, supertype }).contains(occurrence2));
		assertTrue(index.getOccurrences(new Topic[] { type, supertype }).contains(occurrence));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getOccurrences(new Topic[] { type, otherSuperType }).size() == 2);
		assertTrue(index.getOccurrences(new Topic[] { type, otherSuperType }).contains(occurrence2));
		assertTrue(index.getOccurrences(new Topic[] { type, otherSuperType }).contains(occurrence));
		occurrence2.setType(supertype);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { supertype }).contains(occurrence2));
		type.addSupertype(supertype);
		occurrence2.setType(type);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getOccurrences(new Topic[] { supertype }).contains(occurrence2));
		assertTrue(index.getOccurrences(new Topic[] { supertype }).contains(occurrence));
		assertTrue(index.getOccurrences(new Topic[] { otherSuperType }).size() == 2);
		assertTrue(index.getOccurrences(new Topic[] { otherSuperType }).contains(occurrence2));
		assertTrue(index.getOccurrences(new Topic[] { otherSuperType }).contains(occurrence));

		index.close();
	}

	public void testGetOccurrencesCollectionOfQextendsTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getOccurrences((Collection<Topic>) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		IOccurrence occurrence = (IOccurrence) createTopic().createOccurrence(type, "Occurrence", new Topic[0]);

		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).isEmpty());
		type.removeSupertype(supertype);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).isEmpty());

		occurrence.setType(otherType);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).isEmpty());
		type.addSupertype(supertype);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).isEmpty());
		otherType.addSupertype(type);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		occurrence.setType(supertype);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).isEmpty());
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherType })).isEmpty());
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).contains(occurrence));

		IOccurrence occurrence2 = (IOccurrence) createTopic().createOccurrence(type, "Occurrence 2", new Topic[0]);
		type.removeSupertype(supertype);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type })).contains(occurrence2));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type, supertype })).size() == 2);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type, supertype })).contains(occurrence2));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type, supertype })).contains(occurrence));
		ITopic otherSuperType = createTopic();
		supertype.addSupertype(otherSuperType);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type, otherSuperType })).size() == 2);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type, otherSuperType })).contains(occurrence2));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { type, otherSuperType })).contains(occurrence));
		occurrence2.setType(supertype);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).contains(occurrence2));
		type.addSupertype(supertype);
		occurrence2.setType(type);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).contains(occurrence2));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { supertype })).contains(occurrence));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherSuperType })).size() == 2);
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherSuperType })).contains(occurrence2));
		assertTrue(index.getOccurrences(Arrays.asList(new Topic[] { otherSuperType })).contains(occurrence));

		index.close();
	}

	public void testGetTopicsTopicArray() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getTopics((Topic[]) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		ITopic topic = createTopic();
		topic.addType(type);

		assertTrue(index.getTopics(new Topic[] { type }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getTopics(new Topic[] { otherType }).isEmpty());

		topic.addType(otherType);
		assertTrue(index.getTopics(new Topic[] { type }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }).isEmpty());
		assertTrue(index.getTopics(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { otherType }).contains(topic));
		type.addSupertype(supertype);
		assertTrue(index.getTopics(new Topic[] { type }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { supertype }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { otherType }).contains(topic));

		ITopic topic2 = createTopic();
		topic2.addType(type);
		assertTrue(index.getTopics(new Topic[] { type }).size() == 2);
		assertTrue(index.getTopics(new Topic[] { type }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type }).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { supertype }).size() == 2);
		assertTrue(index.getTopics(new Topic[] { supertype }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { otherType }).contains(topic));
		topic2.removeType(type);
		topic2.addType(supertype);
		type.removeSupertype(supertype);
		assertTrue(index.getTopics(new Topic[] { type }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }).contains(topic));
		assertFalse(index.getTopics(new Topic[] { type }).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { supertype }).size() == 1);
		assertFalse(index.getTopics(new Topic[] { supertype }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { otherType }).size() == 1);
		assertTrue(index.getTopics(new Topic[] { otherType }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type, supertype }).size() == 2);
		assertTrue(index.getTopics(new Topic[] { type, supertype }).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type, supertype }).contains(topic2));

		index.close();
	}

	public void testGetTopicsCollectionOfTopic() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getTopics((Collection<Topic>) null);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		ITopic topic = createTopic();
		topic.addType(type);

		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).isEmpty());

		topic.addType(otherType);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).isEmpty());
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).contains(topic));
		type.addSupertype(supertype);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).contains(topic));

		ITopic topic2 = createTopic();
		topic2.addType(type);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).size() == 2);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).size() == 2);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).contains(topic));
		topic2.removeType(type);
		topic2.addType(supertype);
		type.removeSupertype(supertype);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type })).contains(topic));
		assertFalse(index.getTopics(Arrays.asList(new Topic[] { type })).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).size() == 1);
		assertFalse(index.getTopics(Arrays.asList(new Topic[] { supertype })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype })).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type, supertype })).size() == 2);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type, supertype })).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type, supertype })).contains(topic2));

		index.close();
	}

	public void testGetTopicsCollectionOfQextendsTopicBoolean() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getTopics((Collection<Topic>) null, false);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		ITopic topic = createTopic();
		topic.addType(type);

		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).isEmpty());
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).isEmpty());

		topic.addType(otherType);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).isEmpty());
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).contains(topic));
		type.addSupertype(supertype);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).contains(topic));

		ITopic topic2 = createTopic();
		topic2.addType(type);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).size() == 2);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).size() == 2);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).contains(topic));
		topic2.removeType(type);
		topic2.addType(supertype);
		type.removeSupertype(supertype);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic));
		assertFalse(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), true).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), true).contains(topic));
		assertFalse(index.getTopics(Arrays.asList(new Topic[] { type }), true).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).size() == 1);
		assertFalse(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { supertype }), false).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).size() == 1);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { otherType }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type, supertype }), false).size() == 2);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type, supertype }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type, supertype }), false).contains(topic2));

		topic2.addType(type);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).size() == 2);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), false).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), true).size() == 2);
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), true).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type }), true).contains(topic2));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type, supertype }), true).size() == 1);
		assertFalse(index.getTopics(Arrays.asList(new Topic[] { type, supertype }), true).contains(topic));
		assertTrue(index.getTopics(Arrays.asList(new Topic[] { type, supertype }), true).contains(topic2));

		index.close();
	}

	public void testGetTopicsTopic() throws Exception {
		boolean hasFeatureISA = factory.getFeature(FeatureStrings.TOPIC_MAPS_TYPE_INSTANCE_ASSOCIATION);
		boolean hasFeatureAKO = factory.getFeature(FeatureStrings.TOPIC_MAPS_SUPERTYPE_SUBTYPE_ASSOCIATION);
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();		

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		ITopic topic = createTopic();
		topic.addType(type);

		assertTrue(index.getTopics(type).size() == 1);
		assertTrue(index.getTopics(type).contains(topic));
		assertTrue(index.getTopics(supertype).isEmpty());
		assertTrue(index.getTopics(otherType).isEmpty());
		
		int cnt = 3;
		if ( hasFeatureISA){
			cnt += 3;
		}
		assertEquals(cnt, index.getTopics((Topic)null).size() );
		assertTrue(index.getTopics((Topic)null).contains(type));
		assertTrue(index.getTopics((Topic)null).contains(supertype));
		assertTrue(index.getTopics((Topic)null).contains(otherType));

		topic.addType(otherType);
		assertEquals(cnt, index.getTopics((Topic)null).size() );
		assertTrue(index.getTopics(type).size() == 1);
		assertTrue(index.getTopics(type).contains(topic));
		assertTrue(index.getTopics(supertype).isEmpty());
		assertTrue(index.getTopics(otherType).size() == 1);
		assertTrue(index.getTopics(otherType).contains(topic));
		type.addSupertype(supertype);		
		if ( hasFeatureAKO){
			cnt += 3;
		}
		assertEquals(cnt, index.getTopics((Topic)null).size() );
		assertTrue(index.getTopics(type).size() == 1);
		assertTrue(index.getTopics(type).contains(topic));
		assertTrue(index.getTopics(supertype).size() == 1);
		assertTrue(index.getTopics(supertype).contains(topic));
		assertTrue(index.getTopics(otherType).size() == 1);
		assertTrue(index.getTopics(otherType).contains(topic));

		ITopic topic2 = createTopic();
		topic2.addType(type);
		assertTrue(index.getTopics(type).size() == 2);
		assertTrue(index.getTopics(type).contains(topic));
		assertTrue(index.getTopics(type).contains(topic2));
		assertTrue(index.getTopics(supertype).size() == 2);
		assertTrue(index.getTopics(supertype).contains(topic));
		assertTrue(index.getTopics(supertype).contains(topic2));
		assertTrue(index.getTopics(otherType).size() == 1);
		assertTrue(index.getTopics(otherType).contains(topic));
		topic2.removeType(type);
		cnt++;
		assertEquals(cnt, index.getTopics((Topic)null).size() );
		topic2.addType(supertype);
		cnt--;
		assertEquals(cnt, index.getTopics((Topic)null).size() );
		type.removeSupertype(supertype);
		assertEquals(cnt, index.getTopics((Topic)null).size() );
		assertTrue(index.getTopics(type).size() == 1);
		assertTrue(index.getTopics(type).contains(topic));
		assertFalse(index.getTopics(type).contains(topic2));
		assertTrue(index.getTopics(supertype).size() == 1);
		assertFalse(index.getTopics(supertype).contains(topic));
		assertTrue(index.getTopics(supertype).contains(topic2));
		assertTrue(index.getTopics(otherType).size() == 1);
		assertTrue(index.getTopics(otherType).contains(topic));
		
		topic.removeType(type);
		assertEquals(cnt, index.getTopics((Topic)null).size() );
		topic.removeType(otherType);
		cnt++;
		assertEquals(cnt, index.getTopics((Topic)null).size() );
		assertTrue(index.getTopics((Topic)null).contains(type));
		assertTrue(index.getTopics((Topic)null).contains(supertype));
		assertTrue(index.getTopics((Topic)null).contains(otherType));
		assertTrue(index.getTopics((Topic)null).contains(topic));
		
		index.close();
	}

	public void testGetTopicsTopicArrayBoolean() {
		ITransitiveTypeInstanceIndex index = topicMap.getIndex(ITransitiveTypeInstanceIndex.class);
		Assert.assertNotNull(index);
		index.open();
		try {
			index.getTopics((Topic[]) null, false);
			fail("Type cannot be null!");
		} catch (IllegalArgumentException e) {
			// NOTHING TO Do
		}

		ITopic type = createTopic();
		ITopic supertype = createTopic();
		ITopic otherType = createTopic();
		ITopic topic = createTopic();
		topic.addType(type);

		assertTrue(index.getTopics(new Topic[] { type }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }, false).isEmpty());
		assertTrue(index.getTopics(new Topic[] { otherType }, false).isEmpty());

		topic.addType(otherType);
		assertTrue(index.getTopics(new Topic[] { type }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }, false).isEmpty());
		assertTrue(index.getTopics(new Topic[] { otherType }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { otherType }, false).contains(topic));
		type.addSupertype(supertype);
		assertTrue(index.getTopics(new Topic[] { type }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { supertype }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { otherType }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { otherType }, false).contains(topic));

		ITopic topic2 = createTopic();
		topic2.addType(type);
		assertTrue(index.getTopics(new Topic[] { type }, false).size() == 2);
		assertTrue(index.getTopics(new Topic[] { type }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type }, false).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { supertype }, false).size() == 2);
		assertTrue(index.getTopics(new Topic[] { supertype }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }, false).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { otherType }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { otherType }, false).contains(topic));
		topic2.removeType(type);
		topic2.addType(supertype);
		type.removeSupertype(supertype);
		assertTrue(index.getTopics(new Topic[] { type }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }, false).contains(topic));
		assertFalse(index.getTopics(new Topic[] { type }, false).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { type }, true).size() == 1);
		assertTrue(index.getTopics(new Topic[] { type }, true).contains(topic));
		assertFalse(index.getTopics(new Topic[] { type }, true).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { supertype }, false).size() == 1);
		assertFalse(index.getTopics(new Topic[] { supertype }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { supertype }, false).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { otherType }, false).size() == 1);
		assertTrue(index.getTopics(new Topic[] { otherType }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type, supertype }, false).size() == 2);
		assertTrue(index.getTopics(new Topic[] { type, supertype }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type, supertype }, false).contains(topic2));

		topic2.addType(type);
		assertTrue(index.getTopics(new Topic[] { type }, false).size() == 2);
		assertTrue(index.getTopics(new Topic[] { type }, false).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type }, false).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { type }, true).size() == 2);
		assertTrue(index.getTopics(new Topic[] { type }, true).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type }, true).contains(topic2));
		assertTrue(index.getTopics(new Topic[] { type, supertype }, true).size() == 1);
		assertFalse(index.getTopics(new Topic[] { type, supertype }, true).contains(topic));
		assertTrue(index.getTopics(new Topic[] { type, supertype }, true).contains(topic2));

		index.close();
	}

}
