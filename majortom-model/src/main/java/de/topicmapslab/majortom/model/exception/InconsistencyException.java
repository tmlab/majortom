package de.topicmapslab.majortom.model.exception;

/**
 * Exception thrown by an index instance of data used is out of date.
 * 
 * @author Sven Krosse
 * 
 */
public class InconsistencyException extends IndexException {

	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 * 
	 * @param msg
	 *            the message containing some additional information about the
	 *            cause
	 */
	public InconsistencyException(String msg) {
		super(msg);
	}

	/**
	 * constructor
	 * 
	 * @param cause
	 *            the cause of this exception
	 */
	public InconsistencyException(Throwable cause) {
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
	public InconsistencyException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
