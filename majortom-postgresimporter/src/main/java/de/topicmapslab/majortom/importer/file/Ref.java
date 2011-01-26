/**
 * 
 */
package de.topicmapslab.majortom.importer.file;

import com.semagia.mio.IRef;

/**
 * @author Sven
 * 
 */
public class Ref implements IRef {

	private final String iri;
	private final int type;

	/**
	 * constructor
	 * 
	 * @param iri
	 *            the IRI
	 * @param type
	 *            the type
	 */
	public Ref(String iri, int type) {
		this.iri = iri;
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getIRI() {
		return iri;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getType() {
		return type;
	}

}
