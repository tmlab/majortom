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
package de.topicmapslab.majortom.tests.canonical;

import java.util.UUID;

import org.tmapi.core.Association;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestCanonicals extends MaJorToMTestCase {

	public void testNameDuplicates() {
		if (topicMap.getStore().isRevisionManagementSupported()) {
			topicMap.getStore().enableRevisionManagement(false);
		}
		ITopic type = createTopic();
		ITopic[] topics = new ITopic[20];
		for (int i = 0; i < topics.length; i++) {
			topics[i] = createTopic();
			for (int j = 0; j < 10; j++) {
				Name n = topics[i].createName(type, "Value", new Topic[0]);
				n.addItemIdentifier(topicMap.createLocator("http://psi.example.org/" + UUID.randomUUID().toString()));
				n.createVariant("Variant", createTopic());
				ITopic reifier = createTopic();
				n.setReifier(reifier);
				assertEquals(1, n.getItemIdentifiers().size());
				assertEquals(1, n.getVariants().size());
				assertEquals(reifier, n.getReifier());
			}
			assertEquals(10, topics[i].getNames().size());
		}
		assertEquals(topics.length * 21 + 1, topicMap.getTopics().size());
		/*
		 * remove duplicates
		 */
		System.out.print("Remove duplicates ... ");
		long t = System.currentTimeMillis();
		topicMap.removeDuplicates();
		System.out.println(" finised after " + (System.currentTimeMillis() - t) + "ms.");
		assertEquals("reifier should be merged", topics.length * 12 + 1, topicMap.getTopics().size());
		for (int i = 0; i < topics.length; i++) {
			assertEquals("Number of names should be 1", 1, topics[i].getNames().size());
			Name n = topics[i].getNames().iterator().next();
			assertEquals("Name should have all item-identifiers!", 10, n.getItemIdentifiers().size());
			assertEquals("Name should have all variants!", 10, n.getVariants().size());
		}
	}

	public void testVariantDuplicates() {
		if (topicMap.getStore().isRevisionManagementSupported()) {
			topicMap.getStore().enableRevisionManagement(false);
		}
		ITopic type = createTopic();
		ITopic parent = createTopic();
		ITopic theme = createTopic();
		Name[] names = new Name[20];
		for (int i = 0; i < names.length; i++) {
			names[i] = parent.createName(type, "Value", new Topic[0]);
			for (int j = 0; j < 10; j++) {
				Variant v = names[i].createVariant("Value", theme);
				v.addItemIdentifier(topicMap.createLocator("http://psi.example.org/" + UUID.randomUUID().toString()));
				if (j % 10 != 0) {
					ITopic reifier = createTopic();
					v.setReifier(reifier);
					assertEquals(reifier, v.getReifier());
				}
				assertEquals(1, v.getItemIdentifiers().size());
			}
			assertEquals(10, names[i].getVariants().size());
		}
		assertEquals(names.length, parent.getNames().size());
		assertEquals(names.length * 9 + 3, topicMap.getTopics().size());
		/*
		 * remove duplicates
		 */
		System.out.print("Remove duplicates ... ");
		long t = System.currentTimeMillis();
		topicMap.removeDuplicates();
		System.out.println(" finised after " + (System.currentTimeMillis() - t) + "ms.");
		assertEquals("Number of names should be 1", 1, parent.getNames().size());
		Name n = parent.getNames().iterator().next();
		assertEquals("Name should have one variants!", 1, n.getVariants().size());
		assertEquals("reifier should be merged", 4, topicMap.getTopics().size());
		Variant v = n.getVariants().iterator().next();
		assertEquals("Variant should have all item-identifiers!", names.length * 10, v.getItemIdentifiers().size());
	}

	public void testOccurrenceDuplicates() {

		ITopic type = createTopic();
		ITopic[] topics = new ITopic[20];
		for (int i = 0; i < topics.length; i++) {
			topics[i] = createTopic();
			for (int j = 0; j < 10; j++) {
				Occurrence o = topics[i].createOccurrence(type, "Value", new Topic[0]);
				o.addItemIdentifier(topicMap.createLocator("http://psi.example.org/" + UUID.randomUUID().toString()));
				ITopic reifier = createTopic();
				o.setReifier(reifier);
				assertEquals(1, o.getItemIdentifiers().size());
				assertEquals(reifier, o.getReifier());
			}
			assertEquals(10, topics[i].getOccurrences().size());
		}
		assertEquals(topics.length * 11 + 1, topicMap.getTopics().size());
		/*
		 * remove duplicates
		 */
		System.out.print("Remove duplicates ... ");
		long t = System.currentTimeMillis();
		topicMap.removeDuplicates();
		System.out.println(" finised after " + (System.currentTimeMillis() - t) + "ms.");
		assertEquals("reifier should be merged", topics.length * 2 + 1, topicMap.getTopics().size());
		for (int i = 0; i < topics.length; i++) {
			assertEquals("Number of occurrences should be 1", 1, topics[i].getOccurrences().size());
			Occurrence o = topics[i].getOccurrences().iterator().next();
			assertEquals("Occurrence should have all item-identifiers!", 10, o.getItemIdentifiers().size());
		}
	}

	public void testAssociationDuplicates() {
		ITopic type = createTopic();
		ITopic player = createTopic();
		ITopic t = createTopic();
		ITopic t2 = createTopic();
		ITopic t3 = createTopic();
		ITopic t4 = createTopic();
		IAssociation a1 = createAssociation(t);
		a1.createRole(t2, t3);
		IAssociation a3 = createAssociation(t);
		a3.createRole(t2, t3);
		a3.addTheme(t4);
		IAssociation a2 = createAssociation(createTopic());
		a2.createRole(createTopic(), createTopic());
		int size = topicMap.getTopics().size();

		IAssociation[] associations = new IAssociation[20];
		for (int i = 0; i < associations.length; i++) {
			associations[i] = createAssociation(type);
			for (int j = 0; j < 10; j++) {
				Role r = associations[i].createRole(type, player);
				r.addItemIdentifier(topicMap.createLocator("http://psi.example.org/" + UUID.randomUUID().toString()));
				ITopic reifier = createTopic();
				r.setReifier(reifier);
				assertEquals(1, r.getItemIdentifiers().size());
				assertEquals(reifier, r.getReifier());
			}
			assertEquals(10, associations[i].getRoles().size());
		}
		assertEquals(associations.length * 10 + size, topicMap.getTopics().size());
		assertEquals(associations.length + 3, topicMap.getAssociations().size());
		/*
		 * remove duplicates
		 */
		System.out.print("Remove duplicates ... ");
		long time = System.currentTimeMillis();
		topicMap.removeDuplicates();
		System.out.println(" finised after " + (System.currentTimeMillis() - time) + "ms.");
		assertEquals(4, topicMap.getAssociations().size());		
		for (Association a : topicMap.getAssociations()) {
			if (a.equals(a1) || a.equals(a2) || a.equals(a3)) {
				continue;
			}
			assertEquals("Number of roles should be 1", 1, a.getRoles().size());
			Role r = a.getRoles().iterator().next();
			assertEquals("Role should have all item-identifiers!", associations.length * 10, r.getItemIdentifiers().size());
		}
		assertEquals("reifier should be merged", size + 1, topicMap.getTopics().size());
	}

	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception {
		super.setUp();
		if (topicMap.getStore().isRevisionManagementSupported()) {
			topicMap.getStore().enableRevisionManagement(false);
		}
	}

}
