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

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IAssociationRole;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.IRevisionIndex;
import de.topicmapslab.majortom.model.revision.IRevision;
import de.topicmapslab.majortom.revision.core.ReadOnlyAssociationRole;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestRoleRevisions extends MaJorToMTestCase {

	public void testRevision() throws Exception {
		ITopic topic = createTopic();
		ITopic type = createTopic();
		ITopic reifier = createTopic();
		IAssociation association = createAssociation(type);
		IAssociationRole role = (IAssociationRole) association.createRole(type, topic);
		role.setReifier(reifier);

		role.remove();

		IRevisionIndex index = topicMap.getIndex(IRevisionIndex.class);
		index.open();

		IRevision revision = index.getLastRevision();
		assertEquals(2, revision.getChangeset().size());
		assertEquals(role, revision.getChangeset().get(1).getOldValue());
		assertTrue(revision.getChangeset().get(1).getOldValue() instanceof ReadOnlyAssociationRole);

		role = (IAssociationRole) revision.getChangeset().get(1).getOldValue();

		assertEquals(association, role.getParent());
		assertEquals(type, role.getType());
		assertEquals(topic, role.getPlayer());
		assertEquals(reifier, role.getReifier());
	}

}
