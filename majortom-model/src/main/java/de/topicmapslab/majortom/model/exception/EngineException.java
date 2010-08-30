package de.topicmapslab.majortom.model.exception;

import org.tmapi.core.TMAPIRuntimeException;

/**
 * Base exception thrown by the topic maps engine.
 * 
 * @author Sven Krosse
 * 
 */
public class EngineException extends TMAPIRuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 * 
	 * @param msg
	 *            the message containing some additional information about the
	 *            cause
	 */
	public EngineException(String msg) {
		super(msg);
	}

	/**
	 * constructor
	 * 
	 * @param cause
	 *            the cause of this exception
	 */
	public EngineException(Throwable cause) {
		super(cause);
	}

	/**
	 * constructor
	 * 
	 * @param msg
	 *            the message containing some additional information about the
	 *            cause
	 * @param cause
	 *            the cause of this exception
	 */
	public EngineException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
