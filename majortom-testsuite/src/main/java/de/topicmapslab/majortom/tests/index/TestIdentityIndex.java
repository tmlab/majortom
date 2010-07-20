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

import java.util.regex.Pattern;

import org.tmapi.core.Association;
import org.tmapi.core.Locator;
import org.tmapi.core.Name;
import org.tmapi.core.Occurrence;
import org.tmapi.core.Role;
import org.tmapi.core.TMAPIRuntimeException;
import org.tmapi.core.Topic;
import org.tmapi.core.Variant;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.IIdentityIndex;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 *
 */
public class TestIdentityIndex extends MaJorToMTestCase {

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#existsIdentifier(java.lang.String)}.
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#existsIdentifier(org.tmapi.core.Locator)}.
	 */
	public void testExistsIdentifier() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsIdentifier((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String si_ = "http://www.example.org/si";
		Locator si = topicMap.createLocator(si_);
		final String sl_ = "http://www.example.org/sl";
		Locator sl = topicMap.createLocator(sl_);
		final String ii_ = "http://www.example.org/ii";
		Locator ii = topicMap.createLocator(ii_);
				
		assertFalse(index.existsIdentifier(si_));
		assertFalse(index.existsIdentifier(si));
		assertFalse(index.existsIdentifier(sl_));
		assertFalse(index.existsIdentifier(sl));
		assertFalse(index.existsIdentifier(ii_));
		assertFalse(index.existsIdentifier(ii));
		
		Topic topic = createTopicBySI(si_);
		assertTrue(index.existsIdentifier(si_));
		assertTrue(index.existsIdentifier(si));
		assertFalse(index.existsIdentifier(sl_));
		assertFalse(index.existsIdentifier(sl));
		assertFalse(index.existsIdentifier(ii_));
		assertFalse(index.existsIdentifier(ii));
		
		topic.addSubjectLocator(sl);
		assertTrue(index.existsIdentifier(si_));
		assertTrue(index.existsIdentifier(si));
		assertTrue(index.existsIdentifier(sl_));
		assertTrue(index.existsIdentifier(sl));
		assertFalse(index.existsIdentifier(ii_));
		assertFalse(index.existsIdentifier(ii));
		
		topic.addItemIdentifier(ii);
		assertTrue(index.existsIdentifier(si_));
		assertTrue(index.existsIdentifier(si));
		assertTrue(index.existsIdentifier(sl_));
		assertTrue(index.existsIdentifier(sl));
		assertTrue(index.existsIdentifier(ii_));
		assertTrue(index.existsIdentifier(ii));
		
		topic.removeSubjectIdentifier(si);
		assertFalse(index.existsIdentifier(si_));
		assertFalse(index.existsIdentifier(si));
		assertTrue(index.existsIdentifier(sl_));
		assertTrue(index.existsIdentifier(sl));
		assertTrue(index.existsIdentifier(ii_));
		assertTrue(index.existsIdentifier(ii));
		
		topic.removeSubjectLocator(sl);
		assertFalse(index.existsIdentifier(si_));
		assertFalse(index.existsIdentifier(si));
		assertFalse(index.existsIdentifier(sl_));
		assertFalse(index.existsIdentifier(sl));
		assertTrue(index.existsIdentifier(ii_));
		assertTrue(index.existsIdentifier(ii));
		
		topic.removeItemIdentifier(ii);
		assertFalse(index.existsIdentifier(si_));
		assertFalse(index.existsIdentifier(si));
		assertFalse(index.existsIdentifier(sl_));
		assertFalse(index.existsIdentifier(sl));
		assertFalse(index.existsIdentifier(ii_));
		assertFalse(index.existsIdentifier(ii));
		
		index.close();
		try{
			index.existsIdentifier((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			//NOTHING TO DO
		}
	}
	
	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#existsItemIdentifier(java.lang.String)}.
	 */
	public void testExistsItemIdentifierString() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsItemIdentifier((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String loc1_ = "http://www.example.org/1";
		Locator loc1 = topicMap.createLocator(loc1_);
		final String loc2_ = "http://www.example.org/2";
		Locator loc2 = topicMap.createLocator(loc2_);
		final String loc3_ = "http://www.example.org/3";
		Locator loc3 = topicMap.createLocator(loc3_);
				
		assertFalse(index.existsItemIdentifier(loc1_));
		assertFalse(index.existsItemIdentifier(loc1));
		assertFalse(index.existsItemIdentifier(loc2_));
		assertFalse(index.existsItemIdentifier(loc2));
		assertFalse(index.existsItemIdentifier(loc3_));
		assertFalse(index.existsItemIdentifier(loc3));
		
		Topic topic = createTopicBySI(loc1_);
		assertFalse(index.existsItemIdentifier(loc1_));
		assertFalse(index.existsItemIdentifier(loc1));
		assertFalse(index.existsItemIdentifier(loc2_));
		assertFalse(index.existsItemIdentifier(loc2));
		assertFalse(index.existsItemIdentifier(loc3_));
		assertFalse(index.existsItemIdentifier(loc3));
		
		topic.addSubjectLocator(loc2);
		assertFalse(index.existsItemIdentifier(loc1_));
		assertFalse(index.existsItemIdentifier(loc1));
		assertFalse(index.existsItemIdentifier(loc2_));
		assertFalse(index.existsItemIdentifier(loc2));
		assertFalse(index.existsItemIdentifier(loc3_));
		assertFalse(index.existsItemIdentifier(loc3));
		
		topic.addItemIdentifier(loc3);
		assertFalse(index.existsItemIdentifier(loc1_));
		assertFalse(index.existsItemIdentifier(loc1));
		assertFalse(index.existsItemIdentifier(loc2_));
		assertFalse(index.existsItemIdentifier(loc2));
		assertTrue(index.existsItemIdentifier(loc3_));
		assertTrue(index.existsItemIdentifier(loc3));
		
		topic.removeSubjectIdentifier(loc1);
		assertFalse(index.existsItemIdentifier(loc1_));
		assertFalse(index.existsItemIdentifier(loc1));
		assertFalse(index.existsItemIdentifier(loc2_));
		assertFalse(index.existsItemIdentifier(loc2));
		assertTrue(index.existsItemIdentifier(loc3_));
		assertTrue(index.existsItemIdentifier(loc3));
		
		topic.removeSubjectLocator(loc2);
		assertFalse(index.existsItemIdentifier(loc1_));
		assertFalse(index.existsItemIdentifier(loc1));
		assertFalse(index.existsItemIdentifier(loc2_));
		assertFalse(index.existsItemIdentifier(loc2));
		assertTrue(index.existsItemIdentifier(loc3_));
		assertTrue(index.existsItemIdentifier(loc3));
		
		topic.removeItemIdentifier(loc3);
		assertFalse(index.existsItemIdentifier(loc1_));
		assertFalse(index.existsItemIdentifier(loc1));
		assertFalse(index.existsItemIdentifier(loc2_));
		assertFalse(index.existsItemIdentifier(loc2));
		assertFalse(index.existsItemIdentifier(loc3_));
		assertFalse(index.existsItemIdentifier(loc3));
		
		index.close();
		try{
			index.existsItemIdentifier((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			//NOTHING TO DO
		}
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#existsSubjectIdentifier(java.lang.String)}.
	 */
	public void testExistsSubjectIdentifierString() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectIdentifier((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String loc1_ = "http://www.example.org/1";
		Locator loc1 = topicMap.createLocator(loc1_);
		final String loc2_ = "http://www.example.org/2";
		Locator loc2 = topicMap.createLocator(loc2_);
		final String loc3_ = "http://www.example.org/3";
		Locator loc3 = topicMap.createLocator(loc3_);
				
		assertFalse(index.existsSubjectIdentifier(loc1_));
		assertFalse(index.existsSubjectIdentifier(loc1));
		assertFalse(index.existsSubjectIdentifier(loc2_));
		assertFalse(index.existsSubjectIdentifier(loc2));
		assertFalse(index.existsSubjectIdentifier(loc3_));
		assertFalse(index.existsSubjectIdentifier(loc3));
		
		Topic topic = createTopicBySI(loc1_);
		assertTrue(index.existsSubjectIdentifier(loc1_));
		assertTrue(index.existsSubjectIdentifier(loc1));
		assertFalse(index.existsSubjectIdentifier(loc2_));
		assertFalse(index.existsSubjectIdentifier(loc2));
		assertFalse(index.existsSubjectIdentifier(loc3_));
		assertFalse(index.existsSubjectIdentifier(loc3));
		
		topic.addSubjectLocator(loc2);
		assertTrue(index.existsSubjectIdentifier(loc1_));
		assertTrue(index.existsSubjectIdentifier(loc1));
		assertFalse(index.existsSubjectIdentifier(loc2_));
		assertFalse(index.existsSubjectIdentifier(loc2));
		assertFalse(index.existsSubjectIdentifier(loc3_));
		assertFalse(index.existsSubjectIdentifier(loc3));
		
		topic.addItemIdentifier(loc3);
		assertTrue(index.existsSubjectIdentifier(loc1_));
		assertTrue(index.existsSubjectIdentifier(loc1));
		assertFalse(index.existsSubjectIdentifier(loc2_));
		assertFalse(index.existsSubjectIdentifier(loc2));
		assertFalse(index.existsSubjectIdentifier(loc3_));
		assertFalse(index.existsSubjectIdentifier(loc3));
		
		topic.removeSubjectIdentifier(loc1);
		assertFalse(index.existsSubjectIdentifier(loc1_));
		assertFalse(index.existsSubjectIdentifier(loc1));
		assertFalse(index.existsSubjectIdentifier(loc2_));
		assertFalse(index.existsSubjectIdentifier(loc2));
		assertFalse(index.existsSubjectIdentifier(loc3_));
		assertFalse(index.existsSubjectIdentifier(loc3));
		
		topic.removeSubjectLocator(loc2);
		assertFalse(index.existsSubjectIdentifier(loc1_));
		assertFalse(index.existsSubjectIdentifier(loc1));
		assertFalse(index.existsSubjectIdentifier(loc2_));
		assertFalse(index.existsSubjectIdentifier(loc2));
		assertFalse(index.existsSubjectIdentifier(loc3_));
		assertFalse(index.existsSubjectIdentifier(loc3));
		
		topic.removeItemIdentifier(loc3);
		assertFalse(index.existsSubjectIdentifier(loc1_));
		assertFalse(index.existsSubjectIdentifier(loc1));
		assertFalse(index.existsSubjectIdentifier(loc2_));
		assertFalse(index.existsSubjectIdentifier(loc2));
		assertFalse(index.existsSubjectIdentifier(loc3_));
		assertFalse(index.existsSubjectIdentifier(loc3));
		
		index.close();
		try{
			index.existsSubjectIdentifier((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			//NOTHING TO DO
		}
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#existsSubjectLocator(java.lang.String)}.
	 */
	public void testExistsSubjectLocatorString() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String loc1_ = "http://www.example.org/1";
		Locator loc1 = topicMap.createLocator(loc1_);
		final String loc2_ = "http://www.example.org/2";
		Locator loc2 = topicMap.createLocator(loc2_);
		final String loc3_ = "http://www.example.org/3";
		Locator loc3 = topicMap.createLocator(loc3_);
				
		assertFalse(index.existsSubjectLocator(loc1_));
		assertFalse(index.existsSubjectLocator(loc1));
		assertFalse(index.existsSubjectLocator(loc2_));
		assertFalse(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		Topic topic = createTopicBySI(loc1_);
		assertFalse(index.existsSubjectLocator(loc1_));
		assertFalse(index.existsSubjectLocator(loc1));
		assertFalse(index.existsSubjectLocator(loc2_));
		assertFalse(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		topic.addSubjectLocator(loc2);
		assertFalse(index.existsSubjectLocator(loc1_));
		assertFalse(index.existsSubjectLocator(loc1));
		assertTrue(index.existsSubjectLocator(loc2_));
		assertTrue(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		topic.addItemIdentifier(loc3);
		assertFalse(index.existsSubjectLocator(loc1_));
		assertFalse(index.existsSubjectLocator(loc1));
		assertTrue(index.existsSubjectLocator(loc2_));
		assertTrue(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		topic.removeSubjectIdentifier(loc1);
		assertFalse(index.existsSubjectLocator(loc1_));
		assertFalse(index.existsSubjectLocator(loc1));
		assertTrue(index.existsSubjectLocator(loc2_));
		assertTrue(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		topic.addSubjectLocator(loc1);
		assertTrue(index.existsSubjectLocator(loc1_));
		assertTrue(index.existsSubjectLocator(loc1));
		assertTrue(index.existsSubjectLocator(loc2_));
		assertTrue(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		topic.removeSubjectLocator(loc2);
		assertTrue(index.existsSubjectLocator(loc1_));
		assertTrue(index.existsSubjectLocator(loc1));
		assertFalse(index.existsSubjectLocator(loc2_));
		assertFalse(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		topic.removeItemIdentifier(loc3);
		assertTrue(index.existsSubjectLocator(loc1_));
		assertTrue(index.existsSubjectLocator(loc1));
		assertFalse(index.existsSubjectLocator(loc2_));
		assertFalse(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		topic.removeSubjectLocator(loc1);
		assertFalse(index.existsSubjectLocator(loc1_));
		assertFalse(index.existsSubjectLocator(loc1));
		assertFalse(index.existsSubjectLocator(loc2_));
		assertFalse(index.existsSubjectLocator(loc2));
		assertFalse(index.existsSubjectLocator(loc3_));
		assertFalse(index.existsSubjectLocator(loc3));
		
		index.close();
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			//NOTHING TO DO
		}
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getConstructByItemIdentifier(java.lang.String)}.
	 */
	public void testGetConstructByItemIdentifierString() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String loc1_ = "http://www.example.org/1";
		Locator loc1 = topicMap.createLocator(loc1_);
		final String loc2_ = "http://www.example.org/2";
		Locator loc2 = topicMap.createLocator(loc2_);
		final String loc3_ = "http://www.example.org/3";
		Locator loc3 = topicMap.createLocator(loc3_);
				
		Topic topic = createTopic();
		assertNull(index.getConstructByItemIdentifier(loc1));
		assertNull(index.getConstructByItemIdentifier(loc2));
		assertNull(index.getConstructByItemIdentifier(loc3));
		
		topic.addItemIdentifier(loc1);
		assertNotNull(index.getConstructByItemIdentifier(loc1));
		assertEquals(topic,index.getConstructByItemIdentifier(loc1));
		assertNull(index.getConstructByItemIdentifier(loc2));
		assertNull(index.getConstructByItemIdentifier(loc3));
		
		topic.addItemIdentifier(loc2);
		assertNotNull(index.getConstructByItemIdentifier(loc1));
		assertEquals(topic,index.getConstructByItemIdentifier(loc1));
		assertNotNull(index.getConstructByItemIdentifier(loc2));
		assertEquals(topic,index.getConstructByItemIdentifier(loc2));
		assertNull(index.getConstructByItemIdentifier(loc3));
		
		topic.removeItemIdentifier(loc1);
		assertNull(index.getConstructByItemIdentifier(loc1));
		assertNotNull(index.getConstructByItemIdentifier(loc2));
		assertEquals(topic,index.getConstructByItemIdentifier(loc2));
		assertNull(index.getConstructByItemIdentifier(loc3));
		
		Name n = topic.createName("Name", new Topic[0]);
		assertNull(index.getConstructByItemIdentifier(loc1));
		assertNotNull(index.getConstructByItemIdentifier(loc2));
		assertEquals(topic,index.getConstructByItemIdentifier(loc2));
		assertNull(index.getConstructByItemIdentifier(loc3));
		
		n.addItemIdentifier(loc3);
		assertNull(index.getConstructByItemIdentifier(loc1));
		assertNotNull(index.getConstructByItemIdentifier(loc2));
		assertEquals(topic,index.getConstructByItemIdentifier(loc2));
		assertNotNull(index.getConstructByItemIdentifier(loc3));
		assertEquals(n,index.getConstructByItemIdentifier(loc3));
		
		Association a = createAssociation(topic);
		assertNull(index.getConstructByItemIdentifier(loc1));
		assertNotNull(index.getConstructByItemIdentifier(loc2));
		assertEquals(topic,index.getConstructByItemIdentifier(loc2));
		assertNotNull(index.getConstructByItemIdentifier(loc3));
		assertEquals(n,index.getConstructByItemIdentifier(loc3));
		
		a.addItemIdentifier(loc1);
		assertNotNull(index.getConstructByItemIdentifier(loc1));
		assertEquals(a,index.getConstructByItemIdentifier(loc1));
		assertNotNull(index.getConstructByItemIdentifier(loc2));
		assertEquals(topic,index.getConstructByItemIdentifier(loc2));
		assertNotNull(index.getConstructByItemIdentifier(loc3));
		assertEquals(n,index.getConstructByItemIdentifier(loc3));
		
		topic.removeItemIdentifier(loc2);
		assertNotNull(index.getConstructByItemIdentifier(loc1));
		assertEquals(a,index.getConstructByItemIdentifier(loc1));
		assertNull(index.getConstructByItemIdentifier(loc2));		
		assertNotNull(index.getConstructByItemIdentifier(loc3));
		assertEquals(n,index.getConstructByItemIdentifier(loc3));
		
		index.close();
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			//NOTHING TO DO
		}
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getConstructsByIdentifier(java.util.regex.Pattern)}.
	 */
	public void testGetConstructsByIdentifierPattern() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String loc1_ = "http://www.example.org/1";
		Locator loc1 = topicMap.createLocator(loc1_);
		final String loc2_ = "http://www.example.org/2";
		Locator loc2 = topicMap.createLocator(loc2_);
		final String loc3_ = "http://www.example.org/3";
		Locator loc3 = topicMap.createLocator(loc3_);
				
		Topic topic = createTopic();
		assertTrue(index.getConstructsByIdentifier(loc1.getReference()).isEmpty());
		assertTrue(index.getConstructsByIdentifier(loc2.getReference()).isEmpty());
		assertTrue(index.getConstructsByIdentifier(loc3.getReference()).isEmpty());
		
		topic.addSubjectIdentifier(loc1);
		assertTrue(index.getConstructsByIdentifier(loc1.getReference()).contains(topic));
		assertTrue(index.getConstructsByIdentifier(loc2.getReference()).isEmpty());
		assertTrue(index.getConstructsByIdentifier(loc3.getReference()).isEmpty());
		
		topic.addItemIdentifier(loc2);
		assertTrue(index.getConstructsByIdentifier(loc1.getReference()).contains(topic));
		assertTrue(index.getConstructsByIdentifier(loc2.getReference()).contains(topic));
		assertTrue(index.getConstructsByIdentifier(loc3.getReference()).isEmpty());
		assertTrue(index.getConstructsByIdentifier("http://www.example.org/.*").contains(topic));
		
		ITopic otherTopic = (ITopic)topicMap.createTopicBySubjectLocator(loc3);
		assertTrue(index.getConstructsByIdentifier(loc1.getReference()).contains(topic));
		assertTrue(index.getConstructsByIdentifier(loc2.getReference()).contains(topic));
		assertTrue(index.getConstructsByIdentifier(loc3.getReference()).contains(otherTopic));
		assertTrue(index.getConstructsByIdentifier("http://www.example.org/.*").contains(topic));
		assertTrue(index.getConstructsByIdentifier("http://www.example.org/.*").contains(otherTopic));
		
		topic.removeItemIdentifier(loc2);
		assertTrue(index.getConstructsByIdentifier(loc1.getReference()).contains(topic));
		assertTrue(index.getConstructsByIdentifier(loc2.getReference()).isEmpty());
		assertTrue(index.getConstructsByIdentifier(loc3.getReference()).contains(otherTopic));
		assertTrue(index.getConstructsByIdentifier("http://www.example.org/.*").contains(topic));
		assertTrue(index.getConstructsByIdentifier("http://www.example.org/.*").contains(otherTopic));
		
		topic.removeSubjectIdentifier(loc1);
		assertTrue(index.getConstructsByIdentifier(loc1.getReference()).isEmpty());
		assertTrue(index.getConstructsByIdentifier(loc2.getReference()).isEmpty());
		assertTrue(index.getConstructsByIdentifier(loc3.getReference()).contains(otherTopic));
		assertFalse(index.getConstructsByIdentifier("http://www.example.org/.*").contains(topic));
		assertTrue(index.getConstructsByIdentifier("http://www.example.org/.*").contains(otherTopic));
				
		index.close();
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			//NOTHING TO DO
		}
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getConstructsByItemIdentifier(java.util.regex.Pattern)}.
	 */
	public void testGetConstructsByItemIdentifierPattern() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String loc1_ = "http://www.example.org/1";
		Locator loc1 = topicMap.createLocator(loc1_);
		final String loc2_ = "http://www.example.org/2";
		Locator loc2 = topicMap.createLocator(loc2_);
		final String loc3_ = "http://www.example.org/3";
		Locator loc3 = topicMap.createLocator(loc3_);
				
		Topic topic = createTopic();
		assertTrue(index.getConstructsByItemIdentifier(loc1.getReference()).isEmpty());
		assertTrue(index.getConstructsByItemIdentifier(loc2.getReference()).isEmpty());
		assertTrue(index.getConstructsByItemIdentifier(loc3.getReference()).isEmpty());
		
		topic.addItemIdentifier(loc1);
		assertTrue(index.getConstructsByItemIdentifier(loc1.getReference()).contains(topic));
		assertTrue(index.getConstructsByItemIdentifier(loc2.getReference()).isEmpty());
		assertTrue(index.getConstructsByItemIdentifier(loc3.getReference()).isEmpty());
		
		topic.addItemIdentifier(loc2);
		assertTrue(index.getConstructsByItemIdentifier(loc1.getReference()).contains(topic));
		assertTrue(index.getConstructsByItemIdentifier(loc2.getReference()).contains(topic));
		assertTrue(index.getConstructsByItemIdentifier(loc3.getReference()).isEmpty());
		assertTrue(index.getConstructsByItemIdentifier("http://www.example.org/.*").contains(topic));
		
		ITopic otherTopic = (ITopic)topicMap.createTopicByItemIdentifier(loc3);
		assertTrue(index.getConstructsByItemIdentifier(loc1.getReference()).contains(topic));
		assertTrue(index.getConstructsByItemIdentifier(loc2.getReference()).contains(topic));
		assertTrue(index.getConstructsByItemIdentifier(loc3.getReference()).contains(otherTopic));
		assertTrue(index.getConstructsByItemIdentifier("http://www.example.org/.*").contains(topic));
		assertTrue(index.getConstructsByItemIdentifier("http://www.example.org/.*").contains(otherTopic));
		
		topic.removeItemIdentifier(loc2);
		assertTrue(index.getConstructsByItemIdentifier(loc1.getReference()).contains(topic));
		assertTrue(index.getConstructsByItemIdentifier(loc2.getReference()).isEmpty());
		assertTrue(index.getConstructsByItemIdentifier(loc3.getReference()).contains(otherTopic));
		assertTrue(index.getConstructsByItemIdentifier("http://www.example.org/.*").contains(topic));
		assertTrue(index.getConstructsByItemIdentifier("http://www.example.org/.*").contains(otherTopic));
		
		topic.removeItemIdentifier(loc1);
		assertTrue(index.getConstructsByItemIdentifier(loc1.getReference()).isEmpty());
		assertTrue(index.getConstructsByItemIdentifier(loc2.getReference()).isEmpty());
		assertTrue(index.getConstructsByItemIdentifier(loc3.getReference()).contains(otherTopic));
		assertFalse(index.getConstructsByItemIdentifier("http://www.example.org/.*").contains(topic));
		assertTrue(index.getConstructsByItemIdentifier("http://www.example.org/.*").contains(otherTopic));
				
		index.close();
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			//NOTHING TO DO
		}
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getItemIdentifiers()}.
	 */
	public void testGetItemIdentifiers() {		
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		Locator loc1 = topicMap.createLocator("http://psi.example.org/1");		
		Locator loc2 = topicMap.createLocator("http://psi.example.org/2");		
		Locator loc3 = topicMap.createLocator("http://psi.example.org/3");
		Locator loc4 = topicMap.createLocator("http://psi.example.org/4");
		Locator loc5 = topicMap.createLocator("http://psi.example.org/5");
		Locator loc6 = topicMap.createLocator("http://psi.example.org/6");
		Locator loc7 = topicMap.createLocator("http://psi.example.org/7");
		Locator loc8 = topicMap.createLocator("http://psi.example.org/8");
		
		ITopic topic = createTopic();
		Name name = topic.createName("Name", new Topic[0]);
		Occurrence occurrence = topic.createOccurrence(createTopic(),"Occurrence", new Topic[0]);
		Variant variant = name.createVariant("Variant", createTopic());
		Association association = createAssociation(createTopic());
		Role role = association.createRole(createTopic(), createTopic());
		
		assertEquals(6, index.getItemIdentifiers().size());
		assertFalse(index.getItemIdentifiers().contains(loc1));
		assertFalse(index.getItemIdentifiers().contains(loc2));
		assertFalse(index.getItemIdentifiers().contains(loc3));
		assertFalse(index.getItemIdentifiers().contains(loc4));
		assertFalse(index.getItemIdentifiers().contains(loc5));
		assertFalse(index.getItemIdentifiers().contains(loc6));
		assertFalse(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		topic.addItemIdentifier(loc1);
		assertEquals(7, index.getItemIdentifiers().size());
		assertTrue(index.getItemIdentifiers().contains(loc1));
		assertFalse(index.getItemIdentifiers().contains(loc2));
		assertFalse(index.getItemIdentifiers().contains(loc3));
		assertFalse(index.getItemIdentifiers().contains(loc4));
		assertFalse(index.getItemIdentifiers().contains(loc5));
		assertFalse(index.getItemIdentifiers().contains(loc6));
		assertFalse(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		name.addItemIdentifier(loc2);
		assertEquals(8, index.getItemIdentifiers().size());
		assertTrue(index.getItemIdentifiers().contains(loc1));
		assertTrue(index.getItemIdentifiers().contains(loc2));
		assertFalse(index.getItemIdentifiers().contains(loc3));
		assertFalse(index.getItemIdentifiers().contains(loc4));
		assertFalse(index.getItemIdentifiers().contains(loc5));
		assertFalse(index.getItemIdentifiers().contains(loc6));
		assertFalse(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		occurrence.addItemIdentifier(loc3);
		assertEquals(9, index.getItemIdentifiers().size());
		assertTrue(index.getItemIdentifiers().contains(loc1));
		assertTrue(index.getItemIdentifiers().contains(loc2));
		assertTrue(index.getItemIdentifiers().contains(loc3));
		assertFalse(index.getItemIdentifiers().contains(loc4));
		assertFalse(index.getItemIdentifiers().contains(loc5));
		assertFalse(index.getItemIdentifiers().contains(loc6));
		assertFalse(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		variant.addItemIdentifier(loc4);
		assertEquals(10, index.getItemIdentifiers().size());
		assertTrue(index.getItemIdentifiers().contains(loc1));
		assertTrue(index.getItemIdentifiers().contains(loc2));
		assertTrue(index.getItemIdentifiers().contains(loc3));
		assertTrue(index.getItemIdentifiers().contains(loc4));
		assertFalse(index.getItemIdentifiers().contains(loc5));
		assertFalse(index.getItemIdentifiers().contains(loc6));
		assertFalse(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		role.addItemIdentifier(loc5);
		assertEquals(11, index.getItemIdentifiers().size());
		assertTrue(index.getItemIdentifiers().contains(loc1));
		assertTrue(index.getItemIdentifiers().contains(loc2));
		assertTrue(index.getItemIdentifiers().contains(loc3));
		assertTrue(index.getItemIdentifiers().contains(loc4));
		assertTrue(index.getItemIdentifiers().contains(loc5));
		assertFalse(index.getItemIdentifiers().contains(loc6));
		assertFalse(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		association.addItemIdentifier(loc6);
		assertEquals(12, index.getItemIdentifiers().size());
		assertTrue(index.getItemIdentifiers().contains(loc1));
		assertTrue(index.getItemIdentifiers().contains(loc2));
		assertTrue(index.getItemIdentifiers().contains(loc3));
		assertTrue(index.getItemIdentifiers().contains(loc4));
		assertTrue(index.getItemIdentifiers().contains(loc5));
		assertTrue(index.getItemIdentifiers().contains(loc6));
		assertFalse(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		topicMap.addItemIdentifier(loc7);
		assertEquals(13, index.getItemIdentifiers().size());
		assertTrue(index.getItemIdentifiers().contains(loc1));
		assertTrue(index.getItemIdentifiers().contains(loc2));
		assertTrue(index.getItemIdentifiers().contains(loc3));
		assertTrue(index.getItemIdentifiers().contains(loc4));
		assertTrue(index.getItemIdentifiers().contains(loc5));
		assertTrue(index.getItemIdentifiers().contains(loc6));
		assertTrue(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		// REMOVING THEM 
		
		topic.removeItemIdentifier(loc1);
		assertEquals(12, index.getItemIdentifiers().size());
		assertFalse(index.getItemIdentifiers().contains(loc1));
		assertTrue(index.getItemIdentifiers().contains(loc2));
		assertTrue(index.getItemIdentifiers().contains(loc3));
		assertTrue(index.getItemIdentifiers().contains(loc4));
		assertTrue(index.getItemIdentifiers().contains(loc5));
		assertTrue(index.getItemIdentifiers().contains(loc6));
		assertTrue(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		name.removeItemIdentifier(loc2);
		assertEquals(11, index.getItemIdentifiers().size());
		assertFalse(index.getItemIdentifiers().contains(loc1));
		assertFalse(index.getItemIdentifiers().contains(loc2));
		assertTrue(index.getItemIdentifiers().contains(loc3));
		assertTrue(index.getItemIdentifiers().contains(loc4));
		assertTrue(index.getItemIdentifiers().contains(loc5));
		assertTrue(index.getItemIdentifiers().contains(loc6));
		assertTrue(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		occurrence.removeItemIdentifier(loc3);
		assertEquals(10, index.getItemIdentifiers().size());
		assertFalse(index.getItemIdentifiers().contains(loc1));
		assertFalse(index.getItemIdentifiers().contains(loc2));
		assertFalse(index.getItemIdentifiers().contains(loc3));
		assertTrue(index.getItemIdentifiers().contains(loc4));
		assertTrue(index.getItemIdentifiers().contains(loc5));
		assertTrue(index.getItemIdentifiers().contains(loc6));
		assertTrue(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		variant.removeItemIdentifier(loc4);
		assertEquals(9, index.getItemIdentifiers().size());
		assertFalse(index.getItemIdentifiers().contains(loc1));
		assertFalse(index.getItemIdentifiers().contains(loc2));
		assertFalse(index.getItemIdentifiers().contains(loc3));
		assertFalse(index.getItemIdentifiers().contains(loc4));
		assertTrue(index.getItemIdentifiers().contains(loc5));
		assertTrue(index.getItemIdentifiers().contains(loc6));
		assertTrue(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		role.removeItemIdentifier(loc5);
		assertEquals(8, index.getItemIdentifiers().size());
		assertFalse(index.getItemIdentifiers().contains(loc1));
		assertFalse(index.getItemIdentifiers().contains(loc2));
		assertFalse(index.getItemIdentifiers().contains(loc3));
		assertFalse(index.getItemIdentifiers().contains(loc4));
		assertFalse(index.getItemIdentifiers().contains(loc5));
		assertTrue(index.getItemIdentifiers().contains(loc6));
		assertTrue(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		association.removeItemIdentifier(loc6);
		assertEquals(7, index.getItemIdentifiers().size());
		assertFalse(index.getItemIdentifiers().contains(loc1));
		assertFalse(index.getItemIdentifiers().contains(loc2));
		assertFalse(index.getItemIdentifiers().contains(loc3));
		assertFalse(index.getItemIdentifiers().contains(loc4));
		assertFalse(index.getItemIdentifiers().contains(loc5));
		assertFalse(index.getItemIdentifiers().contains(loc6));
		assertTrue(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
		
		topicMap.removeItemIdentifier(loc7);
		assertEquals(6, index.getItemIdentifiers().size());
		assertFalse(index.getItemIdentifiers().contains(loc1));
		assertFalse(index.getItemIdentifiers().contains(loc2));
		assertFalse(index.getItemIdentifiers().contains(loc3));
		assertFalse(index.getItemIdentifiers().contains(loc4));
		assertFalse(index.getItemIdentifiers().contains(loc5));
		assertFalse(index.getItemIdentifiers().contains(loc6));
		assertFalse(index.getItemIdentifiers().contains(loc7));
		assertFalse(index.getItemIdentifiers().contains(loc8));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getSubjectIdentifiers()}.
	 */
	public void testGetSubjectIdentifiers() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		Locator loc1 = topicMap.createLocator("http://psi.example.org/1");		
		Locator loc2 = topicMap.createLocator("http://psi.example.org/2");		
		Locator loc3 = topicMap.createLocator("http://psi.example.org/3");
		Locator loc4 = topicMap.createLocator("http://psi.example.org/4");
		
		ITopic topic = createTopic();
		ITopic otherTopic = createTopic();
		
		assertEquals(0, index.getSubjectIdentifiers().size());
		assertFalse(index.getSubjectIdentifiers().contains(loc1));
		assertFalse(index.getSubjectIdentifiers().contains(loc2));
		assertFalse(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		topic.addSubjectIdentifier(loc1);
		assertEquals(1, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertFalse(index.getSubjectIdentifiers().contains(loc2));
		assertFalse(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		topic.addSubjectIdentifier(loc2);
		assertEquals(2, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertTrue(index.getSubjectIdentifiers().contains(loc2));
		assertFalse(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		topic.addSubjectLocator(loc3);
		assertEquals(2, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertTrue(index.getSubjectIdentifiers().contains(loc2));
		assertFalse(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		otherTopic.addSubjectIdentifier(loc3);
		assertEquals(3, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertTrue(index.getSubjectIdentifiers().contains(loc2));
		assertTrue(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		otherTopic.addSubjectIdentifier(loc4);
		assertEquals(4, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertTrue(index.getSubjectIdentifiers().contains(loc2));
		assertTrue(index.getSubjectIdentifiers().contains(loc3));
		assertTrue(index.getSubjectIdentifiers().contains(loc4));
		
		topic.removeSubjectIdentifier(loc4);
		assertEquals(4, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertTrue(index.getSubjectIdentifiers().contains(loc2));
		assertTrue(index.getSubjectIdentifiers().contains(loc3));
		assertTrue(index.getSubjectIdentifiers().contains(loc4));
		
		otherTopic.removeSubjectIdentifier(loc4);
		assertEquals(3, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertTrue(index.getSubjectIdentifiers().contains(loc2));
		assertTrue(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		topic.removeSubjectLocator(loc3);
		assertEquals(3, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertTrue(index.getSubjectIdentifiers().contains(loc2));
		assertTrue(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		otherTopic.removeSubjectIdentifier(loc3);
		assertEquals(2, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertTrue(index.getSubjectIdentifiers().contains(loc2));
		assertFalse(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		topic.removeSubjectIdentifier(loc2);
		assertEquals(1, index.getSubjectIdentifiers().size());
		assertTrue(index.getSubjectIdentifiers().contains(loc1));
		assertFalse(index.getSubjectIdentifiers().contains(loc2));
		assertFalse(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
		
		topic.removeSubjectIdentifier(loc1);
		assertEquals(0, index.getSubjectIdentifiers().size());
		assertFalse(index.getSubjectIdentifiers().contains(loc1));
		assertFalse(index.getSubjectIdentifiers().contains(loc2));
		assertFalse(index.getSubjectIdentifiers().contains(loc3));
		assertFalse(index.getSubjectIdentifiers().contains(loc4));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getSubjectLocators()}.
	 */
	public void testGetSubjectLocators() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		Locator loc1 = topicMap.createLocator("http://psi.example.org/1");		
		Locator loc2 = topicMap.createLocator("http://psi.example.org/2");		
		Locator loc3 = topicMap.createLocator("http://psi.example.org/3");
		Locator loc4 = topicMap.createLocator("http://psi.example.org/4");
		
		ITopic topic = createTopic();
		ITopic otherTopic = createTopic();
		
		assertEquals(0, index.getSubjectLocators().size());
		assertFalse(index.getSubjectLocators().contains(loc1));
		assertFalse(index.getSubjectLocators().contains(loc2));
		assertFalse(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		topic.addSubjectLocator(loc1);
		assertEquals(1, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertFalse(index.getSubjectLocators().contains(loc2));
		assertFalse(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		topic.addSubjectLocator(loc2);
		assertEquals(2, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertTrue(index.getSubjectLocators().contains(loc2));
		assertFalse(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		topic.addSubjectIdentifier(loc3);
		assertEquals(2, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertTrue(index.getSubjectLocators().contains(loc2));
		assertFalse(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		otherTopic.addSubjectLocator(loc3);
		assertEquals(3, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertTrue(index.getSubjectLocators().contains(loc2));
		assertTrue(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		otherTopic.addSubjectLocator(loc4);
		assertEquals(4, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertTrue(index.getSubjectLocators().contains(loc2));
		assertTrue(index.getSubjectLocators().contains(loc3));
		assertTrue(index.getSubjectLocators().contains(loc4));
		
		topic.removeSubjectLocator(loc4);
		assertEquals(4, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertTrue(index.getSubjectLocators().contains(loc2));
		assertTrue(index.getSubjectLocators().contains(loc3));
		assertTrue(index.getSubjectLocators().contains(loc4));
		
		otherTopic.removeSubjectLocator(loc4);
		assertEquals(3, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertTrue(index.getSubjectLocators().contains(loc2));
		assertTrue(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		topic.removeSubjectIdentifier(loc3);
		assertEquals(3, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertTrue(index.getSubjectLocators().contains(loc2));
		assertTrue(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		otherTopic.removeSubjectLocator(loc3);
		assertEquals(2, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertTrue(index.getSubjectLocators().contains(loc2));
		assertFalse(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		topic.removeSubjectLocator(loc2);
		assertEquals(1, index.getSubjectLocators().size());
		assertTrue(index.getSubjectLocators().contains(loc1));
		assertFalse(index.getSubjectLocators().contains(loc2));
		assertFalse(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
		
		topic.removeSubjectLocator(loc1);
		assertEquals(0, index.getSubjectLocators().size());
		assertFalse(index.getSubjectLocators().contains(loc1));
		assertFalse(index.getSubjectLocators().contains(loc2));
		assertFalse(index.getSubjectLocators().contains(loc3));
		assertFalse(index.getSubjectLocators().contains(loc4));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getTopicBySubjectIdentifier(java.lang.String)}.
	 */
	public void testGetTopicBySubjectIdentifierString() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		Locator loc1 = topicMap.createLocator("http://psi.example.org/1");		
		Locator loc2 = topicMap.createLocator("http://psi.example.org/2");
		
		assertNull(index.getTopicBySubjectIdentifier(loc1));
		assertNull(index.getTopicBySubjectIdentifier(loc1.getReference()));
		assertNull(index.getTopicBySubjectIdentifier(loc2));
		assertNull(index.getTopicBySubjectIdentifier(loc2.getReference()));
		
		Topic topic = topicMap.createTopicBySubjectIdentifier(loc1);
		assertEquals(topic, index.getTopicBySubjectIdentifier(loc1));
		assertEquals(topic, index.getTopicBySubjectIdentifier(loc1.getReference()));
		assertNull(index.getTopicBySubjectIdentifier(loc2));
		assertNull(index.getTopicBySubjectIdentifier(loc2.getReference()));
		
		topic.removeSubjectLocator(loc1);
		assertEquals(topic, index.getTopicBySubjectIdentifier(loc1));
		assertEquals(topic, index.getTopicBySubjectIdentifier(loc1.getReference()));
		assertNull(index.getTopicBySubjectIdentifier(loc2));
		assertNull(index.getTopicBySubjectIdentifier(loc2.getReference()));
		
		topic.removeSubjectIdentifier(loc1);
		assertNull(index.getTopicBySubjectIdentifier(loc1));
		assertNull(index.getTopicBySubjectIdentifier(loc1.getReference()));
		assertNull(index.getTopicBySubjectIdentifier(loc2));
		assertNull(index.getTopicBySubjectIdentifier(loc2.getReference()));
		
		topic = topicMap.createTopicBySubjectLocator(loc2);
		assertNull(index.getTopicBySubjectIdentifier(loc1));
		assertNull(index.getTopicBySubjectIdentifier(loc1.getReference()));
		assertNull(index.getTopicBySubjectIdentifier(loc2));
		assertNull(index.getTopicBySubjectIdentifier(loc2.getReference()));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getTopicBySubjectLocator(java.lang.String)}.
	 */
	public void testGetTopicBySubjectLocatorString() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		Locator loc1 = topicMap.createLocator("http://psi.example.org/1");		
		Locator loc2 = topicMap.createLocator("http://psi.example.org/2");
		
		assertNull(index.getTopicBySubjectLocator(loc1));
		assertNull(index.getTopicBySubjectLocator(loc1.getReference()));
		assertNull(index.getTopicBySubjectLocator(loc2));
		assertNull(index.getTopicBySubjectLocator(loc2.getReference()));
		
		Topic topic = topicMap.createTopicBySubjectLocator(loc1);
		assertEquals(topic, index.getTopicBySubjectLocator(loc1));
		assertEquals(topic, index.getTopicBySubjectLocator(loc1.getReference()));
		assertNull(index.getTopicBySubjectLocator(loc2));
		assertNull(index.getTopicBySubjectLocator(loc2.getReference()));
		
		topic.removeSubjectIdentifier(loc1);
		assertEquals(topic, index.getTopicBySubjectLocator(loc1));
		assertEquals(topic, index.getTopicBySubjectLocator(loc1.getReference()));
		assertNull(index.getTopicBySubjectLocator(loc2));
		assertNull(index.getTopicBySubjectLocator(loc2.getReference()));
		
		topic.removeSubjectLocator(loc1);
		assertNull(index.getTopicBySubjectLocator(loc1));
		assertNull(index.getTopicBySubjectLocator(loc1.getReference()));
		assertNull(index.getTopicBySubjectLocator(loc2));
		assertNull(index.getTopicBySubjectLocator(loc2.getReference()));
		
		topic = topicMap.createTopicBySubjectIdentifier(loc2);
		assertNull(index.getTopicBySubjectLocator(loc1));
		assertNull(index.getTopicBySubjectLocator(loc1.getReference()));
		assertNull(index.getTopicBySubjectLocator(loc2));
		assertNull(index.getTopicBySubjectLocator(loc2.getReference()));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getTopicsBySubjectIdentifier(java.util.regex.Pattern)}.
	 */
	public void testGetTopicsBySubjectIdentifierPattern() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String pattern = "http://psi.example.org/";
		
		Locator loc1 = topicMap.createLocator(pattern+"1");		
		Locator loc2 = topicMap.createLocator(pattern+"2");
		Locator loc3 = topicMap.createLocator(pattern+"3");
		
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		
		Topic topic = topicMap.createTopicBySubjectIdentifier(loc1);
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(1, index.getTopicsBySubjectIdentifier(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(topic));
		assertEquals(1, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(topic));
		
		Topic otherTopic = topicMap.createTopicBySubjectLocator(loc3);
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(1, index.getTopicsBySubjectIdentifier(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(topic));
		assertEquals(1, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(topic));
		
		otherTopic.addSubjectIdentifier(loc2);
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(2, index.getTopicsBySubjectIdentifier(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(otherTopic));
		assertEquals(2, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(otherTopic));
		
		Topic thirdTopic = createTopic();
		thirdTopic.addSubjectIdentifier(loc3);
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(2, index.getTopicsBySubjectIdentifier(pattern+"[12]").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+"[12]").contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+"[12]").contains(otherTopic));
		assertEquals(2, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).contains(otherTopic));
		
		otherTopic.removeSubjectLocator(loc3);
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(2, index.getTopicsBySubjectIdentifier(pattern+"[12]").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+"[12]").contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+"[12]").contains(otherTopic));
		assertEquals(2, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).contains(otherTopic));
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(3, index.getTopicsBySubjectIdentifier(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(otherTopic));
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(thirdTopic));
		assertEquals(3, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(otherTopic));
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(thirdTopic));
		
		otherTopic.removeSubjectIdentifier(loc2);
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(1, index.getTopicsBySubjectIdentifier(pattern+"[12]").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+"[12]").contains(topic));
		assertEquals(1, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).contains(topic));
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(2, index.getTopicsBySubjectIdentifier(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(thirdTopic));
		assertEquals(2, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(topic));
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(thirdTopic));
		
		topic.remove();
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern+"[12]").size());
		assertEquals(0, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+"[12]")).size());
		assertEquals(0, index.getTopicsBySubjectIdentifier(pattern).size());
		assertEquals(1, index.getTopicsBySubjectIdentifier(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectIdentifier(pattern+".*").contains(thirdTopic));
		assertEquals(1, index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectIdentifier(Pattern.compile(pattern+".*")).contains(thirdTopic));
	}

	/**
	 * Test method for {@link de.topicmapslab.majortom.inMemory.index.InMemoryIdentityIndex#getTopicsBySubjectLocator(java.util.regex.Pattern)}.
	 */
	public void testGetTopicsBySubjectLocatorPattern() {
		IIdentityIndex index = topicMap.getIndex(IIdentityIndex.class);
		assertNotNull(index);
		try{
			index.existsSubjectLocator((String)null);
			fail("Index should be close!");
		}catch(TMAPIRuntimeException e){
			index.open();
		}
		
		final String pattern = "http://psi.example.org/";
		
		Locator loc1 = topicMap.createLocator(pattern+"1");		
		Locator loc2 = topicMap.createLocator(pattern+"2");
		Locator loc3 = topicMap.createLocator(pattern+"3");
		
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		
		Topic topic = topicMap.createTopicBySubjectLocator(loc1);
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(1, index.getTopicsBySubjectLocator(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(topic));
		assertEquals(1, index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(topic));
		
		Topic otherTopic = topicMap.createTopicBySubjectIdentifier(loc3);
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(1, index.getTopicsBySubjectLocator(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(topic));
		assertEquals(1, index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(topic));
		
		otherTopic.addSubjectLocator(loc2);
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(2, index.getTopicsBySubjectLocator(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(otherTopic));
		assertEquals(2, index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(otherTopic));
		
		Topic thirdTopic = createTopic();
		thirdTopic.addSubjectLocator(loc3);
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(2, index.getTopicsBySubjectLocator(pattern+"[12]").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+"[12]").contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(pattern+"[12]").contains(otherTopic));
		assertEquals(2, index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).contains(otherTopic));
		
		otherTopic.removeSubjectIdentifier(loc3);
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(2, index.getTopicsBySubjectLocator(pattern+"[12]").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+"[12]").contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(pattern+"[12]").contains(otherTopic));
		assertEquals(2, index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).contains(otherTopic));
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(3, index.getTopicsBySubjectLocator(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(otherTopic));
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(thirdTopic));
		assertEquals(3, index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(otherTopic));
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(thirdTopic));
		
		otherTopic.removeSubjectLocator(loc2);
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(1, index.getTopicsBySubjectLocator(pattern+"[12]").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+"[12]").contains(topic));
		assertEquals(1, index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).contains(topic));
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(2, index.getTopicsBySubjectLocator(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(thirdTopic));
		assertEquals(2, index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(topic));
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(thirdTopic));
		
		topic.remove();
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(0, index.getTopicsBySubjectLocator(pattern+"[12]").size());
		assertEquals(0, index.getTopicsBySubjectLocator(Pattern.compile(pattern+"[12]")).size());
		assertEquals(0, index.getTopicsBySubjectLocator(pattern).size());
		assertEquals(1, index.getTopicsBySubjectLocator(pattern+".*").size());
		assertTrue(index.getTopicsBySubjectLocator(pattern+".*").contains(thirdTopic));
		assertEquals(1, index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).size());
		assertTrue(index.getTopicsBySubjectLocator(Pattern.compile(pattern+".*")).contains(thirdTopic));
	}

}
