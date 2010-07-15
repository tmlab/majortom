package de.topicmapslab.majortom.tests.transaction;

import org.tmapi.core.Locator;
import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IAssociation;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.IScopable;
import de.topicmapslab.majortom.model.core.IScope;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.index.IScopedIndex;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

public class TestScopeTransaction extends MaJorToMTestCase {

	public void testName() throws Exception {
		ITopic theme = createTopic();
		_testScoped((IName) createTopic().createName("Name", theme));
	}

	public void testOccurrence() throws Exception {
		ITopic theme = createTopic();
		_testScoped((IOccurrence) createTopic().createOccurrence(createTopic(), "Occ", theme));
	}

	public void testVariant() throws Exception {
		ITopic theme = createTopic();
		_testScoped((IVariant) createTopic().createName("Name", new Topic[0]).createVariant("Variant", theme));
	}

	public void testAssociation() throws Exception {
		ITopic theme = createTopic();
		_testScoped((IAssociation) topicMap.createAssociation(createTopic(), theme));
	}

	public void _testScoped(IScopable scopable) throws Exception {
		ITransaction transaction = topicMap.createTransaction();
		IScopable scopable_ = transaction.moveToTransactionContext(scopable);
		assertEquals(scopable, scopable_);
		assertEquals(scopable.getScope(), scopable_.getScope());
		
		IScopedIndex index = topicMap.getIndex(IScopedIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		IScopedIndex index_ = transaction.getIndex(IScopedIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		IScope scope = scopable.getScopeObject();		
		IScope scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope).contains(scopable_));
		
		scopable_.addTheme(transaction.createTopic());
		assertEquals(2, scopable_.getScope().size());
		assertEquals(1, scopable.getScope().size());
		
		transaction.rollback();
		
		transaction = topicMap.createTransaction();
		scopable_ = transaction.moveToTransactionContext(scopable);
		assertEquals(scopable, scopable_);
		assertEquals(scopable.getScope(), scopable_.getScope());
		
		index = topicMap.getIndex(IScopedIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(IScopedIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		scope = scopable.getScopeObject();		
		scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope_).contains(scopable_));
		
		Topic theme = transaction.createTopic();
		scopable_.addTheme(theme);
		assertEquals(2, scopable_.getScope().size());
		assertEquals(1, scopable.getScope().size());
		
		scope = scopable.getScopeObject();		
		scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope_).contains(scopable_));
		
		scopable_.removeTheme(theme);
		assertEquals(1, scopable_.getScope().size());
		assertEquals(1, scopable.getScope().size());
		
		transaction.rollback();
		
		assertEquals(1, scopable.getScope().size());
		
		transaction = topicMap.createTransaction();
		scopable_ = transaction.moveToTransactionContext(scopable);
		assertEquals(scopable, scopable_);
		assertEquals(scopable.getScope(), scopable_.getScope());
		
		index = topicMap.getIndex(IScopedIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(IScopedIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		scope = scopable.getScopeObject();		
		scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope_).contains(scopable_));
		
		Locator loc = topicMap.createLocator("http://psi.example.org/topic");
		
		theme = transaction.createTopicBySubjectIdentifier(loc);
		scopable_.addTheme(theme);
		assertEquals(2, scopable_.getScope().size());
		assertEquals(1, scopable.getScope().size());
		
		scope = scopable.getScopeObject();		
		scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope_).contains(scopable_));
		
		transaction.commit();
		
		assertEquals(2, scopable.getScope().size());
		
		transaction = topicMap.createTransaction();
		scopable_ = transaction.moveToTransactionContext(scopable);
		assertEquals(scopable, scopable_);
		assertEquals(scopable.getScope(), scopable_.getScope());
		
		index = topicMap.getIndex(IScopedIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(IScopedIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		scope = scopable.getScopeObject();		
		scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope_).contains(scopable_));
		
		loc = topicMap.createLocator("http://psi.example.org/topic");
		
		theme = transaction.getTopicBySubjectIdentifier(loc);
		scopable_.removeTheme(theme);
		assertEquals(1, scopable_.getScope().size());
		assertEquals(2, scopable.getScope().size());
		
		scope = scopable.getScopeObject();		
		scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope_).contains(scopable_));
		
		transaction.rollback();
		
		assertEquals(2, scopable.getScope().size());
		
		transaction = topicMap.createTransaction();
		scopable_ = transaction.moveToTransactionContext(scopable);
		assertEquals(scopable, scopable_);
		assertEquals(scopable.getScope(), scopable_.getScope());
		
		index = topicMap.getIndex(IScopedIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(IScopedIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		scope = scopable.getScopeObject();		
		scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope_).contains(scopable_));
		
		loc = topicMap.createLocator("http://psi.example.org/topic");
		
		theme = transaction.getTopicBySubjectIdentifier(loc);
		scopable_.removeTheme(theme);
		assertEquals(1, scopable_.getScope().size());
		assertEquals(2, scopable.getScope().size());
		
		scope = scopable.getScopeObject();		
		scope_ = scopable_.getScopeObject();
		
		assertFalse(index.getScopables(scope).isEmpty());
		assertTrue(index.getScopables(scope).contains(scopable));
		assertFalse(index_.getScopables(scope_).isEmpty());
		assertTrue(index_.getScopables(scope_).contains(scopable_));
		
		transaction.commit();
		
		assertEquals(1, scopable.getScope().size());	
	}

}
