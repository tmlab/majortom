package de.topicmapslab.majortom.model.exception;

/**
 * Base exception thrown by the topic maps engine in the context of index operations.
 * 
 * @author Sven Krosse
 * 
 */
public class IndexException extends EngineException {

	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 * 
	 * @param msg
	 *            the message containing some additional information about the
	 *            cause
	 */
	public IndexException(String msg) {
		super(msg);
	}

	/**
	 * constructor
	 * 
	 * @param cause
	 *            the cause of this exception
	 */
	public IndexException(Throwable cause) {
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
	public IndexException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
