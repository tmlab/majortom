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
package de.topicmapslab.majortom.model.exception;

/**
 * Exception implementation thrown by the topic map store if the store is
 * read-only and a modification method was called.
 * 
 * @author Sven Krosse
 * 
 */
public class UnmodifyableStoreException extends TopicMapStoreException {

	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 * 
	 * @param msg the message containing some additional information about the
	 *            cause
	 */
	public UnmodifyableStoreException(String msg) {
		super(msg);
	}

	/**
	 * constructor
	 * 
	 * @param cause the cause of this exception
	 */
	public UnmodifyableStoreException(Throwable cause) {
		super(cause);
	}

	/**
	 * constructor
	 * 
	 * @param msg the message containing some additional information about the
	 *            cause
	 * @param cause the cause of this exception
	 */
	public UnmodifyableStoreException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
