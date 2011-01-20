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

package de.topicmapslab.majortom.core;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.UUID;

import org.tmapi.core.Locator;
import org.tmapi.core.MalformedIRIException;

import de.topicmapslab.majortom.model.core.ILocator;

/**
 * Base Implementation of {@link ILocator}.
 * 
 * @author Sven Krosse
 * 
 */
public class LocatorImpl implements ILocator, Comparable<LocatorImpl>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2475883158229059516L;
	private final URI uri;
	private final String reference;
	private final String id;
	
	/**
	 * constructor
	 * 
	 * @param reference
	 *            the reference
	 */
	public LocatorImpl(String reference) throws MalformedIRIException {
		this(reference, "");
	}

	/**
	 * constructor
	 * 
	 * @param reference
	 *            the reference
	 */
	public LocatorImpl(final String reference, final String id) throws MalformedIRIException {
		if (reference == null || reference.isEmpty()) {
			throw new MalformedIRIException("The given IRI reference is invalid.");
		}
		try {			
			String tmp = reference.replace("+", "%2B");
			this.reference = URLDecoder.decode(tmp, "utf-8"); 
			uri = new URI(tmp.replace(" ", "%20"));
		} catch (URISyntaxException e) {
			throw new MalformedIRIException("The given IRI reference '" + reference +"' is invalid.");
		} catch (UnsupportedEncodingException e) {
			throw new MalformedIRIException("The given IRI reference '" + reference +"'is invalid.");
		}

		if (!uri.isAbsolute()) {
			throw new MalformedIRIException("Relative URI");
		}
		this.id = id;
	}

	/**
	 * constructor
	 * 
	 * @param uri
	 *            the URI
	 */
	public LocatorImpl(final URI uri) throws MalformedIRIException {
		try {
			this.reference = URLDecoder.decode(uri.toASCIIString(), "utf-8");
			this.uri = uri;
		} catch (UnsupportedEncodingException e) {
			throw new MalformedIRIException("The given IRI reference is invalid.");
		}
		this.id = UUID.randomUUID().toString();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Locator) {
			return getReference().equals(((Locator) obj).getReference());
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public URI toUri() throws URISyntaxException {
		return uri;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getReference() {
		return reference;
	}

	/**
	 * {@inheritDoc}
	 */
	public Locator resolve(String reference) throws MalformedIRIException {
		return new LocatorImpl(uri.resolve(reference.replace(" ", "%20")));
	}

	/**
	 * {@inheritDoc}
	 */
	public String toExternalForm() {
		return uri.toASCIIString();
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return reference;
	}

	/**
	 * {@inheritDoc}
	 */
	public int hashCode() {
		return reference.hashCode();
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	public int compareTo(LocatorImpl o) {
		return getReference().compareTo(o.getReference());
	}
}
