package de.topicmapslab.majortom.tests.transaction;

import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.index.ISupertypeSubtypeIndex;
import de.topicmapslab.majortom.model.index.ITypeInstanceIndex;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

public class TestTopicTypeTransaction extends MaJorToMTestCase {

	public void testTypeModification() throws Exception {
		ITopic topic = createTopic();
		ITopic type = createTopic();
		ITopic otherType = createTopic();
		
		assertEquals(0, topic.getTypes().size());
		
		ITransaction transaction = topicMap.createTransaction();
		ITopic topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		ITopic type_ = transaction.moveToTransactionContext(type);
		assertEquals(type, type_);
		ITopic otherType_ = transaction.moveToTransactionContext(otherType);
		assertEquals(otherType, otherType_);
		
		ITypeInstanceIndex index = topicMap.getIndex(ITypeInstanceIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		ITypeInstanceIndex index_ = transaction.getIndex(ITypeInstanceIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		
		assertEquals(0, index.getTopicTypes().size());
		assertEquals(0, index.getTopics(type).size());
		assertEquals(0, index.getTopics(otherType).size());
		assertEquals(0, index_.getTopicTypes().size());
		assertEquals(0, index_.getTopics(type_).size());
		assertEquals(0, index_.getTopics(otherType_).size());
		
		topic_.addType(type_);		
		assertEquals(0, topic.getTypes().size());
		assertEquals(1, topic_.getTypes().size());
		assertTrue(topic_.getTypes().contains(type_));
		
		assertEquals(0, index.getTopicTypes().size());
		assertEquals(0, index.getTopics(type).size());
		assertEquals(0, index.getTopics(otherType).size());
		assertEquals(1, index_.getTopicTypes().size());
		assertTrue(index_.getTopicTypes().contains(type_));
		assertEquals(1, index_.getTopics(type_).size());
		assertTrue(index_.getTopics(type_).contains(topic_));
		assertEquals(0, index_.getTopics(otherType_).size());
		
		topic_.addType(otherType_);
		assertEquals(0, topic.getTypes().size());
		assertEquals(2, topic_.getTypes().size());
		assertTrue(topic_.getTypes().contains(type_));
		assertTrue(topic_.getTypes().contains(otherType_));
		
		assertEquals(0, index.getTopicTypes().size());
		assertEquals(0, index.getTopics(type).size());
		assertEquals(0, index.getTopics(otherType).size());
		assertEquals(2, index_.getTopicTypes().size());
		assertTrue(index_.getTopicTypes().contains(type_));
		assertTrue(index_.getTopicTypes().contains(otherType_));
		assertEquals(1, index_.getTopics(type_).size());
		assertTrue(index_.getTopics(type_).contains(topic_));
		assertEquals(1, index_.getTopics(otherType_).size());
		assertTrue(index_.getTopics(otherType_).contains(topic_));
		
		transaction.rollback();
		assertEquals(0, topic.getTypes().size());
		assertEquals(0, index.getTopicTypes().size());
		assertEquals(0, index.getTopics(type).size());
		assertEquals(0, index.getTopics(otherType).size());
		
		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		type_ = transaction.moveToTransactionContext(type);
		assertEquals(type, type_);
		otherType_ = transaction.moveToTransactionContext(otherType);
		assertEquals(otherType, otherType_);
		
		index = topicMap.getIndex(ITypeInstanceIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(ITypeInstanceIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		
		assertEquals(0, index.getTopicTypes().size());
		assertEquals(0, index.getTopics(type).size());
		assertEquals(0, index.getTopics(otherType).size());
		assertEquals(0, index_.getTopicTypes().size());
		assertEquals(0, index_.getTopics(type_).size());
		assertEquals(0, index_.getTopics(otherType_).size());
		
		topic_.addType(type_);		
		assertEquals(0, topic.getTypes().size());
		assertEquals(1, topic_.getTypes().size());
		assertTrue(topic_.getTypes().contains(type_));
		
		assertEquals(0, index.getTopicTypes().size());
		assertEquals(0, index.getTopics(type).size());
		assertEquals(0, index.getTopics(otherType).size());
		assertEquals(1, index_.getTopicTypes().size());
		assertTrue(index_.getTopicTypes().contains(type_));
		assertEquals(1, index_.getTopics(type_).size());
		assertTrue(index_.getTopics(type_).contains(topic_));
		assertEquals(0, index_.getTopics(otherType_).size());
		
		topic_.addType(otherType_);
		assertEquals(0, topic.getTypes().size());
		assertEquals(2, topic_.getTypes().size());
		assertTrue(topic_.getTypes().contains(type_));
		assertTrue(topic_.getTypes().contains(otherType_));
		
		assertEquals(0, index.getTopicTypes().size());
		assertEquals(0, index.getTopics(type).size());
		assertEquals(0, index.getTopics(otherType).size());
		assertEquals(2, index_.getTopicTypes().size());
		assertTrue(index_.getTopicTypes().contains(type_));
		assertTrue(index_.getTopicTypes().contains(otherType_));
		assertEquals(1, index_.getTopics(type_).size());
		assertTrue(index_.getTopics(type_).contains(topic_));
		assertEquals(1, index_.getTopics(otherType_).size());
		assertTrue(index_.getTopics(otherType_).contains(topic_));
		
		transaction.commit();
		assertEquals(2, topic.getTypes().size());
		assertEquals(2, index.getTopicTypes().size());
		assertTrue(index.getTopicTypes().contains(type));
		assertTrue(index.getTopicTypes().contains(otherType));
		assertEquals(1, index.getTopics(type).size());
		assertTrue(index.getTopics(type).contains(topic));
		assertEquals(1, index.getTopics(otherType).size());
		assertTrue(index.getTopics(otherType).contains(topic));
		
		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		type_ = transaction.moveToTransactionContext(type);
		assertEquals(type, type_);
		otherType_ = transaction.moveToTransactionContext(otherType);
		assertEquals(otherType, otherType_);
		
		index = topicMap.getIndex(ITypeInstanceIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(ITypeInstanceIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		
		assertEquals(2, topic.getTypes().size());
		assertEquals(2, index.getTopicTypes().size());
		assertTrue(index.getTopicTypes().contains(type));
		assertTrue(index.getTopicTypes().contains(otherType));
		assertEquals(1, index.getTopics(type).size());
		assertTrue(index.getTopics(type).contains(topic));
		assertEquals(1, index.getTopics(otherType).size());
		assertTrue(index.getTopics(otherType).contains(topic));
		
		assertEquals(2, topic_.getTypes().size());
		assertEquals(2, index_.getTopicTypes().size());
		assertTrue(index_.getTopicTypes().contains(type_));
		assertTrue(index_.getTopicTypes().contains(otherType_));
		assertEquals(1, index_.getTopics(type_).size());
		assertTrue(index_.getTopics(type_).contains(topic_));
		assertEquals(1, index_.getTopics(otherType_).size());
		assertTrue(index_.getTopics(otherType_).contains(topic_));
		
		topic_.removeType(type_);	
		
		assertEquals(2, topic.getTypes().size());
		assertEquals(2, index.getTopicTypes().size());
		assertTrue(index.getTopicTypes().contains(type));
		assertTrue(index.getTopicTypes().contains(otherType));
		assertEquals(1, index.getTopics(type).size());
		assertTrue(index.getTopics(type).contains(topic));
		assertEquals(1, index.getTopics(otherType).size());
		assertTrue(index.getTopics(otherType).contains(topic));
		
		assertEquals(1, topic_.getTypes().size());
		assertEquals(1, index_.getTopicTypes().size());
		assertTrue(index_.getTopicTypes().contains(otherType_));
		assertEquals(0, index_.getTopics(type_).size());
		assertEquals(1, index_.getTopics(otherType_).size());
		assertTrue(index_.getTopics(otherType_).contains(topic_));
		
		transaction.rollback();
		
		assertEquals(2, topic.getTypes().size());
		assertEquals(2, index.getTopicTypes().size());
		assertTrue(index.getTopicTypes().contains(type));
		assertTrue(index.getTopicTypes().contains(otherType));
		assertEquals(1, index.getTopics(type).size());
		assertTrue(index.getTopics(type).contains(topic));
		assertEquals(1, index.getTopics(otherType).size());
		assertTrue(index.getTopics(otherType).contains(topic));
		
		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		type_ = transaction.moveToTransactionContext(type);
		assertEquals(type, type_);
		otherType_ = transaction.moveToTransactionContext(otherType);
		assertEquals(otherType, otherType_);
		
		index = topicMap.getIndex(ITypeInstanceIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(ITypeInstanceIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		
		assertEquals(2, topic.getTypes().size());
		assertEquals(2, index.getTopicTypes().size());
		assertTrue(index.getTopicTypes().contains(type));
		assertTrue(index.getTopicTypes().contains(otherType));
		assertEquals(1, index.getTopics(type).size());
		assertTrue(index.getTopics(type).contains(topic));
		assertEquals(1, index.getTopics(otherType).size());
		assertTrue(index.getTopics(otherType).contains(topic));
		
		assertEquals(2, topic_.getTypes().size());
		assertEquals(2, index_.getTopicTypes().size());
		assertTrue(index_.getTopicTypes().contains(type_));
		assertTrue(index_.getTopicTypes().contains(otherType_));
		assertEquals(1, index_.getTopics(type_).size());
		assertTrue(index_.getTopics(type_).contains(topic_));
		assertEquals(1, index_.getTopics(otherType_).size());
		assertTrue(index_.getTopics(otherType_).contains(topic_));
		
		topic_.removeType(type_);	
		
		assertEquals(2, topic.getTypes().size());
		assertEquals(2, index.getTopicTypes().size());
		assertTrue(index.getTopicTypes().contains(type));
		assertTrue(index.getTopicTypes().contains(otherType));
		assertEquals(1, index.getTopics(type).size());
		assertTrue(index.getTopics(type).contains(topic));
		assertEquals(1, index.getTopics(otherType).size());
		assertTrue(index.getTopics(otherType).contains(topic));
		
		assertEquals(1, topic_.getTypes().size());
		assertEquals(1, index_.getTopicTypes().size());
		assertTrue(index_.getTopicTypes().contains(otherType_));
		assertEquals(0, index_.getTopics(type_).size());
		assertEquals(1, index_.getTopics(otherType_).size());
		assertTrue(index_.getTopics(otherType_).contains(topic_));
		
		transaction.commit();
		
		assertEquals(1, topic.getTypes().size());
		assertEquals(1, index.getTopicTypes().size());
		assertTrue(index.getTopicTypes().contains(otherType));
		assertEquals(0, index.getTopics(type).size());
		assertEquals(1, index.getTopics(otherType).size());
		assertTrue(index.getTopics(otherType).contains(topic));
	}
	
	public void testSupertypeModification() throws Exception {
		ITopic topic = createTopic();
		ITopic supertype = createTopic();
		ITopic otherSuperType = createTopic();
		
		assertEquals(0, topic.getSupertypes().size());
		
		ITransaction transaction = topicMap.createTransaction();
		ITopic topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		ITopic supertype_ = transaction.moveToTransactionContext(supertype);
		assertEquals(supertype, supertype_);
		ITopic otherSuperType_ = transaction.moveToTransactionContext(otherSuperType);
		assertEquals(otherSuperType, otherSuperType_);
		
		ISupertypeSubtypeIndex index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		ISupertypeSubtypeIndex index_ = transaction.getIndex(ISupertypeSubtypeIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		
		assertEquals(0, index.getSupertypes().size());
		assertEquals(0, index.getSubtypes(supertype).size());
		assertEquals(0, index.getSubtypes(otherSuperType).size());
		assertEquals(0, index_.getSupertypes().size());
		assertEquals(0, index_.getSubtypes(supertype_).size());
		assertEquals(0, index_.getSubtypes(otherSuperType_).size());
		
		topic_.addSupertype(supertype_);		
		assertEquals(0, topic.getSupertypes().size());
		assertEquals(1, topic_.getSupertypes().size());
		assertTrue(topic_.getSupertypes().contains(supertype_));
		
		assertEquals(0, index.getSupertypes().size());
		assertEquals(0, index.getSubtypes(supertype).size());
		assertEquals(0, index.getSubtypes(otherSuperType).size());
		assertEquals(1, index_.getSupertypes().size());
		assertTrue(index_.getSupertypes().contains(supertype_));
		assertEquals(1, index_.getSubtypes(supertype_).size());
		assertTrue(index_.getSubtypes(supertype_).contains(topic_));
		assertEquals(0, index_.getSubtypes(otherSuperType_).size());
		
		topic_.addSupertype(otherSuperType_);
		assertEquals(0, topic.getSupertypes().size());
		assertEquals(2, topic_.getSupertypes().size());
		assertTrue(topic_.getSupertypes().contains(supertype_));
		assertTrue(topic_.getSupertypes().contains(otherSuperType_));
		
		assertEquals(0, index.getSupertypes().size());
		assertEquals(0, index.getSubtypes(supertype).size());
		assertEquals(0, index.getSubtypes(otherSuperType).size());
		assertEquals(2, index_.getSupertypes().size());
		assertTrue(index_.getSupertypes().contains(supertype_));
		assertTrue(index_.getSupertypes().contains(otherSuperType_));
		assertEquals(1, index_.getSubtypes(supertype_).size());
		assertTrue(index_.getSubtypes(supertype_).contains(topic_));
		assertEquals(1, index_.getSubtypes(otherSuperType_).size());
		assertTrue(index_.getSubtypes(otherSuperType_).contains(topic_));
		
		transaction.rollback();
		assertEquals(0, topic.getSupertypes().size());
		assertEquals(0, index.getSupertypes().size());
		assertEquals(0, index.getSubtypes(supertype).size());
		assertEquals(0, index.getSubtypes(otherSuperType).size());
		
		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		supertype_ = transaction.moveToTransactionContext(supertype);
		assertEquals(supertype, supertype_);
		otherSuperType_ = transaction.moveToTransactionContext(otherSuperType);
		assertEquals(otherSuperType, otherSuperType_);
		
		index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(ISupertypeSubtypeIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		
		assertEquals(0, index.getSupertypes().size());
		assertEquals(0, index.getSubtypes(supertype).size());
		assertEquals(0, index.getSubtypes(otherSuperType).size());
		assertEquals(0, index_.getSupertypes().size());
		assertEquals(0, index_.getSubtypes(supertype_).size());
		assertEquals(0, index_.getSubtypes(otherSuperType_).size());
		
		topic_.addSupertype(supertype_);		
		assertEquals(0, topic.getSupertypes().size());
		assertEquals(1, topic_.getSupertypes().size());
		assertTrue(topic_.getSupertypes().contains(supertype_));
		
		assertEquals(0, index.getSupertypes().size());
		assertEquals(0, index.getSubtypes(supertype).size());
		assertEquals(0, index.getSubtypes(otherSuperType).size());
		assertEquals(1, index_.getSupertypes().size());
		assertTrue(index_.getSupertypes().contains(supertype_));
		assertEquals(1, index_.getSubtypes(supertype_).size());
		assertTrue(index_.getSubtypes(supertype_).contains(topic_));
		assertEquals(0, index_.getSubtypes(otherSuperType_).size());
		
		topic_.addSupertype(otherSuperType_);
		assertEquals(0, topic.getSupertypes().size());
		assertEquals(2, topic_.getSupertypes().size());
		assertTrue(topic_.getSupertypes().contains(supertype_));
		assertTrue(topic_.getSupertypes().contains(otherSuperType_));
		
		assertEquals(0, index.getSupertypes().size());
		assertEquals(0, index.getSubtypes(supertype).size());
		assertEquals(0, index.getSubtypes(otherSuperType).size());
		assertEquals(2, index_.getSupertypes().size());
		assertTrue(index_.getSupertypes().contains(supertype_));
		assertTrue(index_.getSupertypes().contains(otherSuperType_));
		assertEquals(1, index_.getSubtypes(supertype_).size());
		assertTrue(index_.getSubtypes(supertype_).contains(topic_));
		assertEquals(1, index_.getSubtypes(otherSuperType_).size());
		assertTrue(index_.getSubtypes(otherSuperType_).contains(topic_));
		
		transaction.commit();
		assertEquals(2, topic.getSupertypes().size());
		assertEquals(2, index.getSupertypes().size());
		assertTrue(index.getSupertypes().contains(supertype));
		assertTrue(index.getSupertypes().contains(otherSuperType));
		assertEquals(1, index.getSubtypes(supertype).size());
		assertTrue(index.getSubtypes(supertype).contains(topic));
		assertEquals(1, index.getSubtypes(otherSuperType).size());
		assertTrue(index.getSubtypes(otherSuperType).contains(topic));
		
		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		supertype_ = transaction.moveToTransactionContext(supertype);
		assertEquals(supertype, supertype_);
		otherSuperType_ = transaction.moveToTransactionContext(otherSuperType);
		assertEquals(otherSuperType, otherSuperType_);
		
		index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(ISupertypeSubtypeIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		
		assertEquals(2, topic.getSupertypes().size());
		assertEquals(2, index.getSupertypes().size());
		assertTrue(index.getSupertypes().contains(supertype));
		assertTrue(index.getSupertypes().contains(otherSuperType));
		assertEquals(1, index.getSubtypes(supertype).size());
		assertTrue(index.getSubtypes(supertype).contains(topic));
		assertEquals(1, index.getSubtypes(otherSuperType).size());
		assertTrue(index.getSubtypes(otherSuperType).contains(topic));
		
		assertEquals(2, topic_.getSupertypes().size());
		assertEquals(2, index_.getSupertypes().size());
		assertTrue(index_.getSupertypes().contains(supertype_));
		assertTrue(index_.getSupertypes().contains(otherSuperType_));
		assertEquals(1, index_.getSubtypes(supertype_).size());
		assertTrue(index_.getSubtypes(supertype_).contains(topic_));
		assertEquals(1, index_.getSubtypes(otherSuperType_).size());
		assertTrue(index_.getSubtypes(otherSuperType_).contains(topic_));
		
		topic_.removeSupertype(supertype_);	
		
		assertEquals(2, topic.getSupertypes().size());
		assertEquals(2, index.getSupertypes().size());
		assertTrue(index.getSupertypes().contains(supertype));
		assertTrue(index.getSupertypes().contains(otherSuperType));
		assertEquals(1, index.getSubtypes(supertype).size());
		assertTrue(index.getSubtypes(supertype).contains(topic));
		assertEquals(1, index.getSubtypes(otherSuperType).size());
		assertTrue(index.getSubtypes(otherSuperType).contains(topic));
		
		assertEquals(1, topic_.getSupertypes().size());
		assertEquals(1, index_.getSupertypes().size());
		assertTrue(index_.getSupertypes().contains(otherSuperType_));
		assertEquals(0, index_.getSubtypes(supertype_).size());
		assertEquals(1, index_.getSubtypes(otherSuperType_).size());
		assertTrue(index_.getSubtypes(otherSuperType_).contains(topic_));
		
		transaction.rollback();
		
		assertEquals(2, topic.getSupertypes().size());
		assertEquals(2, index.getSupertypes().size());
		assertTrue(index.getSupertypes().contains(supertype));
		assertTrue(index.getSupertypes().contains(otherSuperType));
		assertEquals(1, index.getSubtypes(supertype).size());
		assertTrue(index.getSubtypes(supertype).contains(topic));
		assertEquals(1, index.getSubtypes(otherSuperType).size());
		assertTrue(index.getSubtypes(otherSuperType).contains(topic));
		
		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		assertEquals(topic, topic_);
		supertype_ = transaction.moveToTransactionContext(supertype);
		assertEquals(supertype, supertype_);
		otherSuperType_ = transaction.moveToTransactionContext(otherSuperType);
		assertEquals(otherSuperType, otherSuperType_);
		
		index = topicMap.getIndex(ISupertypeSubtypeIndex.class);
		if ( !index.isOpen()){
			index.open();
		}
		index_ = transaction.getIndex(ISupertypeSubtypeIndex.class);
		if ( !index_.isOpen()){
			index_.open();
		}
		
		assertEquals(2, topic.getSupertypes().size());
		assertEquals(2, index.getSupertypes().size());
		assertTrue(index.getSupertypes().contains(supertype));
		assertTrue(index.getSupertypes().contains(otherSuperType));
		assertEquals(1, index.getSubtypes(supertype).size());
		assertTrue(index.getSubtypes(supertype).contains(topic));
		assertEquals(1, index.getSubtypes(otherSuperType).size());
		assertTrue(index.getSubtypes(otherSuperType).contains(topic));
		
		assertEquals(2, topic_.getSupertypes().size());
		assertEquals(2, index_.getSupertypes().size());
		assertTrue(index_.getSupertypes().contains(supertype_));
		assertTrue(index_.getSupertypes().contains(otherSuperType_));
		assertEquals(1, index_.getSubtypes(supertype_).size());
		assertTrue(index_.getSubtypes(supertype_).contains(topic_));
		assertEquals(1, index_.getSubtypes(otherSuperType_).size());
		assertTrue(index_.getSubtypes(otherSuperType_).contains(topic_));
		
		topic_.removeSupertype(supertype_);	
		
		assertEquals(2, topic.getSupertypes().size());
		assertEquals(2, index.getSupertypes().size());
		assertTrue(index.getSupertypes().contains(supertype));
		assertTrue(index.getSupertypes().contains(otherSuperType));
		assertEquals(1, index.getSubtypes(supertype).size());
		assertTrue(index.getSubtypes(supertype).contains(topic));
		assertEquals(1, index.getSubtypes(otherSuperType).size());
		assertTrue(index.getSubtypes(otherSuperType).contains(topic));
		
		assertEquals(1, topic_.getSupertypes().size());
		assertEquals(1, index_.getSupertypes().size());
		assertTrue(index_.getSupertypes().contains(otherSuperType_));
		assertEquals(0, index_.getSubtypes(supertype_).size());
		assertEquals(1, index_.getSubtypes(otherSuperType_).size());
		assertTrue(index_.getSubtypes(otherSuperType_).contains(topic_));
		
		transaction.commit();
		
		assertEquals(1, topic.getSupertypes().size());
		assertEquals(1, index.getSupertypes().size());
		assertTrue(index.getSupertypes().contains(otherSuperType));
		assertEquals(0, index.getSubtypes(supertype).size());
		assertEquals(1, index.getSubtypes(otherSuperType).size());
		assertTrue(index.getSubtypes(otherSuperType).contains(topic));
	}
	
}
