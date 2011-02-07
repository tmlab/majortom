package de.topicmapslab.majortom.tests.transaction;

import org.tmapi.core.Topic;

import de.topicmapslab.majortom.model.core.IDatatypeAware;
import de.topicmapslab.majortom.model.core.ILocator;
import de.topicmapslab.majortom.model.core.IName;
import de.topicmapslab.majortom.model.core.IOccurrence;
import de.topicmapslab.majortom.model.core.ITopic;
import de.topicmapslab.majortom.model.core.IVariant;
import de.topicmapslab.majortom.model.index.ILiteralIndex;
import de.topicmapslab.majortom.model.namespace.Namespaces;
import de.topicmapslab.majortom.model.transaction.ITransaction;
import de.topicmapslab.majortom.tests.MaJorToMTestCase;

public class TestCharacteristicTransactions extends MaJorToMTestCase {

	public void testOccurrences() throws Exception {

		final ILocator xsdString = (ILocator) topicMap.createLocator(Namespaces.XSD.STRING);

		ITopic topic = createTopic();
		IOccurrence occurrence = (IOccurrence) topic.createOccurrence(createTopic(), "val", new Topic[0]);
		_testDatatypeAware(occurrence);

		ITransaction transaction = topicMap.createTransaction();
		ITopic topic_ = transaction.moveToTransactionContext(topic);
		IOccurrence occurrence_ = transaction.moveToTransactionContext(occurrence);
		assertEquals(topic, topic_);

		IOccurrence otherOccurrence_ = (IOccurrence) topic_.createOccurrence(transaction.createTopic(), "Value2", new Topic[0]);

		assertEquals(1, topic.getOccurrences().size());
		assertTrue(topic.getOccurrences().contains(occurrence));
		assertEquals(2, topic_.getOccurrences().size());
		assertTrue(topic_.getOccurrences().contains(occurrence_));
		assertTrue(topic_.getOccurrences().contains(otherOccurrence_));

		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		ILiteralIndex index_ = transaction.getIndex(ILiteralIndex.class);
		if (!index_.isOpen()) {
			index_.open();
		}
		assertEquals(1, index_.getDatatypeAwares(xsdString).size());
		assertTrue(index_.getDatatypeAwares(xsdString).contains(otherOccurrence_));
		assertEquals(0, index.getDatatypeAwares(xsdString).size());

		transaction.rollback();

		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		occurrence_ = transaction.moveToTransactionContext(occurrence);
		assertEquals(topic, topic_);

		otherOccurrence_ = (IOccurrence) topic_.createOccurrence(transaction.createTopic(), "Value2", new Topic[0]);

		assertEquals(1, topic.getOccurrences().size());
		assertTrue(topic.getOccurrences().contains(occurrence));
		assertEquals(2, topic_.getOccurrences().size());
		assertTrue(topic_.getOccurrences().contains(occurrence_));
		assertTrue(topic_.getOccurrences().contains(otherOccurrence_));

		index = topicMap.getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		index_ = transaction.getIndex(ILiteralIndex.class);
		if (!index_.isOpen()) {
			index_.open();
		}
		assertEquals(1, index_.getDatatypeAwares(xsdString).size());
		assertTrue(index_.getDatatypeAwares(xsdString).contains(otherOccurrence_));
		assertEquals(0, index.getDatatypeAwares(xsdString).size());

		transaction.commit();

		assertEquals(2, topic.getOccurrences().size());
		assertTrue(topic.getOccurrences().contains(occurrence));
		assertEquals(1, index.getDatatypeAwares(xsdString).size());
	}

	public void testNames() throws Exception {

		ITopic topic = createTopic();
		IName name = (IName) topic.createName(createTopic(), "val", new Topic[0]);

		ITransaction transaction = topicMap.createTransaction();
		ITopic topic_ = transaction.moveToTransactionContext(topic);
		IName name_ = transaction.moveToTransactionContext(name);
		assertEquals(topic, topic_);

		IName otherName_ = (IName) topic_.createName(transaction.createTopic(), "Value2", new Topic[0]);

		assertEquals(1, topic.getNames().size());
		assertTrue(topic.getNames().contains(name));
		assertEquals(2, topic_.getNames().size());
		assertTrue(topic_.getNames().contains(name_));
		assertTrue(topic_.getNames().contains(otherName_));

		name_.setValue("LaLa");
		assertEquals(name.getValue(), "val");
		assertEquals(name_.getValue(), "LaLa");

		transaction.rollback();

		transaction = topicMap.createTransaction();
		topic_ = transaction.moveToTransactionContext(topic);
		name_ = transaction.moveToTransactionContext(name);
		assertEquals(topic, topic_);

		otherName_ = (IName) topic_.createName(transaction.createTopic(), "Value2", new Topic[0]);

		assertEquals(1, topic.getNames().size());
		assertTrue(topic.getNames().contains(name));
		assertEquals(2, topic_.getNames().size());
		assertTrue(topic_.getNames().contains(name_));
		assertTrue(topic_.getNames().contains(otherName_));

		name_.setValue("LaLa");
		assertEquals(name.getValue(), "val");
		assertEquals(name_.getValue(), "LaLa");

		transaction.commit();

		assertEquals(2, topic.getNames().size());
		assertTrue(topic.getNames().contains(name));
		assertEquals(name.getValue(), "LaLa");

	}

