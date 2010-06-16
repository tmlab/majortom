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
package de.topicmapslab.majortom.tests.feed;

import java.io.File;
import java.io.PrintWriter;

import org.tmapi.core.Name;
import org.tmapi.core.Topic;

import com.sun.syndication.feed.WireFeed;
import com.sun.syndication.io.WireFeedOutput;

import de.topicmapslab.majortom.feed.model.FeedContentType;
import de.topicmapslab.majortom.feed.rss.RevisionRssFeeder;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

/**
 * @author Sven Krosse
 * 
 */
public class TestFeed extends MaJorToMTestCase {

	public void testRssFeedWithXmlContent() throws Exception {
		ITopic topic = createTopicBySI("http://psi.example.org/topic");
		doChanges(topic);

		RevisionRssFeeder feeder = new RevisionRssFeeder(topicMap);
		WireFeed feed = feeder.getFeed(topic);

		WireFeedOutput output = new WireFeedOutput();
		output.output(feed, new PrintWriter(System.out));
	}

	public void testRssFeedWithTextContent() throws Exception {
		ITopic topic = createTopicBySI("http://psi.example.org/topic");
		doChanges(topic);

		RevisionRssFeeder feeder = new RevisionRssFeeder(topicMap);
		feeder.setContentType(FeedContentType.TEXT);
		WireFeed feed = feeder.getFeed(topic);

		WireFeedOutput output = new WireFeedOutput();
		output.output(feed, new PrintWriter(System.out));

		output.output(feed, new File("src/test/resources/test.rss"));
	}

	private void doChanges(ITopic topic) {
		topic.createName("Name", new Topic[0]);
		topic.createOccurrence(createTopic(), "Occurrence", new Topic[0]);
		topic.addType(createTopic());

		ITopic topic2 = createTopicBySL("http://psi.example.org/topic");
		topic2.createName("Name 2", new Topic[0]);
		topic2.createOccurrence(createTopic(), "Occurrence", new Topic[0]);

		Topic otherTopic = createTopic();
		Name n = otherTopic.createName("Name 3", new Topic[0]);
		n.setReifier(topic2);

		topic.mergeIn(topic2);
	}
}