	public void testVariants() throws Exception {

		final ILocator xsdString = (ILocator) topicMap.createLocator(Namespaces.XSD.STRING);

		ITopic topic = createTopic();
		IName name = (IName) topic.createName("val", new Topic[0]);
		IVariant variant = (IVariant) name.createVariant("val", createTopic());
		_testDatatypeAware(variant);

		ITransaction transaction = topicMap.createTransaction();
		IName name_ = transaction.moveToTransactionContext(name);
		IVariant variant_ = transaction.moveToTransactionContext(variant);
		assertEquals(name, name_);

		IVariant otherVariant_ = (IVariant) name_.createVariant("val 2", transaction.createTopic());

		assertEquals(1, name.getVariants().size());
		assertTrue(name.getVariants().contains(variant));
		assertEquals(2, name_.getVariants().size());
		assertTrue(name_.getVariants().contains(variant_));
		assertTrue(name_.getVariants().contains(otherVariant_));

		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		ILiteralIndex index_ = transaction.getIndex(ILiteralIndex.class);
		if (!index_.isOpen()) {
			index_.open();
		}
		assertEquals(1, index_.getDatatypeAwares(xsdString).size());
		assertTrue(index_.getDatatypeAwares(xsdString).contains(otherVariant_));
		assertEquals(0, index.getDatatypeAwares(xsdString).size());

		transaction.rollback();

		transaction = topicMap.createTransaction();
		name_ = transaction.moveToTransactionContext(name);
		variant_ = transaction.moveToTransactionContext(variant);
		assertEquals(name, name_);

		otherVariant_ = (IVariant) name_.createVariant("val 2", transaction.createTopic());

		assertEquals(1, name.getVariants().size());
		assertTrue(name.getVariants().contains(variant));
		assertEquals(2, name_.getVariants().size());
		assertTrue(name_.getVariants().contains(variant_));
		assertTrue(name_.getVariants().contains(otherVariant_));

		index = topicMap.getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		index_ = transaction.getIndex(ILiteralIndex.class);
		if (!index_.isOpen()) {
			index_.open();
		}
		assertEquals(1, index_.getDatatypeAwares(xsdString).size());
		assertTrue(index_.getDatatypeAwares(xsdString).contains(otherVariant_));
		assertEquals(0, index.getDatatypeAwares(xsdString).size());

		transaction.commit();

		assertEquals(2, name.getVariants().size());
		assertTrue(name.getVariants().contains(variant));
		assertEquals(1, index.getDatatypeAwares(xsdString).size());
	}

	public void _testDatatypeAware(IDatatypeAware d) throws Exception {

		final ILocator xsdString = (ILocator) topicMap.createLocator(Namespaces.XSD.STRING);
		final ILocator xsdInt = (ILocator) topicMap.createLocator(Namespaces.XSD.INT);

		ITransaction transaction = topicMap.createTransaction();
		IDatatypeAware d_ = transaction.moveToTransactionContext(d);
		d_.setValue(1);
		assertEquals(d.getValue(), "val");
		assertEquals(d.getDatatype(), xsdString);
		assertEquals(d_.getValue(), "1");
		assertEquals(d_.getDatatype(), xsdInt);

		ILiteralIndex index = topicMap.getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		ILiteralIndex index_ = transaction.getIndex(ILiteralIndex.class);
		if (!index_.isOpen()) {
			index_.open();
		}
		assertEquals(1, index_.getDatatypeAwares(xsdInt).size());
		assertTrue(index_.getDatatypeAwares(xsdInt).contains(d_));
		assertEquals(0, index_.getDatatypeAwares(xsdString).size());

		assertEquals(0, index.getDatatypeAwares(xsdInt).size());
		assertEquals(1, index.getDatatypeAwares(xsdString).size());
		assertTrue(index.getDatatypeAwares(xsdString).contains(d));

		transaction.rollback();

		assertEquals(0, index.getDatatypeAwares(xsdInt).size());
		assertEquals(1, index.getDatatypeAwares(xsdString).size());
		assertTrue(index.getDatatypeAwares(xsdString).contains(d));
		assertEquals(d.getValue(), "val");
		assertEquals(d.getDatatype(), xsdString);

		transaction = topicMap.createTransaction();
		d_ = transaction.moveToTransactionContext(d);
		d_.setValue(1);
		assertEquals(d.getValue(), "val");
		assertEquals(d.getDatatype(), xsdString);
		assertEquals(d_.getValue(), "1");
		assertEquals(d_.getDatatype(), xsdInt);

		index = topicMap.getIndex(ILiteralIndex.class);
		if (!index.isOpen()) {
			index.open();
		}

		index_ = transaction.getIndex(ILiteralIndex.class);
		if (!index_.isOpen()) {
			index_.open();
		}
		assertEquals(1, index_.getDatatypeAwares(xsdInt).size());
		assertTrue(index_.getDatatypeAwares(xsdInt).contains(d_));
		assertEquals(0, index_.getDatatypeAwares(xsdString).size());

		assertEquals(0, index.getDatatypeAwares(xsdInt).size());
		assertEquals(1, index.getDatatypeAwares(xsdString).size());
		assertTrue(index.getDatatypeAwares(xsdString).contains(d));

		transaction.commit();

		assertEquals(1, index.getDatatypeAwares(xsdInt).size());
		assertTrue(index.getDatatypeAwares(xsdInt).contains(d));
		assertEquals(0, index.getDatatypeAwares(xsdString).size());
		assertEquals(d.getValue(), "1");
		assertEquals(d.getDatatype(), xsdInt);
	}

}
